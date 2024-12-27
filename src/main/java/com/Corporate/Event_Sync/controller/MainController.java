package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MainController {

    @Autowired
    private ApplicationContext applicationContext;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    private Label messageLabel;

    @FXML
    private AnchorPane anchorPane;  // Reference to the AnchorPane

    @FXML
    private Label dashboardLabel;

    @FXML
    private Button logIn;
    @FXML
    private Button about;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Button register;// Reference to the Dashboard Label

    // Initialize method to set up any default states or configurations
    @FXML
    public void initialize() {
        // You can add any initialization logic here

        dashboardLabel.setText("Welcome to Event Sync Solutions!");

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            dateTimeLabel.setText(now.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    // Action handler for the Exit Button
    @FXML
    private void handleExit() {
        // Logic to handle exit action, e.g., closing the application
        System.exit(0);
    }

    // Action handler for the Home Button
    @FXML
    private void switchToRegister() {
        try {

            Stage stage = (Stage) register.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/register.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);
            String css = getClass().getResource("/com.Corporate.Event_Sync/style.css").toExternalForm();
            loginScene.getStylesheets().add(css);

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading order list: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("An unexpected error occurred.");
        }
    }

    // Action handler for User Details Button
    @FXML
    private void handleUserDetails() {
        // Logic for handling user details
        System.out.println("User Details button clicked!");
    }

    // Action handler for Create Order Button
    @FXML
    private void switchToLogin() {
        try {

            Stage stage = (Stage) logIn.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/login.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading order list: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("An unexpected error occurred.");
        }
    }

    // Action handler for Your Orders Button
    @FXML
    private void toAbout() {
        // Logic for handling user's orders
        try {

            Stage stage = (Stage) about.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/about.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading order list: " + e.getMessage());
    }
}}
