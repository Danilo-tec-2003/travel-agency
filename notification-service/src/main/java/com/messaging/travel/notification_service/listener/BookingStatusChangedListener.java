package com.messaging.travel.notification_service.listener;


import com.messaging.travel.notification_service.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.messaging.travel.notification_service.event.BookingStatusChangedEvent;

@Component
public class BookingStatusChangedListener {

    @RabbitListener (queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handle (BookingStatusChangedEvent event) {

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
            System.out.println("========> BOOKING CONFIRMED");
            System.out.println(bodyMail);
        } else {
            System.out.println("========> BOOKING CANCELED");
            System.out.println(bodyMail);
        }

    }
}
