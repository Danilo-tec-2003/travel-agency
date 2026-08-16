package com.messaging.travel.booking_service.service;

import com.messaging.travel.booking_service.config.RabbitMQConfig;
import com.messaging.travel.booking_service.domain.Booking;
import com.messaging.travel.booking_service.domain.BookingStatus;
import com.messaging.travel.booking_service.domain.ProcessedEvent;
import com.messaging.travel.booking_service.domain.ProcessedEventType;
import com.messaging.travel.booking_service.dto.BookingResponse;
import com.messaging.travel.booking_service.dto.CreateBookingRequest;
import com.messaging.travel.booking_service.event.BookingCreatedEvent;
import com.messaging.travel.booking_service.event.BookingResultEvent;
import com.messaging.travel.booking_service.event.BookingStatusChangedEvent;
import com.messaging.travel.booking_service.exception.BookingNotFoundException;
import com.messaging.travel.booking_service.repository.BookingRepository;
import com.messaging.travel.booking_service.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ProcessedEventRepository processedEventRepository;

    public BookingResponse create(CreateBookingRequest request) {
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

        return toResponse(savedBooking);
    }

    @Transactional
    public void processBookingResult(BookingResultEvent bookingResultEvent) {
        if (processedEventRepository.existsByEventId(bookingResultEvent.eventId())) {
            System.out.println("Duplicate booking result event ignored. Event ID: " + bookingResultEvent.eventId());
            return;
        }

        Booking booking = bookingRepository.findById(bookingResultEvent.bookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingResultEvent.bookingId()));

        if ("RESERVED".equals(bookingResultEvent.status())) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
        }

        booking.setMessage(bookingResultEvent.message());

        bookingRepository.save(booking);

        BookingStatusChangedEvent statusChangedEvent = new BookingStatusChangedEvent(
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
                statusChangedEvent
        );

        ProcessedEvent processedEvent = new ProcessedEvent(
                bookingResultEvent.eventId(),
                ProcessedEventType.BOOKING_RESULT
        );

        processedEventRepository.save(processedEvent);
    }

    public List<BookingResponse> findAll() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BookingResponse findById(UUID id) {
        Booking booking =  bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + id));

        return toResponse(booking);
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomerName(),
                booking.getDestination(),
                booking.getTravelers(),
                booking.getStatus().name(),
                booking.getMessage(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

}
