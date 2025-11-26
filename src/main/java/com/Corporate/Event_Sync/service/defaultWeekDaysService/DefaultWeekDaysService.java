package com.Corporate.Event_Sync.service.defaultWeekDaysService;

import com.Corporate.Event_Sync.dto.DefaultWeekDaysDto;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.mapper.DefaultWeekDaysMapper;
import com.Corporate.Event_Sync.entity.DefaultWeekDays;
import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.repository.DefaultWeekDaysRepository;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import com.Corporate.Event_Sync.utils.Category;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DefaultWeekDaysService {

    private final DefaultWeekDaysRepository defaultWeekDaysRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderService orderService;
    private final DefaultWeekDaysMapper defaultWeekDaysMapper;

    /**
     * Creates week days with selected meal categories
     */
    public void createWeekDays(Integer userId, String days, Boolean isWeekDays,
                               double latitude, double longitude, List<String> mealCategories) {
        if (!isWeekDays) {
            throw new IllegalArgumentException("Cannot create DefaultWeekDays as it is not a week day.");
        }

        if (mealCategories == null || mealCategories.isEmpty()) {
            throw new IllegalArgumentException("At least one meal category must be selected.");
        }

        // Delete existing DefaultWeekDays and associated orders for the user
        List<DefaultWeekDays> existingWeekDays = defaultWeekDaysRepository.findByUserId(userId);
        for (DefaultWeekDays defaultWeekDay : existingWeekDays) {
            orderService.deleteOrdersByUserId(userId);
            defaultWeekDaysRepository.delete(defaultWeekDay);
        }

        // Save new DefaultWeekDays entry
        defaultWeekDaysRepository.saveDefaultWeekDays(userId, days, isWeekDays);

        // Check if current day matches selected days
        String currentDay = LocalDate.now().getDayOfWeek().toString();
        if (days.toUpperCase().contains(currentDay)) {
            createOrdersForCategories(userId, mealCategories, latitude, longitude);
        }
    }

    /**
     * Creates orders for selected meal categories
     */
    private void createOrdersForCategories(Integer userId, List<String> categories,
                                           double latitude, double longitude) {
        Random random = new Random();

        for (String category : categories) {
            // Get random menu item ID based on category range
            Integer menuItemId = getRandomMenuItemIdByCategory(category, random);

            if (menuItemId == null) {
                System.out.println("Warning: Invalid category or no items available: " + category);
                continue;
            }

            // Fetch the menu item by ID
            MenuItem menuItem = menuItemRepository.findById(menuItemId).orElse(null);

            if (menuItem == null) {
                System.out.println("Warning: Menu item not found for ID: " + menuItemId);
                continue;
            }

            MenuItemDto menuItemDto = defaultWeekDaysMapper.mapToDto(menuItem);

            // Create order
            orderService.createOrder(
                    userId,
                    menuItemDto.getId(),
                    "ORDERED",
                    LocalDateTime.now(),
                    latitude,
                    longitude,
                    menuItemDto.getPrice()
            );

            System.out.println("Order created - Category: " + category +
                    ", Item ID: " + menuItemDto.getId() +
                    ", Price: " + menuItemDto.getPrice());
        }
    }

    /**
     * Get random menu item ID based on category range
     * BREAKFAST: 1-5
     * LUNCH: 6-10
     * SNACKS: 11-15
     * DINNER: 16-20
     */
    private Integer getRandomMenuItemIdByCategory(String category, Random random) {
        int min, max;

        switch (category.toUpperCase()) {
            case "BREAKFAST":
                min = 1;
                max = 5;
                break;
            case "LUNCH":
                min = 6;
                max = 10;
                break;
            case "SNACKS":
                min = 11;
                max = 15;
                break;
            case "DINNER":
                min = 16;
                max = 20;
                break;
            default:
                System.out.println("Unknown category: " + category);
                return null;
        }

        // Generate random ID within range (inclusive)
        return min + random.nextInt(max - min + 1);
    }


    /**
     * Overloaded method for backward compatibility (uses default categories: LUNCH and SNACKS)
     */
    public void createWeekDays(Integer userId, String days, Boolean isWeekDays,
                               double latitude, double longitude) {
        List<String> defaultCategories = Arrays.asList(
                Category.LUNCH.name(),
                Category.SNACKS.name()
        );
        createWeekDays(userId, days, isWeekDays, latitude, longitude, defaultCategories);
    }

    public void deleteWeekDay(Integer userId, String dayToRemove) {
        List<DefaultWeekDays> defaultWeekDaysList = defaultWeekDaysRepository.findByUserId(userId);

        for (DefaultWeekDays defaultWeekDay : defaultWeekDaysList) {
            String[] daysArray = defaultWeekDay.getDays().split(",");
            List<String> daysList = new ArrayList<>(Arrays.asList(daysArray));

            if (daysList.contains(dayToRemove)) {
                daysList.remove(dayToRemove);
                String updatedDays = String.join(",", daysList);
                defaultWeekDay.setDays(updatedDays);
                defaultWeekDaysRepository.save(defaultWeekDay);
            }
        }
    }

    public List<DefaultWeekDaysDto> getDaysByUserId(Integer userId) {
        List<DefaultWeekDays> weekDaysList = defaultWeekDaysRepository.findByUserId(userId);
        return weekDaysList.stream()
                .map(weekDay -> new DefaultWeekDaysDto(
                        weekDay.getId(),
                        weekDay.getUser().getId(),
                        weekDay.getDays(),
                        weekDay.isWeekDays()
                ))
                .collect(Collectors.toList());
    }
}