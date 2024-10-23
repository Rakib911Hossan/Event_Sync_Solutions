package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.UserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class MainController {
    @FXML
    private VBox userDashboard;  // This will hold the user details

    private UserDTO loggedInUser;

    public void setLoggedInUser(UserDTO userDTO) {
        this.loggedInUser = userDTO;

        // Create labels for user data
        Label nameLabel = new Label("Name: " + userDTO.getName());
        Label departmentLabel = new Label("Department: " + userDTO.getDepartment());
        Label emailLabel = new Label("Email: " + userDTO.getEmail());
        Label officeIdLabel = new Label("Office ID: " + userDTO.getOfficeId());
        Label activeLabel = new Label("Active: " + (userDTO.getIsActive() ? "Yes" : "No"));

        // Clear previous data
        userDashboard.getChildren().clear();

        // Add user data labels to the dashboard
        userDashboard.getChildren().addAll(nameLabel, departmentLabel, emailLabel, officeIdLabel, activeLabel);
    }
}
