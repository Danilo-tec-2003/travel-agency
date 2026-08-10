package com.messaging.travel.reservation_service.listener;

import com.messaging.travel.reservation_service.config.RabbitMQConfig;
import com.messaging.travel.reservation_service.event.BookingCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookingCreatedListener {

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_QUEUE)
    public void handle(BookingCreatedEvent event) {
        System.out.println("Booking created event received:");
        System.out.println("Event ID: " + event.eventId());
        System.out.println("Booking ID: " + event.bookingId());
        System.out.println("Customer: " + event.customerName());
        System.out.println("Destination: " + event.destination());
        System.out.println("Travelers: " + event.travelers());
    }
}
