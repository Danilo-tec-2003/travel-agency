package com.messaging.travel.reservation_service.event;

import java.util.UUID;

public record BookingCreatedEvent(
        UUID eventId,
        UUID bookingId,
        String customerName,
        String destination,
        Integer travelers
) {
}
