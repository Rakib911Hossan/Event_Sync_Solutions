package com.Corporate.Event_Sync.entity;

import com.Corporate.Event_Sync.utils.Status;
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
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id",referencedColumnName = "id", nullable = false) // Foreign key to User
    private User user;

    private LocalDateTime orderDate; // Could use LocalDateTime or String depending on your preference

    @Enumerated(EnumType.STRING)
    private Status status; // Enum for status like ORDERED, PREPARED, SERVED
//    private LocalDateTime orderDate; // Changed to LocalDateTime for consistency with the TIMESTAMP SQL type

//    @ManyToOne
//    @JoinColumn(name = "menu_item_id", nullable = false)
//    private MenuItem menuItem;
//

    // Getters and Setters
}

