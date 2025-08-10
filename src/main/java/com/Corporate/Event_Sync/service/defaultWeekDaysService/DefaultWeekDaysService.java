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


    public void createWeekDays(Integer userId, String days, Boolean isWeekDays,
                               double latitude, double longitude) {
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
                orderService.createOrder(userId, lunchMenuItem.getId(), "ORDERED", LocalDateTime.now(), latitude, longitude);
                orderService.createOrder(userId, snacksMenuItem.getId(), "ORDERED", LocalDateTime.now(), latitude, longitude);
            } else {
                throw new IllegalStateException("No lunch or snack items available for ordering.");
            }
        }
    }

    public void deleteWeekDay(Integer userId, String dayToRemove) {
        // Fetch the user's default week days
        List<DefaultWeekDays> defaultWeekDaysList = defaultWeekDaysRepository.findByUserId(userId);

        for (DefaultWeekDays defaultWeekDay : defaultWeekDaysList) {
            // Split the 'days' string into an array of days
            String[] daysArray = defaultWeekDay.getDays().split(",");

            // Convert the array to a list to manipulate it
            List<String> daysList = new ArrayList<>(Arrays.asList(daysArray));

            // Remove the target day from the list (if it exists)
            if (daysList.contains(dayToRemove)) {
                daysList.remove(dayToRemove);

                // Rebuild the days string from the updated list
                String updatedDays = String.join(",", daysList);

                // Update the 'days' field with the new string and save it back to the database
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
