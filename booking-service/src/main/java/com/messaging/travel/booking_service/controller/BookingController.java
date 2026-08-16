package com.messaging.travel.booking_service.controller;

import com.messaging.travel.booking_service.dto.BookingResponse;
import com.messaging.travel.booking_service.dto.CreateBookingRequest;
import com.messaging.travel.booking_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@RequestBody @Valid CreateBookingRequest request) {
        return bookingService.create(request);
    }

    @GetMapping
    public List<BookingResponse> findAll() {
        return bookingService.findAll();
    }

    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable UUID id) {
        return bookingService.findById(id);
    }
}
