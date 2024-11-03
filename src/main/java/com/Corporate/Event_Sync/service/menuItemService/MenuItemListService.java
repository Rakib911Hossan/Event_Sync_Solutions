package com.Corporate.Event_Sync.service.menuItemService;

import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class MenuItemListService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItemDto> getAllMenuItems() {
        // Fetch the raw data using the repository
        List<Object[]> menuItemsData = menuItemRepository.findAllMenuItems();
        List<MenuItemDto> menuItems = new ArrayList<>();

        // Iterate over the raw data to create DTOs
        for (Object[] item : menuItemsData) {
            MenuItemDto menuItemDto = new MenuItemDto(
                    (Integer) item[0], // id
                    (String) item[1],  // item_name
                    (String) item[2],  // description
                    (String) item[3],  // category
                    (String) item[4]   // available_time
            );
            menuItems.add(menuItemDto);
        }

        return menuItems; // Return the list of MenuItemDto
    }


}
