package com.Corporate.Event_Sync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "event_subscriber", schema = "event_management")
public class EventSubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer user_id;

    private Integer event_id;

    private Integer office_id;

    private String status;

    @Column(name = "subscribed_at", nullable = false)
    private LocalDateTime subscribedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = true)
    private String createdBy;

    @Column(name = "updated_by", nullable = true)
    private String updatedBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        ZoneId zoneId = ZoneId.of("Asia/Dhaka");
        ZonedDateTime nowInDhaka = ZonedDateTime.now(zoneId);
        subscribedAt = nowInDhaka.toLocalDateTime(); // Convert to LocalDateTime for storage
        createdAt = nowInDhaka.toLocalDateTime();
        updatedAt = nowInDhaka.toLocalDateTime();
        isActive = true; // default value
    }

    @PreUpdate
    protected void onUpdate() {
        ZoneId zoneId = ZoneId.of("Asia/Dhaka");
        updatedAt = ZonedDateTime.now(zoneId).toLocalDateTime();
    }
}
