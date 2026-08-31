package com.messaging.travel.booking_service.listener;

import com.messaging.travel.booking_service.config.RabbitMQConfig;
import com.messaging.travel.booking_service.event.BookingResultEvent;
import com.messaging.travel.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingResultListener {

    private final BookingService bookingService;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_RESULT_QUEUE)
    public void handle(BookingResultEvent event) {
        log.info(
                "Booking result event received. eventId={}, bookingId={}, status={}, message={}",
                event.eventId(),
                event.bookingId(),
                event.status(),
                event.message()
        );

        bookingService.processBookingResult(event);
    }

}
