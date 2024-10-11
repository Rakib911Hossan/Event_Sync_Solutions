package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.EventSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventSubscriptionRepository extends JpaRepository<EventSubscriptionEntity, Long> {

    @Query("SELECT e FROM EventSubscriptionEntity e WHERE e.event_id = :eventId AND e.subscribedAt >= :startOfDay AND e.subscribedAt < :endOfDay")
    List<EventSubscriptionEntity> findAllCreatedToday(Integer eventId,LocalDateTime startOfDay, LocalDateTime endOfDay);
}
