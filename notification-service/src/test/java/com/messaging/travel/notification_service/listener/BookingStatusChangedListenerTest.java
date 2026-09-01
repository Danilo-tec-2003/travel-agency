package com.messaging.travel.notification_service.listener;

import com.messaging.travel.notification_service.event.BookingStatusChangedEvent;
import com.messaging.travel.notification_service.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingStatusChangedListenerTest {

    @InjectMocks
    private BookingStatusChangedListener listener;

    @Mock
    private EmailService emailService;

    @Test
    void shouldSendEmailWhenBookingStatusChangedEventIsReceived() {
        BookingStatusChangedEvent event = new BookingStatusChangedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Danilo",
                "danilo@email.com",
                "Recife",
                "CONFIRMED",
                "Travel package reserved successfully"
        );

        listener.handle(event);

        verify(emailService).send(
                eq("danilo@email.com"),
                eq("Travel booking confirmed"),
                contains("Travel package reserved successfully")
        );
    }
}
