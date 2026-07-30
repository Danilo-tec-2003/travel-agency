package com.messaging.travel.booking_service.service;

import com.messaging.travel.booking_service.config.RabbitMQConfig;
import com.messaging.travel.booking_service.domain.Booking;
import com.messaging.travel.booking_service.dto.CreateBookingRequest;
import com.messaging.travel.booking_service.event.BookingCreatedEvent;
import com.messaging.travel.booking_service.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;

    public Booking create(CreateBookingRequest request) {
        Booking booking = new Booking(
                request.customerName(),
                request.destination(),
                request.travelers()
        );

        Booking savedBooking = bookingRepository.save(booking);

        BookingCreatedEvent event = new BookingCreatedEvent(
                UUID.randomUUID(),
                savedBooking.getId(),
                savedBooking.getCustomerName(),
                savedBooking.getDestination(),
                savedBooking.getTravelers()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRAVEL_EXCHANGE,
                "booking.created",
                event
        );

        return savedBooking;
    }
}
