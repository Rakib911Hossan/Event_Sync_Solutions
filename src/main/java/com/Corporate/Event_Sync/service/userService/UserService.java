package com.Corporate.Event_Sync.service.userService;

import com.Corporate.Event_Sync.dto.ActiveUserDTO;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.dto.mapper.UserMapper;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.exceptions.ConflictException;
import com.Corporate.Event_Sync.exceptions.IllegalStateException;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.OrderRepository;
import com.Corporate.Event_Sync.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
//@NoArgsConstructor
@AllArgsConstructor
@Service
public class UserService {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private OrderRepository orderRepository;
    /*   public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }*/
    // For password hashing
//
//    // Register new user
    public String registerUser(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);  // Set active status

        try {
            userRepository.save(user);
            return "Registration successful!"; // Success message
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage(); // Failure message
        }
    }

    // Find user by email
//    public Optional<User> findByEmail(String email) {
//        return Optional.ofNullable(userRepository.findByEmail(email));
//    }

    public boolean authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        // Check if the user exists
        if (user == null) {
            throw new NotFoundException("User with email " + email + " not found");
        }
        // Check if the user is active
        if (!user.getIsActive()) {
            throw new IllegalStateException("User account is inactive. Please contact with admin.");
        }
        // Compare the raw password with the hashed password
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);

        // Return userDTO or throw NotFoundException
        if (user == null) {
            throw new NotFoundException("User with email " + email + " not found");
        }

        List<Order> orders = user.getOrders(); // This forces lazy loading of the orders collection

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setDepartment(user.getDepartment());
        userDTO.setRole(user.getRole());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setOfficeId(user.getOfficeId());

        return userDTO;
    }

    public UserDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        return userMapper.convertToUserDTO(user);
    }

    public void deactivateUser(Integer id) {
        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        foundUser.setIsActive(false);
        userRepository.save(foundUser);

        orderRepository.deleteOrdersByInactiveUsers();
    }

    public User fetchUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    }

    public void activateUser(Integer id) {
        User foundUser = fetchUserById(id);

        foundUser.setIsActive(true);

        userRepository.save(foundUser);
    }


    public ActiveUserDTO getUserByIdUserDto(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));

        // Check if the user is already active
        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new ConflictException("User with ID " + userId + " is already activated.");
        }

        return userMapper.mapUserToActiveUserDTO(user); // Convert User to ActiveUserDTO
    }



//    public UserDTO getUserWithOrdersById(Integer id) {
//        UserDTO userDTO = userRepository.findUserWithOrdersById(id);
//        if (userDTO == null) {
//            throw new EntityNotFoundException("User not found with id: " + id);
//        }
//        return userDTO;
//    }

}