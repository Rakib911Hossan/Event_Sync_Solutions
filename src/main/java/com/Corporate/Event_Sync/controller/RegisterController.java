package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.service.userService.UserService;
import com.Corporate.Event_Sync.utils.Role;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

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
    private ComboBox<String> roleComboBox; // Change to String to match FXML

    @FXML
    private TextField officeIdField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        // Populate the role combo box with string values
        roleComboBox.setItems(FXCollections.observableArrayList("ADMIN", "USER"));
    }

    @FXML
    public void handleRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String department = departmentField.getText();
        String password = passwordField.getText();
        String selectedRole = roleComboBox.getValue(); // Get the selected role as String
        Integer officeId = officeIdField.getText().isEmpty() ? null : Integer.parseInt(officeIdField.getText());

        // Check for empty fields
        if (name.isEmpty() || email.isEmpty() || department.isEmpty() || password.isEmpty() || selectedRole == null || officeId == null) {
            messageLabel.setText("All fields are required.");
            return;
        }

        // Create a new User object
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setDepartment(department);
        user.setPassword(password);
        user.setRole(Role.valueOf(selectedRole)); // Convert string to Role enum
        user.setOfficeId(officeId);

        try {
            // Register the user
            userService.registerUser(user);
            messageLabel.setText("Registration successful! Redirecting to login...");
            Stage stage = (Stage) nameField.getScene().getWindow();
            switchToLogin(stage);
        } catch (Exception e) {
            messageLabel.setText("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchToLogin(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/login.fxml"));
        fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
        Scene loginScene = new Scene(fxmlLoader.load());
        stage.setScene(loginScene);
    }
}
