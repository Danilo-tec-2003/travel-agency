package com.messaging.travel.booking_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBookingRequest(
        @NotBlank
        String customerName,

        @NotBlank
        String destination,

        @NotNull
        @Positive
        Integer travelers
) {
}
