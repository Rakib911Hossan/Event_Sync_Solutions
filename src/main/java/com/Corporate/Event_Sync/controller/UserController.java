package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.ActiveUserDTO;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.service.UserListService;
import com.Corporate.Event_Sync.service.userService.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String email, @RequestParam String password) {
        // Authenticate the user
        boolean isAuthenticated = userService.authenticate(email, password);

        if (isAuthenticated) {
            // Retrieve user data after successful authentication
            UserDTO userDTO = userService.findByEmail(email);
            return ResponseEntity.ok(userDTO); // Return user data
        }

        // If authentication fails
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> ResponseEntity.ok(userService.findByEmail(value.getEmail())))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Deactivate user account
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Integer id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok("User account deactivated successfully");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Activate user account (only for admin)
    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(@PathVariable Integer id) {
        try {
            // Here, you should check if the requester has admin privileges
            // For simplicity, let's assume this method exists
            if (isAdmin()) {
                userService.activateUser(id); // This method should be implemented in UserService
                return ResponseEntity.ok("User account activated successfully");
            } else {
                return new ResponseEntity<>("Unauthorized: Only admin can activate users", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Example method to check if the requester is an admin (implementation needed)
    private boolean isAdmin() {
        // Implement logic to check if the current user is an admin
        return true; // Placeholder for demonstration
    }

    private final UserListService userListService;

    // Endpoint to get the list of active users
    @GetMapping("/active")
    public ResponseEntity<List<ActiveUserDTO>> getActiveUsers() {
        List<ActiveUserDTO> activeUsers = userListService.getActiveUsers();
        return ResponseEntity.ok(activeUsers);
    }
}
