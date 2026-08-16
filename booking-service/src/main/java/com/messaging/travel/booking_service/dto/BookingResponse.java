package com.messaging.travel.booking_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        String customerName,
        String destination,
        Integer travelers,
        String status,
        String message,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
