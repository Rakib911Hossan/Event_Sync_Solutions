package com.Corporate.Event_Sync.dto.mapper;

import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.entity.MenuItem;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class DefaultWeekDaysMapper {

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


}
