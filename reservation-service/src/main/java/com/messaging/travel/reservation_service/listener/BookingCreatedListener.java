package com.messaging.travel.reservation_service.listener;

import com.messaging.travel.reservation_service.config.RabbitMQConfig;
import com.messaging.travel.reservation_service.event.BookingCreatedEvent;
import com.messaging.travel.reservation_service.event.BookingResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingCreatedListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_QUEUE)
    public void handle(BookingCreatedEvent event) {
        System.out.println("Booking created event received:");
        System.out.println("Event ID: " + event.eventId());
        System.out.println("Booking ID: " + event.bookingId());
        System.out.println("Customer: " + event.customerName());
        System.out.println("Destination: " + event.destination());
        System.out.println("Travelers: " + event.travelers());

        BookingResultEvent resultEvent = processReservation(event);

        String routingKey = "RESERVED".equals(resultEvent.status())
                ? RabbitMQConfig.BOOKING_RESERVED_ROUTING_KEY
                : RabbitMQConfig.BOOKING_FAILED_ROUTING_KEY;

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRAVEL_EXCHANGE,
                routingKey,
                resultEvent
        );
    }

    private BookingResultEvent processReservation(BookingCreatedEvent event) {
        if (event.travelers() > 4) {
            return new BookingResultEvent(
                    UUID.randomUUID(),
                    event.bookingId(),
                    "FAILED",
                    "Reservation failed: maximum travelers allowed is 4"
            );
        }
        return new BookingResultEvent(
                UUID.randomUUID(),
                event.bookingId(),
                "RESERVED",
                "Travel package reserved successfully"
        );
    }
}
