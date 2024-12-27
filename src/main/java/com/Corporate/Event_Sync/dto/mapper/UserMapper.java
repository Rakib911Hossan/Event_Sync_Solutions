package com.Corporate.Event_Sync.dto.mapper;

import com.Corporate.Event_Sync.dto.ActiveUserDTO;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.entity.User;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserMapper {

    public ActiveUserDTO mapUserToActiveUserDTO(User user) {
        return new ActiveUserDTO(
                user.getId(),
                user.getName(),
                user.getDepartment(),
                user.getOfficeId(),
                user.getOrders()

        );
    }

    public UserDTO convertToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getAddress(),// Fixed here
                user.getDepartment(),
                user.getRole(),
                user.getIsActive(),
                user.getOfficeId(),
                user.getUserPic()
        );
    }
}
