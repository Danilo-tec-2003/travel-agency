package com.messaging.travel.booking_service.repository;

import com.messaging.travel.booking_service.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository  extends JpaRepository<Booking, UUID> {
}
