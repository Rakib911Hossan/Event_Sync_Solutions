package com.Corporate.Event_Sync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "default_Weekdays")
public class DefaultWeekDays extends GenericEntity<Integer> {


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    private String days;
    @Column(name = "is_week_days",nullable = false)
   private boolean isWeekDays;

}
