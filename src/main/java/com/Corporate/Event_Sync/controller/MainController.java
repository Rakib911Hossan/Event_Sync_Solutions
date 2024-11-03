package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MainController {

    @FXML
    private VBox userDashboard;

    @FXML
    private Label nameLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label officeIdLabel;

    @FXML
    private Label activeLabel;

    private UserDTO loggedInUser;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ApplicationContext applicationContext;

    public void setLoggedInUser(UserDTO userDTO) {
        this.loggedInUser = userDTO;

        // Set the text for each label based on user data
        nameLabel.setText(userDTO.getName());
        departmentLabel.setText(userDTO.getDepartment());
        emailLabel.setText(userDTO.getEmail());
        officeIdLabel.setText(String.valueOf(userDTO.getOfficeId()));
        activeLabel.setText(userDTO.getIsActive() ? "Yes" : "No");
    }

    // New method to get userId
    public Integer getUserId() {
        return loggedInUser != null ? loggedInUser.getId() : null;
    }

    // Button action handlers
    @FXML
    private void handleLogout() {
        // Logic for logging out the user
        System.out.println("User logged out");
    }

    @FXML
    private void handleHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/home.fxml"));
            Parent root = loader.load();

            // Optionally, pass any necessary controllers or data to the new view
            Stage stage = (Stage) userDashboard.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error message to the user
        }
    }

    @FXML
    private void handleDashboard() {
        navigateToDashboard();
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            Parent dashboardRoot = loader.load();

            // If you need to pass the logged-in user to the dashboard controller, do it here
            MainController dashboardController = loader.getController();
            if (dashboardController != null && loggedInUser != null) {
                dashboardController.setLoggedInUser(loggedInUser);
            }

            Stage stage = (Stage) userDashboard.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void handleUserDetails() {
        // Logic to show user details
        System.out.println("Showing user details");
    }

    @FXML
    private void switchToCreateOrder() {
        try {
            // Load the FXML file for the Create Order view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/createOrder.fxml"));
            loader.setControllerFactory(applicationContext::getBean);

            // Get the root node from the loader
            Parent root = loader.load();

            // Get the CreateOrderController from the loader
            CreateOrderController createOrderController = loader.getController();

            // Pass services and stage
            createOrderController.setOrderService(orderService);
            createOrderController.setPrimaryStage((Stage) userDashboard.getScene().getWindow());
            createOrderController.setMainController(this);

            // Close the current dashboard stage
            Stage currentStage = (Stage) userDashboard.getScene().getWindow();
            currentStage.close();

            // Create a new Stage for the Create Order window
            Stage stage = new Stage();
            stage.setTitle("Create Order");
            stage.setScene(new Scene(root)); // Set the scene to the root node
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // Show the new window and wait until it's closed before returning focus

        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Create Order window.", "Could not load the Create Order window. Please try again.");
        }
    }






    // Utility method to show an error dialog
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void handleYourOrders() {
        // Logic to show the user's orders
        System.out.println("Showing your orders");
    }

    @FXML
    private void handleExit() {
        // Logic to exit the application
        Stage stage = (Stage) userDashboard.getScene().getWindow();
        stage.close();
        System.out.println("Application exited");
    }
}
