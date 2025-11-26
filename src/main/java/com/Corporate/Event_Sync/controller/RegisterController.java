package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RegisterController {

    @Autowired
    private UserService userService;

    @FXML
    private Button home;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField addressField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private ComboBox<String> departmentCombobox;
    @FXML
    private TextField officeIdField;

    @FXML
    private Label dateTimeLabel;

    @Autowired
    private ApplicationContext applicationContext;

    // Add this to your initialize() method
    @FXML
    public void initialize() {
        departmentCombobox.setItems(FXCollections.observableArrayList(
                "HR", "ADMINISTRATION", "ACCOUNTS", "IT", "MARKETING", "CSE", "EEE", "BBA"
        ));
        roleComboBox.setItems(FXCollections.observableArrayList(
                "ADMIN", "USER", "STUDENT", "TEACHER", "STAFF", "DELIVERY_MAN"
        ));

        // Disable department field by default on form load
        departmentCombobox.setDisable(true);
        officeIdField.setDisable(true);

        // Add listener to role selection to enable/disable department
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Only TEACHER and STAFF can select department
                if ("TEACHER".equals(newValue) || "STAFF".equals(newValue)) {
                    departmentCombobox.setDisable(false);
                    officeIdField.setDisable(false);
                } else {
                    // For all other roles (ADMIN, USER, STUDENT, DELIVERY_MAN), disable department
                    departmentCombobox.setDisable(true);
                    officeIdField.setDisable(true);
                    departmentCombobox.setValue(null);
                }
            }
        });

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
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String department = departmentCombobox.getValue();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue();
        String officeIdText = officeIdField.getText();

        // Basic validation for all users
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRole == null) {
            showErrorDialog("Name, Email, Password, and Role are required fields");
            return;
        }

        // Validate officeId for non-ADMIN roles
        if ("TEACHER".equals(selectedRole) || "STAFF".equals(selectedRole)) {
            if (officeIdText == null || officeIdText.isEmpty()) {
                showErrorDialog("Office ID is required for " + selectedRole + " role");
                return;
            }
        }

        // Only TEACHER and STAFF can have department (optional for them)
        // All other roles should not have department

        // Parse officeId
        Integer officeId = 0;
        if (officeIdText != null && !officeIdText.isEmpty()) {
            try {
                officeId = Integer.parseInt(officeIdText);
            } catch (NumberFormatException e) {
                showErrorDialog("Office ID must be a valid number");
                return;
            }
        }

        // Create and populate user object
        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);
        user.setDepartment(department);
        user.setPassword(hashPassword(password));
        user.setRole(selectedRole);
        user.setOfficeId(officeId);

        // Register the user
        String registrationMessage = userService.registerUser(user);

        if ("Registration successful!".equals(registrationMessage)) {
            showSuccessDialog("Registration Successful!");
            switchToLogin();
        } else {
            showErrorDialog("Registration failed: " + registrationMessage);
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
            showErrorDialog("Failed to switch to login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent dashboardRoot = loader.load();
            Scene dashboardScene = new Scene(dashboardRoot);

            // Load and apply the CSS file to the new scene
            String css = getClass().getResource("/com.Corporate.Event_Sync/style.css").toExternalForm();
            dashboardScene.getStylesheets().add(css);

            // Set the new scene to the current stage
            Stage currentStage = (Stage) home.getScene().getWindow();
            currentStage.setScene(dashboardScene);
        } catch (IOException e) {
            showErrorDialog("Failed to switch to home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSuccessDialog(String message) {
        showDialog(message, "#4CAF50");
    }

    private void showErrorDialog(String message) {
        showDialog(message, "#E74C3C");
    }

    private void showDialog(String message, String color) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(nameField.getScene().getWindow());
        dialogStage.setTitle("Message");

        // Dialog content
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 20px;");
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-size: 14px;");
        okButton.setOnAction(event -> dialogStage.close());

        VBox dialogLayout = new VBox(10, messageLabel, okButton);
        dialogLayout.setStyle("-fx-background-color: #2C3E50; -fx-padding: 20px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        dialogLayout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene dialogScene = new Scene(dialogLayout);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }

    @FXML
    public void handleExit() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
