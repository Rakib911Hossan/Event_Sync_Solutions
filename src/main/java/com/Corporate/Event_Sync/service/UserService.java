package com.Corporate.Event_Sync.service;

import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.repository.UserRepository;
import com.Corporate.Event_Sync.utils.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
//@NoArgsConstructor
@AllArgsConstructor
@Service
public class UserService {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    /*   public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }*/
    // For password hashing
//
//    // Register new user
    public User registerUser(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);  // Active status
        return userRepository.save(user);
    }

    // Find user by email
//    public Optional<User> findByEmail(String email) {
//        return Optional.ofNullable(userRepository.findByEmail(email));
//    }

    // Authenticate user (login)
    public boolean authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        // Check for user existence
        if (user == null) {
            throw new NotFoundException("User with email " + email + " not found");
        }
        // Compare the raw password with the hashed password stored in the User entity
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email);

        // Return userDTO or throw NotFoundException
        if (user == null) {
            throw new NotFoundException("User with email " + email + " not found");
        }
        // Convert User to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(Math.toIntExact(user.getId()));
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setDepartment(user.getDepartment());
        userDTO.setRole(Role.valueOf(String.valueOf(user.getRole())));
        userDTO.getOrders();
        userDTO.setIsActive(user.getIsActive());
        userDTO.setOfficeId(user.getOfficeId());

        return userDTO;
    }






    // Get user by ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }
    // Deactivate user account
    public void deactivateUser(Integer id) {
        Optional<User> user = getUserById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setIsActive(false);
            userRepository.save(foundUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }
//    public UserDTO getUserWithOrdersById(Integer id) {
//        UserDTO userDTO = userRepository.findUserWithOrdersById(id);
//        if (userDTO == null) {
//            throw new EntityNotFoundException("User not found with id: " + id);
//        }
//        return userDTO;
//    }






}
