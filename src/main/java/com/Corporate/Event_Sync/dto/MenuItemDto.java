package com.Corporate.Event_Sync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class MenuItemDto {
    private Integer id;            // Unique identifier of the menu item
    private String itemName;        // Name of the menu item
    private String description;     // Description of the menu item
    private String category;
    private String availableTime;   // Time when the item is available


}
