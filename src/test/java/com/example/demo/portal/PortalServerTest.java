package com.example.demo.portal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PortalServerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private PortalServer server;
    private String base;

    @BeforeEach
    void start() throws Exception {
        server = new PortalServer(new AppointmentBook());
        base = "http://127.0.0.1:" + server.start(0);
    }

    @AfterEach
    void stop() {
        server.stop();
    }

    private HttpResponse<String> post(String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create(base + "/appointments"))
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void usesSnakeCaseJsonContract() throws Exception {
        HttpResponse<String> res = post("{\"vehicle_id\":\"VH-7\",\"service_centre\":\"north-centre\","
                + "\"slot_start\":\"2026-05-02T10:30\",\"service_type\":\"battery-check\"}");
        assertEquals(201, res.statusCode());
        JsonNode node = new ObjectMapper().readTree(res.body());
        assertEquals("SA-00001", node.get("appointment_id").asText());
        assertEquals("north-centre", node.get("service_centre").asText());
        assertTrue(node.has("slot_start"));
    }

    @Test
    void returns404ForUnknownAppointment() throws Exception {
        HttpResponse<String> res = client.send(HttpRequest.newBuilder(URI.create(base + "/appointments/SA-99999")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(404, res.statusCode());
    }

    @Test
    void returns400ForInvalidRequest() throws Exception {
        assertEquals(400, post("{\"vehicle_id\":\"VH-7\",\"service_centre\":\"north-centre\","
                + "\"slot_start\":\"later\",\"service_type\":\"tyres\"}").statusCode());
    }
}
