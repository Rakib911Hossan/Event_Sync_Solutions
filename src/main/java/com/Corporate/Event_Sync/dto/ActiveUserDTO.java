package com.Corporate.Event_Sync.dto;

import com.Corporate.Event_Sync.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor // Generates a constructor with all fields
public class ActiveUserDTO {
    private Integer id;
    private String name;
    private String department;
    private Integer officeId;
    private List<Order> orders;

    // Optionally, you can also define a no-argument constructor
    public ActiveUserDTO() {}
}
