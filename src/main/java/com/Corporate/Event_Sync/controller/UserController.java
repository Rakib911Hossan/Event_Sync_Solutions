package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@AllArgsConstructor
//@NoArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

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

            // Return the user data without the password
            return ResponseEntity.ok(userDTO);
        }

        // If authentication fails
        return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }




    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update user role
//    @PutMapping("/{id}/role")
//    public ResponseEntity<String> updateUserRole(@PathVariable Integer id, @RequestParam Role role) {
//        try {
//            userService.updateUserRole(id, role);
//            return ResponseEntity.ok("User role updated successfully");
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }

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

//
//    @GetMapping("/{id}/orders")
//    public ResponseEntity<UserDTO> getUserWithOrders(@PathVariable Integer id) {
//        UserDTO userDTO = userService.getUserWithOrdersById(id);
//        return ResponseEntity.ok(userDTO);
//    }
//


}
