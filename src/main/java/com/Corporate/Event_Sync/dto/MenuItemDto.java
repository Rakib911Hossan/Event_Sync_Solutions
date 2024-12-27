package com.Corporate.Event_Sync.dto;

import com.Corporate.Event_Sync.entity.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDto {
    private Integer id;
    private String itemName;
    private String description;
    private String category;
    private String availableTime;
    private String itemPic;  // Holds the image path or URL
    private Integer price;
    public MenuItemDto(MenuItem menuItem) {
    }

    public String getItemImage() {
        return itemPic; // Ensure this returns a valid path or URL
    }

    // Add a method to validate or fix the path if necessary
    public String getImagePath() {
        if (itemPic != null && !itemPic.startsWith("file:") && !itemPic.startsWith("http")) {
            return "file:" + itemPic; // Prepend file: if not present
        }
        return itemPic; // Already a valid URL or file path
    }
}
