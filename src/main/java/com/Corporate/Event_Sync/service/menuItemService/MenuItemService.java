package com.Corporate.Event_Sync.service.menuItemService;

import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.mapper.MenuItemMapper;
import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }


    public MenuItem updateMenuItem(Integer id, MenuItem menuItem) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MenuItem not found with id: " + id));

        existingMenuItem.setItemName(menuItem.getItemName());
        existingMenuItem.setDescription(menuItem.getDescription());
        existingMenuItem.setCategory(menuItem.getCategory());
        existingMenuItem.setAvailableTime(menuItem.getAvailableTime());
        return menuItemRepository.save(existingMenuItem);
    }


    public void deleteMenuItem(Integer id) {
        if (!menuItemRepository.existsById(id)) {
            throw new NotFoundException("MenuItem not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }


    public MenuItem getMenuItemById(Integer id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MenuItem not found with id: " + id));
    }

    public List<MenuItemDto> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = menuItemRepository.findByCategory(category);
        return menuItems.stream().map(menuItemMapper::mapToDto).collect(Collectors.toList());
    }

}
