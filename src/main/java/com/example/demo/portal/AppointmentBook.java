package com.example.demo.portal;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** In-memory appointment book with slot-clash detection. */
public final class AppointmentBook {
    static final Set<String> SERVICE_TYPES = Set.of("annual-service", "tyres", "battery-check", "software-update");

    private final Map<String, Appointment> byId = new ConcurrentHashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    public synchronized Appointment book(Appointment request) {
        if (request.getVehicleId() == null || request.getServiceCentre() == null) {
            throw new IllegalArgumentException("vehicle_id and service_centre are required");
        }
        if (!SERVICE_TYPES.contains(request.getServiceType())) {
            throw new IllegalArgumentException("unknown service_type: " + request.getServiceType());
        }
        try {
            LocalDateTime.parse(request.getSlotStart());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("slot_start must be an ISO local date-time");
        }
        boolean clash = byId.values().stream().anyMatch(a ->
                a.getServiceCentre().equals(request.getServiceCentre()) && a.getSlotStart().equals(request.getSlotStart()));
        if (clash) {
            throw new IllegalStateException("slot already booked at " + request.getServiceCentre());
        }
        String id = String.format("SA-%05d", seq.getAndIncrement());
        Appointment stored = new Appointment(id, request.getVehicleId(), request.getServiceCentre(),
                request.getSlotStart(), request.getServiceType(), request.getCustomerNote());
        byId.put(id, stored);
        return stored;
    }

    public Optional<Appointment> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    public List<Appointment> forCentre(String centre) {
        List<Appointment> out = new ArrayList<>();
        for (Appointment a : byId.values()) {
            if (a.getServiceCentre().equals(centre)) {
                out.add(a);
            }
        }
        out.sort(Comparator.comparing(Appointment::getSlotStart));
        return out;
    }

    public boolean cancel(String id) {
        return byId.remove(id) != null;
    }
}
