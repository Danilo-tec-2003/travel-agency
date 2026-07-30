package com.messaging.travel.booking_service.controller;

import com.messaging.travel.booking_service.domain.Booking;
import com.messaging.travel.booking_service.dto.CreateBookingRequest;
import com.messaging.travel.booking_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestBody @Valid CreateBookingRequest request) {
        return bookingService.create(request);
    }
}
