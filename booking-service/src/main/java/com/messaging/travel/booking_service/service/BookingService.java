package com.messaging.travel.booking_service.service;

import com.messaging.travel.booking_service.config.RabbitMQConfig;
import com.messaging.travel.booking_service.domain.Booking;
import com.messaging.travel.booking_service.domain.BookingStatus;
import com.messaging.travel.booking_service.dto.CreateBookingRequest;
import com.messaging.travel.booking_service.event.BookingCreatedEvent;
import com.messaging.travel.booking_service.event.BookingStatusChangedEvent;
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

    public void updateStatusFromResult(UUID bookingId, String status, String message) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        if ("RESERVED".equals(status)) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
        }

        booking.setMessage(message);

        bookingRepository.save(booking);

        BookingStatusChangedEvent event = new BookingStatusChangedEvent(
                UUID.randomUUID(),
                booking.getId(),
                booking.getCustomerName(),
                booking.getDestination(),
                booking.getStatus().name(),
                booking.getMessage()
        );

        String routingKey = BookingStatus.CONFIRMED.equals(booking.getStatus())
                ? RabbitMQConfig.BOOKING_CONFIRMED_ROUTING_KEY
                : RabbitMQConfig.BOOKING_CANCELLED_ROUTING_KEY;

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRAVEL_EXCHANGE,
                routingKey,
                event
        );
    }
}
