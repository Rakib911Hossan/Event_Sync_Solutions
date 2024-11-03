package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.service.userService.UserService;
import com.Corporate.Event_Sync.utils.Role;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField departmentField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField officeIdField;

    @FXML
    private Label messageLabel;

    @FXML
    private Label dateTimeLabel;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("ADMIN", "USER"));

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            dateTimeLabel.setText(now.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    public void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String department = departmentField.getText();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue();
        Integer officeId = officeIdField.getText().isEmpty() ? null : Integer.parseInt(officeIdField.getText());

        if (name.isEmpty() || email.isEmpty() || department.isEmpty() || password.isEmpty() || selectedRole == null || officeId == null) {
            messageLabel.setText("All fields are required.");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setDepartment(department);
        user.setPassword(hashPassword(password)); // Ensure to hash the password before storing
        user.setRole(Role.valueOf(selectedRole));
        user.setOfficeId(officeId);

        // Register the user and capture the feedback message
        String registrationMessage = userService.registerUser(user);
        messageLabel.setText(registrationMessage);

        if ("Registration successful!".equals(registrationMessage)) {
            switchToLogin();
        }
    }

    private String hashPassword(String password) {
        // Implement your password hashing logic here
        return password; // Replace with hashed password
    }

    @FXML
    public void switchToLogin() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/login.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);
        } catch (IOException e) {
            messageLabel.setText("Failed to switch to login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExit() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
