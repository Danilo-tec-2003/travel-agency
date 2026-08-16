package com.messaging.travel.booking_service.listener;

import com.messaging.travel.booking_service.config.RabbitMQConfig;
import com.messaging.travel.booking_service.event.BookingResultEvent;
import com.messaging.travel.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingResultListener {

    private final BookingService bookingService;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_RESULT_QUEUE)
    public void handle(BookingResultEvent event) {
        System.out.println("Booking result event received:");
        System.out.println("Event ID: " + event.eventId());
        System.out.println("Booking ID: " + event.bookingId());
        System.out.println("Status: " + event.status());
        System.out.println("Message: " + event.message());

        bookingService.processBookingResult(event);
    }

}
