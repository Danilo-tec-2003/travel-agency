package com.messaging.travel.booking_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBookingRequest(
        @NotBlank(message = "Customer name is required")
        @Size(min = 3, max = 80, message = "Customer name must be between 3 and 80 characters")
        @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Customer name must contain only letters and spaces")
        String customerName,

        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be valid")
        @Size(max = 120, message = "Customer email must be at most 120 characters")
        String customerEmail,

        @NotBlank(message = "Destination is required")
        @Size(min = 3, max = 80, message = "Destination must be between 3 and 80 characters")
        String destination,

        @NotNull(message = "Travelers is required")
        @Min(value = 1, message = "Travelers must be at least 1")
        @Max(value = 10, message = "Travelers must be at most 10")
        Integer travelers
) {
}
