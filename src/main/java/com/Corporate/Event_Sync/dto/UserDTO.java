package com.Corporate.Event_Sync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Generates a constructor with all fields
public class UserDTO {
    private Integer id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String department;
    private String role;
    private Boolean isActive;
    private Integer officeId;
    private String userPic;
    private String passToken;
    private String discountToken;
    private Integer discountAmount;
    private String userCategory;

    // Optionally, you can also define a no-argument constructor
    public UserDTO() {}
}
