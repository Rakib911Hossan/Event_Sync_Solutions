package com.Corporate.Event_Sync.service.menuItemService;

import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }


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

}
