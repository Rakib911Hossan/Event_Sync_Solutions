package com.Corporate.Event_Sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultWeekDaysDto {

    private Integer id;
    private Integer userId;
    private String days;
    @JsonProperty("isWeekDays")
    private boolean isWeekDays;
}
