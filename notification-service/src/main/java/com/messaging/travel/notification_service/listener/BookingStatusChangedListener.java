package com.messaging.travel.notification_service.listener;

import com.messaging.travel.notification_service.config.RabbitMQConfig;
import com.messaging.travel.notification_service.event.BookingStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingStatusChangedListener {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handle(BookingStatusChangedEvent event) {

        String bodyMail = """
                Booking ID: %s
                Customer: %s
                Destination: %s
                Status: %s
                Message: %s
                """.formatted(
                event.bookingId(),
                event.customerName(),
                event.destination(),
                event.status(),
                event.message()
        );


        if ("CONFIRMED".equals(event.status())) {
            log.info("Booking confirmation email simulated. bookingId={}, customerName={}", event.bookingId(), event.customerName());
        } else {
            log.info("Booking cancellation email simulated. bookingId={}, customerName={}", event.bookingId(), event.customerName());
        }

        log.info("Email body simulated:\n{}", bodyMail);
    }
}
