package com.messaging.travel.booking_service.listener;

import com.messaging.travel.booking_service.event.BookingResultEvent;
import com.messaging.travel.booking_service.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingResultListenerTest {

    @InjectMocks
    private BookingResultListener bookingResultListener;

    @Mock
    private BookingService bookingService;

    @Test
    void shouldDelegateBookingResultEventToService() {
        BookingResultEvent event = new BookingResultEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "RESERVED",
                "Reservation confirmed successfully"
        );

        bookingResultListener.handle(event);

        verify(bookingService).processBookingResult(event);
    }
}
