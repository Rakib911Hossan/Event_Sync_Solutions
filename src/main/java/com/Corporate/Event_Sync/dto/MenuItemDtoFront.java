package com.Corporate.Event_Sync.dto;

import com.Corporate.Event_Sync.utils.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDtoFront {
    private Integer id;            // Unique identifier of the menu item
    private String itemName;        // Name of the menu item
    private String description;     // Description of the menu item
    private Category category;      // Category enum (BREAKFAST, LUNCH, etc.)
    private String availableTime;   // Time when the item is available
}
