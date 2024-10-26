package com.Corporate.Event_Sync.service.menuItemService;

import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemListService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemListService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }


    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }
}
