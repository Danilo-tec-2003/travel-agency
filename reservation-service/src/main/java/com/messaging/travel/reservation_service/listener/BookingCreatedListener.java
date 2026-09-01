package com.messaging.travel.reservation_service.listener;

import com.messaging.travel.reservation_service.config.RabbitMQConfig;
import com.messaging.travel.reservation_service.event.BookingCreatedEvent;
import com.messaging.travel.reservation_service.event.BookingResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCreatedListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_QUEUE)
    public void handle(BookingCreatedEvent event) {
        log.info(
                "Booking created event received. eventId={}, bookingId={}, customerName={}, customerEmail={}, destination={}, travelers={}",
                event.eventId(),
                event.bookingId(),
                event.customerName(),
                event.customerEmail(),
                event.destination(),
                event.travelers()
        );

        BookingResultEvent resultEvent = processReservation(event);

        String routingKey = "RESERVED".equals(resultEvent.status())
                ? RabbitMQConfig.BOOKING_RESERVED_ROUTING_KEY
                : RabbitMQConfig.BOOKING_FAILED_ROUTING_KEY;

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRAVEL_EXCHANGE,
                routingKey,
                resultEvent
        );

        log.info(
                "Booking result event published. eventId={}, bookingId={}, status={}, routingKey={}",
                resultEvent.eventId(),
                resultEvent.bookingId(),
                resultEvent.status(),
                routingKey
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
