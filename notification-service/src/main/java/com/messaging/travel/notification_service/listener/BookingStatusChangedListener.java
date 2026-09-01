package com.messaging.travel.notification_service.listener;

import com.messaging.travel.notification_service.config.RabbitMQConfig;
import com.messaging.travel.notification_service.event.BookingStatusChangedEvent;
import com.messaging.travel.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingStatusChangedListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handle(BookingStatusChangedEvent event) {

        String subject = "Travel booking %s".formatted(event.status().toLowerCase());

        String body = """
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
            log.info("Booking confirmation notification received. bookingId={}, customerEmail={}", event.bookingId(), event.customerEmail());
        } else {
            log.info("Booking cancellation notification received. bookingId={}, customerEmail={}", event.bookingId(), event.customerEmail());
        }

        emailService.send(event.customerEmail(), subject, body);
    }
}
