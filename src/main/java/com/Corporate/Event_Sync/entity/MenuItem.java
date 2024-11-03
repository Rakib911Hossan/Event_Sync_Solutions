package com.Corporate.Event_Sync.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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


    private String itemName;
    private String description;


    private String category;//  BREAKFAST,LUNCH,SNACKS,DINNER etc.

    private String availableTime;

    @JsonIgnore
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;

//    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Feedback> feedbacks;


}