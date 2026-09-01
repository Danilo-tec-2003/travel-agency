package com.messaging.travel.booking_service.event;

import java.util.UUID;

public record BookingCreatedEvent(
        UUID eventId,
        UUID bookingId,
        String customerName,
        String customerEmail,
        String destination,
        Integer travelers
) {
}
