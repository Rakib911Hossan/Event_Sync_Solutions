package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @Autowired
    private UserService userService;

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            // Authenticate the user
            if (userService.authenticate(email, password)) {
                // Retrieve the user data after successful authentication
                UserDTO userDTO = userService.findByEmail(email);

                // Show a success message
                messageLabel.setText("Login successful! Welcome, " + userDTO.getName());

                // Here you can update the UI with user data or transition to another window
                // For example, you might want to pass the userDTO to another scene/controller
                loadMainApplicationWindow(userDTO); // Method to transition to another window

            } else {
                // Show an error message for invalid credentials
                messageLabel.setText("Invalid email or password");
            }
        } catch (NotFoundException e) {
            messageLabel.setText(e.getMessage());
        }
    }
    @Autowired
    private ApplicationContext context;

    private void loadMainApplicationWindow(UserDTO userDTO) {
        try {
            // Load the main application FXML file using Spring context
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));

            // Set the Spring context as the controller factory
            loader.setControllerFactory(context::getBean);

            // Load the main scene
            Parent root = loader.load();

            // Get the controller of the new scene
            MainController mainController = loader.getController();

            // Pass the logged-in user data to the new controller
            mainController.setLoggedInUser(userDTO);

            // Create a new scene and set it on the current stage
            Scene mainScene = new Scene(root);
            Stage currentStage = (Stage) emailField.getScene().getWindow();  // Get the current stage
            currentStage.setScene(mainScene);  // Set the new scene
            currentStage.setTitle("Main Application");  // Update the title
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();  // Handle exception
        }
    }



}

