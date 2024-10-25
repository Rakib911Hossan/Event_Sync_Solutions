package com.Corporate.Event_Sync.service;

import com.Corporate.Event_Sync.dto.ActiveUserDTO;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserListService {

    private final UserRepository userRepository;

    public List<ActiveUserDTO> getActiveUsers() {
        List<User> activeUsers = userRepository.findByIsActive(true);

        return activeUsers.stream()
                .map(this::mapUserToActiveUserDTO)
                .collect(Collectors.toList());
    }

    // Helper method to map User to ActiveUserDTO
    private ActiveUserDTO mapUserToActiveUserDTO(User user) {
        return new ActiveUserDTO(
                user.getId(),
                user.getName(),
                user.getDepartment(),
                user.getOfficeId(),
                user.getOrders()
        );
    }
}
