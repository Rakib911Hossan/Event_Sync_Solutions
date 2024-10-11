package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.EventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventsEntity, Integer> {
    EventsEntity findByCode(String le);
}
