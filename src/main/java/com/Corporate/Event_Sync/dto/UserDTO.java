package com.Corporate.Event_Sync.dto;

import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.utils.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor // Generates a constructor with all fields
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private String department;
    private Role role; // Assuming role is a String; change if it's an Enum or different type
    private List<Order> orders;
    private Boolean isActive;
    private Integer officeId;

    // Optionally, you can also define a no-argument constructor
    public UserDTO() {}
}
