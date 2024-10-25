package com.Corporate.Event_Sync.dto;

import com.Corporate.Event_Sync.utils.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class OrderDTO {
    private Long userId;
    private Long orderId;
    private String orderDate;
    private Status status;
}