package com.Corporate.Event_Sync.service.userService;

import com.Corporate.Event_Sync.dto.ActiveUserDTO;
import com.Corporate.Event_Sync.dto.mapper.UserMapper;
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
    private final UserMapper userMapper;

    // Fetch all active users
    public List<ActiveUserDTO> getActiveUsers() {
        List<User> activeUsers = userRepository.findByIsActive(true);

        return activeUsers.stream()
                .map(userMapper::mapUserToActiveUserDTO)  // Corrected mapping syntax
                .collect(Collectors.toList());
    }


}
