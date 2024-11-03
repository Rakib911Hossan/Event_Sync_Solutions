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

    @Autowired
    private ApplicationContext context;

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            if (userService.authenticate(email, password)) {
                UserDTO userDTO = userService.findByEmail(email);
                messageLabel.setText("Login successful! Welcome, " + userDTO.getName());
                emailField.clear();
                passwordField.clear();
                loadMainApplicationWindow(userDTO);
            } else {
                messageLabel.setText("Invalid email or password");
            }
        } catch (NotFoundException | IllegalStateException e) {
            messageLabel.setText(e.getMessage());
        } catch (Exception e) {
            messageLabel.setText("An unexpected error occurred. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    public void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/register.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Scene registerScene = new Scene(root);
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.setScene(registerScene);
            currentStage.setTitle("Register");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Failed to load the registration page.");
        }
    }

    private void loadMainApplicationWindow(UserDTO userDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setLoggedInUser(userDTO);
            Scene mainScene = new Scene(root);
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.setScene(mainScene);
            currentStage.setTitle("Main Application");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
