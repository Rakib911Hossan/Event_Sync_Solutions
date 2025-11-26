//package com.Corporate.Event_Sync.controller;
//
//import com.Corporate.Event_Sync.dto.MenuItemDto;
//import com.Corporate.Event_Sync.entity.MenuItem;
//import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
//import com.Corporate.Event_Sync.service.menuItemService.MenuItemService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//@AllArgsConstructor
//@RestController
//@RequestMapping("/api/menu-items")
//public class MenuItemController {
//
//    private final MenuItemService menuItemService;
//    private final MenuItemListService menuItemListService;
//
//
//    @PostMapping
//    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItemDto menuItemDto) {
//        MenuItem createdMenuItem = menuItemService.createMenuItem(menuItemDto);
//        return ResponseEntity.ok(createdMenuItem);
//    }
//    @PutMapping("/{id}")
//    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Integer id, @RequestBody MenuItem menuItem) {
//        MenuItem updatedMenuItem = menuItemService.updateMenuItem(id, menuItem);
//        return ResponseEntity.ok(updatedMenuItem);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer id) {
//        menuItemService.deleteMenuItem(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Integer id) {
//        MenuItem menuItem = menuItemService.getMenuItemById(id);
//        return ResponseEntity.ok(menuItem);
//    }
//
//    @GetMapping
//    public List<MenuItemDto> getAllMenuItems() {
//        return menuItemListService.getAllMenuItems(); // Call service to get the list of MenuItemDto
//    }
//
//
//    @GetMapping("/category/{category}")
//    public ResponseEntity<List<MenuItemDto>> getMenuItemsByCategory(@PathVariable String category) {
//        List<MenuItemDto> menuItems = menuItemService.getMenuItemsByCategory(category);
//        return ResponseEntity.ok(menuItems);
//    }
//}
