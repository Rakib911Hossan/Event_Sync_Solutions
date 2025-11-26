package com.Corporate.Event_Sync.dto.mapper;

import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.entity.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    // Converts MenuItem to MenuItemDto
    public MenuItemDto mapToDto(MenuItem menuItem) {
        return new MenuItemDto(
                menuItem.getId(),
                menuItem.getItemName(),
                menuItem.getDescription(),
                menuItem.getCategory(),
                menuItem.getAvailableTime(),
                menuItem.getItemPic(),
                menuItem.getPrice()
        );
    }

    // Converts MenuItemDto to MenuItem
    public MenuItem mapToEntity(MenuItemDto dto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(dto.getId());
        menuItem.setItemName(dto.getItemName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setCategory(dto.getCategory());
        menuItem.setAvailableTime(dto.getAvailableTime());
        menuItem.setItemPic(dto.getItemPic());
        menuItem.setPrice(dto.getPrice());
        return menuItem;
    }
}
