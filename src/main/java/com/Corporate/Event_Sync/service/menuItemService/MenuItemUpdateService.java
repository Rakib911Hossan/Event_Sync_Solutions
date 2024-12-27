package com.Corporate.Event_Sync.service.menuItemService;

import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.mapper.MenuItemMapper;
import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MenuItemUpdateService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @PostConstruct
    public void insertInitialData() {
        List<MenuItemDto> menuItemsDto = List.of(
                new MenuItemDto( null, "PANCAKES", "Fluffy pancakes with syrup", "BREAKFAST", "08:00 - 10:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/Panecake.jpg",100),
                new MenuItemDto(null, "OMELETTE", "Classic omelette with cheese", "BREAKFAST", "08:00 - 10:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/omelette.jpg",100),
                new MenuItemDto(null, "FRUIT SALAD", "Fresh mixed fruit", "BREAKFAST", "08:00 - 10:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/fruit-salad.jpg",90),
                new MenuItemDto(null, "AVOCADO TOAST", "Toast with avocado spread", "BREAKFAST", "08:00 - 10:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/AVOCADO TOAST.jpg",90),
                new MenuItemDto(null, "YOGURT PARFAIT", "Yogurt with granola and berries", "BREAKFAST", "08:00 - 10:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/YOGURT PARFAIT.jpg",95),

                new MenuItemDto(null, "GRILLED CHICKEN", "Juicy grilled chicken with herbs", "LUNCH", "12:00 - 14:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/GRILLED CHICKEN.jpg",190),
                new MenuItemDto(null, "FISH TACOS", "Crispy fish tacos with fresh salsa", "LUNCH", "12:00 - 14:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/FISH TACOS.jpg",190),
                new MenuItemDto(null, "CAESAR SALAD", "Crisp romaine with Caesar dressing", "LUNCH", "12:00 - 14:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/CAESAR SALAD.jpg",195),
                new MenuItemDto(null, "CLUB SANDWICH", "Stacked sandwich with turkey and bacon", "LUNCH", "12:00 - 14:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/CLUB SANDWICH.jpg",190),
                new MenuItemDto(null, "PASTA SALAD", "Pasta with veggies in a tangy dressing", "LUNCH", "12:00 - 14:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/PASTA SALAD.jpg",195),

                new MenuItemDto(null, "APPLE SLICES", "Fresh apple slices with peanut butter", "SNACKS", "15:00 - 16:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/APPLE SLICES.jpg",100),
                new MenuItemDto(null, "ENERGY BARS", "Oats and nut energy bars", "SNACKS", "15:00 - 16:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/ENERGY BARS.jpg",95),
                new MenuItemDto(null, "YOGURT CUPS", "Greek yogurt with fruit toppings", "SNACKS", "15:00 - 16:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/YOGURT CUPS.jpg",100),
                new MenuItemDto(null, "HUMMUS PLATE", "Hummus with veggie sticks", "SNACKS", "15:00 - 16:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/HUMMUS PLATE.jpg",90),
                new MenuItemDto(null, "STUFFED PEPPERS", "Bell peppers stuffed with rice and meat", "SNACKS", "15:00 - 16:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/STUFFED PEPPERS.jpg",95),

                new MenuItemDto(null, "STEAK", "Tender steak cooked to perfection", "DINNER", "18:00 - 20:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/STEAK.jpg",190),
                new MenuItemDto(null, "PAELLA", "Traditional Spanish rice dish with seafood", "DINNER", "18:00 - 20:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/PAELLA.jpg",190),
                new MenuItemDto(null, "VEGETABLE STIR-FRY", "Mixed vegetables stir-fried with soy sauce", "DINNER", "18:00 - 20:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/VEGETABLE STIR-FRY.jpg",195),
                new MenuItemDto(null, "LASAGNA", "Layers of pasta, meat, and cheese", "DINNER", "18:00 - 20:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/LASAGNA.jpg",190),
                new MenuItemDto(null, "SPAGHETTI CONGOLESE", "Classic spaghetti with meat sauce", "DINNER", "18:00 - 20:00", "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/Foods/SPAGHETTI CONGOLESE.jpg",195)
        );

        // Convert MenuItemDto to MenuItem entity and insert into the database if not already present
        for (MenuItemDto menuItemDto : menuItemsDto) {
            if (!menuItemRepository.existsByItemName(menuItemDto.getItemName())) {
                MenuItem menuItem = new MenuItem(
                        menuItemDto.getItemName(),
                        menuItemDto.getDescription(),
                        menuItemDto.getCategory(),
                        menuItemDto.getAvailableTime(),
                        menuItemDto.getItemPic(),
                        menuItemDto.getPrice()
                );
                menuItemRepository.save(menuItem);
            }
        }
    }

    // Method to delete a menu item by name
    public void deleteMenuItemByName(String itemName) {
        if (menuItemRepository.existsByItemName(itemName)) {
            menuItemRepository.deleteByItemName(itemName);
        }
    }

    // Method to fetch all menu items and convert them to DTOs
    public List<MenuItemDto> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        return menuItems.stream()
                .map(menuItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteMenuItem(Integer id) {
        if (!menuItemRepository.existsById(id)) {
            throw new NotFoundException("MenuItem not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }

}
