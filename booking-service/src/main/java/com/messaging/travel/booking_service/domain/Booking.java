package com.messaging.travel.booking_service.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private UUID id;

    private String customerName;

    private String destination;

    private Integer travelers;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Booking(String customerName, String destination, Integer travelers) {
        this.id = UUID.randomUUID();
        this.customerName = customerName;
        this.destination = destination;
        this.travelers = travelers;
        this.status = BookingStatus.PENDING;
        this.message = "Booking created and waiting reservation processing";
    }

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
