package com.messaging.travel.notification_service.event;

import java.util.UUID;

public record BookingStatusChangedEvent(
        UUID eventId,
        UUID bookingId,
        String customerName,
        String destination,
        String status,
        String message
) {
}
