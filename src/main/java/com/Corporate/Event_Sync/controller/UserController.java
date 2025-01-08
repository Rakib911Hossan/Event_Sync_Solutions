//package com.Corporate.Event_Sync.controller;
//
//import com.Corporate.Event_Sync.dto.ActiveUserDTO;
//import com.Corporate.Event_Sync.dto.UserDTO;
//import com.Corporate.Event_Sync.entity.User;
//import com.Corporate.Event_Sync.service.userService.UserService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@AllArgsConstructor
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private final UserService userService;
//
//    // Register a new user
//    @PostMapping("/register")
//    public ResponseEntity<User> registerUser(@RequestBody User user) {
//        User registeredUser = userService.registerUser(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
//    }
//
//    // Authenticate user (login)
//    @PostMapping("/login")
//    public ResponseEntity<String> authenticate(@RequestParam String email, @RequestParam String password) {
//        boolean isAuthenticated = userService.authenticate(email, password);
//        if (isAuthenticated) {
//            return ResponseEntity.ok("User authenticated successfully.");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
//        }
//    }
//
//    // Find user by email
//    // Get a user by email
//    @GetMapping("/email/{email}")
//    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
//        UserDTO userDTO = userService.findByEmail(email);
//        return ResponseEntity.ok(userDTO);
//    }
//
//    // Get user by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
//        UserDTO userDTO = userService.getUserById(id); // Ensure this method returns UserDTO
//        return ResponseEntity.ok(userDTO);
//    }
//
//    // Get user by ID and map to ActiveUserDTO
//    @GetMapping("/{id}/dto")
//    public ResponseEntity<ActiveUserDTO> getUserByIdUserDto(@PathVariable Integer id) {
//        ActiveUserDTO activeUserDTO = userService.getUserByIdUserDto(id);
//        return ResponseEntity.ok(activeUserDTO);
//    }
//
//    // Deactivate user account
//    @PutMapping("/{id}/deactivate")
//    public ResponseEntity<Void> deactivateUser(@PathVariable Integer id) {
//        userService.deactivateUser(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // Activate user account
//    @PutMapping("/{id}/activate")
//    public ResponseEntity<String> activateUser(@PathVariable Integer id) {
//        try {
//            userService.activateUser(id);
//            return ResponseEntity.ok("User account activated.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//        }
//    }
//}
