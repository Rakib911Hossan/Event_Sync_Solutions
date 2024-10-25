package com.Corporate.Event_Sync.utils;

import lombok.Getter;

@Getter
public enum Category {
    BREAKFAST("07:00", "10:00"),
    LUNCH("12:00", "14:00"),
    SNACKS("15:00", "17:00");

    private final String startTime;
    private final String endTime;

    Category(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return name() + " (" + startTime + " - " + endTime + ")";
    }
}
