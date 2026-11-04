package com.example.demo.portal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/** Minimal JSON HTTP API over the JDK's built-in server. */
public final class PortalServer {
    private final AppointmentBook book;
    private final ObjectMapper mapper = JsonSupport.mapper();
    private HttpServer server;

    public PortalServer(AppointmentBook book) {
        this.book = book;
    }

    public int start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/health", ex -> send(ex, 200, Map.of("status", "ok")));
        server.createContext("/appointments", this::appointments);
        server.start();
        return server.getAddress().getPort();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void appointments(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            if ("POST".equals(ex.getRequestMethod()) && path.equals("/appointments")) {
                Appointment req = mapper.readValue(ex.getRequestBody(), Appointment.class);
                send(ex, 201, book.book(req));
            } else if ("GET".equals(ex.getRequestMethod()) && path.startsWith("/appointments/")) {
                String id = path.substring("/appointments/".length());
                Object body = book.find(id).<Object>map(a -> a).orElse(Map.of("error", "not found"));
                send(ex, book.find(id).isPresent() ? 200 : 404, body);
            } else {
                send(ex, 405, Map.of("error", "method not allowed"));
            }
        } catch (IllegalArgumentException e) {
            send(ex, 400, Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            send(ex, 409, Map.of("error", e.getMessage()));
        }
    }

    private void send(HttpExchange ex, int status, Object body) throws IOException {
        byte[] bytes = mapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = new PortalServer(new AppointmentBook()).start(Integer.parseInt(System.getProperty("port", "8080")));
        System.out.println("demo-service-portal listening on :" + port);
    }
}
