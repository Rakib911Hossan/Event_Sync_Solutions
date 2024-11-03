package com.Corporate.Event_Sync.service.defaultWeekDaysService;

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
import java.util.List;
import java.util.Random;

@AllArgsConstructor
@Service
public class DefaultWeekDaysService {

    private final DefaultWeekDaysRepository defaultWeekDaysRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderService orderService;
    private final DefaultWeekDaysMapper defaultWeekDaysMapper;

    public void createWeekDays(Integer userId, String days, Boolean isWeekDays) {
        if (!isWeekDays) {
            throw new IllegalArgumentException("Cannot create DefaultWeekDays as it is not a week day.");
        }

        // Delete existing DefaultWeekDays and associated orders for the user
        List<DefaultWeekDays> existingWeekDays = defaultWeekDaysRepository.findByUserId(userId);
        for (DefaultWeekDays defaultWeekDay : existingWeekDays) {
            orderService.deleteOrdersByUserId(userId);
            defaultWeekDaysRepository.delete(defaultWeekDay);
        }

        // Save new DefaultWeekDays entry
        defaultWeekDaysRepository.saveDefaultWeekDays(userId, days, isWeekDays);

        String currentDay = LocalDate.now().getDayOfWeek().toString();
        if (days.contains(currentDay)) {
            // Fetch LUNCH and SNACKS menu items
            List<MenuItem> lunchMenuItems = menuItemRepository.findByCategory(Category.LUNCH.name());
            List<MenuItem> snackMenuItems = menuItemRepository.findByCategory(Category.SNACKS.name());

            Random random = new Random();
            if (!lunchMenuItems.isEmpty() && !snackMenuItems.isEmpty()) {
                MenuItemDto lunchMenuItem = defaultWeekDaysMapper.mapToDto(
                        lunchMenuItems.get(random.nextInt(lunchMenuItems.size()))
                );
                MenuItemDto snacksMenuItem = defaultWeekDaysMapper.mapToDto(
                        snackMenuItems.get(random.nextInt(snackMenuItems.size()))
                );

                // Create orders using IDs from MenuItemDto
                orderService.createOrder(userId, lunchMenuItem.getId(), "ORDERED", LocalDateTime.now());
                orderService.createOrder(userId, snacksMenuItem.getId(), "ORDERED", LocalDateTime.now());
            } else {
                throw new IllegalStateException("No lunch or snack items available for ordering.");
            }
        }
    }
}
