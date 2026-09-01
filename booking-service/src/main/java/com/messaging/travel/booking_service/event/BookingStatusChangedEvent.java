package com.messaging.travel.booking_service.event;

import java.util.UUID;

public record BookingStatusChangedEvent(
        UUID eventId,
        UUID bookingId,
        String customerName,
        String customerEmail,
        String destination,
        String status,
        String message
) {
}
