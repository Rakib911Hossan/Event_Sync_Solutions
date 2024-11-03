package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.DefaultWeekDaysDto;
import com.Corporate.Event_Sync.service.defaultWeekDaysService.DefaultWeekDaysService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}