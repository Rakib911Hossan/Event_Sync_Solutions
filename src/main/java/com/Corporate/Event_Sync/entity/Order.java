package com.Corporate.Event_Sync.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "orders")
public class Order extends GenericEntity<Integer>{
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id", nullable = false) // Foreign key to User
    private User user;

    private LocalDateTime orderDate; // Could use LocalDateTime or String depending on your preference

    @Column(nullable = false)
    private String status; // Enum for status like ORDERED, PREPARED, SERVED

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    private double longitude;

    private double latitude;

    // Getters and Setters
}

