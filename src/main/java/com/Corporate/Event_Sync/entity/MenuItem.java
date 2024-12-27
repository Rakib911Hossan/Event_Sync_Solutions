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
    public MenuItem(String itemName, String description, String category, String availableTime, String itemPic,Integer price) {
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.availableTime = availableTime;
        this.itemPic = itemPic;
        this.price= price;
    }

    private String itemName;
    private String description;


    private String category;//  BREAKFAST,LUNCH,SNACKS,DINNER etc.

    private String availableTime;

    private Integer price;

    @JsonIgnore
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders;
    private String itemPic;



//    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Feedback> feedbacks;


}