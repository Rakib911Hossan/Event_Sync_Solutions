package com.Corporate.Event_Sync.entity;

import com.Corporate.Event_Sync.utils.Category;
import com.Corporate.Event_Sync.utils.ItemName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "menu_items")
public class MenuItem extends GenericEntity<Integer>{


    @Enumerated(EnumType.STRING)
    private ItemName itemName;
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category; // Breakfast, Lunch, Snacks, etc.

    private String availableTime;

//    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<LunchSchedule> lunchSchedules;

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;

//    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Feedback> feedbacks;

// Getters and Setters
}