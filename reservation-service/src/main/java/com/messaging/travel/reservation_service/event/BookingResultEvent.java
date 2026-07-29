package com.messaging.travel.reservation_service.event;

import java.util.UUID;

public record BookingResultEvent(
        UUID eventId,
        UUID bookingId,
        String status,
        String message
) {
}
