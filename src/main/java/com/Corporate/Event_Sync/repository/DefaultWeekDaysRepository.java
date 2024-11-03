package com.Corporate.Event_Sync.repository;

import com.Corporate.Event_Sync.entity.DefaultWeekDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DefaultWeekDaysRepository extends JpaRepository<DefaultWeekDays, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO default_Weekdays (user_id, days, is_week_days) VALUES (:userId, :days, :isWeekDays)", nativeQuery = true)
    void saveDefaultWeekDays(@Param("userId") Integer userId,
                             @Param("days") String days,
                             @Param("isWeekDays") boolean isWeekDays);


    List<DefaultWeekDays> findByUserId(Integer userId);
}
