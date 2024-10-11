package com.Corporate.Event_Sync.service;

import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.repository.UserRepository;
import com.Corporate.Event_Sync.utils.Role;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder; // For password hashing

    // Register new user
    public User registerUser(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);  // Active status
        return userRepository.save(user);
    }

    // Find user by email
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    // Authenticate user (login)
    public boolean authenticate(String email, String rawPassword) {
        Optional<User> user = findByEmail(email);
        if (user.isPresent()) {
            return passwordEncoder.matches(rawPassword, user.get().getPassword());
        }
        return false;
    }

    // Get user by ID
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    // Update user role
    public User updateUserRole(Integer id, Role role) {
        Optional<User> user = getUserById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setRole(role);
            return userRepository.save(foundUser);
        }
        throw new RuntimeException("User not found");
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
}
