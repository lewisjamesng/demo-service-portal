package com.example.demo.portal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AppointmentBookTest {
    private static Appointment req(String centre, String slot) {
        return new Appointment(null, "VH-100", centre, slot, "tyres", "synthetic note");
    }

    @Test
    void booksAndFindsAppointment() {
        AppointmentBook book = new AppointmentBook();
        Appointment a = book.book(req("north-centre", "2026-04-01T09:00"));
        assertEquals("SA-00001", a.getAppointmentId());
        assertTrue(book.find("SA-00001").isPresent());
    }

    @Test
    void rejectsDoubleBookingOfSameSlot() {
        AppointmentBook book = new AppointmentBook();
        book.book(req("north-centre", "2026-04-01T09:00"));
        assertThrows(IllegalStateException.class, () -> book.book(req("north-centre", "2026-04-01T09:00")));
        book.book(req("south-centre", "2026-04-01T09:00"));
        assertEquals(1, book.forCentre("south-centre").size());
    }

    @Test
    void validatesServiceTypeAndSlot() {
        AppointmentBook book = new AppointmentBook();
        Appointment bad = req("north-centre", "2026-04-01T09:00");
        bad.setServiceType("paint");
        assertThrows(IllegalArgumentException.class, () -> book.book(bad));
        assertThrows(IllegalArgumentException.class, () -> book.book(req("north-centre", "tomorrow")));
    }
}
