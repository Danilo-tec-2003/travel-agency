package com.messaging.travel.booking_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessedEventType eventType;

    private LocalDateTime processedAt;

    public ProcessedEvent(UUID eventId, ProcessedEventType eventType) {
        this.id = UUID.randomUUID();
        this.eventId = eventId;
        this.eventType = eventType;
    }

    @PrePersist
    void prePersist() {
        this.processedAt = LocalDateTime.now();
    }
}
