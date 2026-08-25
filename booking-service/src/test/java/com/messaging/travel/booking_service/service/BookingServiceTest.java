package com.messaging.travel.booking_service.service;

import com.messaging.travel.booking_service.domain.Booking;
import com.messaging.travel.booking_service.domain.BookingStatus;
import com.messaging.travel.booking_service.dto.BookingResponse;
import com.messaging.travel.booking_service.dto.CreateBookingRequest;
import com.messaging.travel.booking_service.event.BookingCreatedEvent;
import com.messaging.travel.booking_service.exception.BookingNotFoundException;
import com.messaging.travel.booking_service.repository.BookingRepository;
import com.messaging.travel.booking_service.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Test
    void shouldCreatePendingBookingAndPublishEvent() {
        CreateBookingRequest request = new CreateBookingRequest("Danilo", "Recife", 3);

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.customerName()).isEqualTo("Danilo");
        assertThat(response.destination()).isEqualTo("Recife");
        assertThat(response.travelers()).isEqualTo(3);
        assertThat(response.status()).isEqualTo("PENDING");

        verify(bookingRepository).save(any(Booking.class));
        verify(rabbitTemplate).convertAndSend(
                eq("travel.exchange"),
                eq("booking.created"),
                any(BookingCreatedEvent.class)
        );
    }

    @Test
    void shouldReturnBookingWhenFindByIdExists() {

        //Given (Dados)
        UUID id = UUID.randomUUID();
        Booking booking = new Booking("Rebeca",
                "Jaboatao",
                2);

        booking.setId(id);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        //When (quando)
        BookingResponse response = bookingService.findById(id);

        //Then (então)
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.customerName()).isEqualTo("Rebeca");
        assertThat(response.destination()).isEqualTo("Jaboatao");
        assertThat(response.travelers()).isEqualTo(2);
        assertThat(response.status()).isEqualTo("CONFIRMED");

        verify(bookingRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {

        //Given (Dados)
        UUID id = UUID.randomUUID();

        //When (quando)
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> bookingService.findById(id))
                .isInstanceOf(BookingNotFoundException.class);

        verify(bookingRepository).findById(id);
    }
}
