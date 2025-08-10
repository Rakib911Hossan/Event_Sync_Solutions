package com.Corporate.Event_Sync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderDTO {
    private Integer orderId;
    private Integer userId;
    private Integer menuItemId;
    private Integer price;
    private LocalDateTime orderDate;
    private String status;
    private String department;
    private double longitude;
    private double latitude;
}
