package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.DefaultWeekDaysDto;
import com.Corporate.Event_Sync.service.defaultWeekDaysService.DefaultWeekDaysService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/defaultWeekDays")
public class DefaultWeekDaysController {

    private final DefaultWeekDaysService defaultWeekDaysService;


    @PostMapping("/create")
    public ResponseEntity<String> createDefaultWeekDays(@RequestBody DefaultWeekDaysDto dto) {
        try {
            // Call service method to create DefaultWeekDays
            defaultWeekDaysService.createWeekDays(
                    dto.getUserId(),
                    dto.getDays(),
                    dto.isWeekDays()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body("DefaultWeekDays created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating DefaultWeekDays.");
        }
    }
    @DeleteMapping("/delete/{userId}/{days}")
    public ResponseEntity<String> deleteWeekDay(@PathVariable Integer userId, @PathVariable String days) {
        try {
            // Call the service method to delete the week day
            defaultWeekDaysService.deleteWeekDay(userId, days);
            // Return a success message
            return ResponseEntity.ok("Week day deleted successfully for user: " + userId + " on day: " + days);
        } catch (IllegalArgumentException e) {
            // Return an error message if the week day does not exist
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public List<DefaultWeekDaysDto> getWeekDaysByUserId(@PathVariable Integer userId) {
        return defaultWeekDaysService.getDaysByUserId(userId);
    }
}