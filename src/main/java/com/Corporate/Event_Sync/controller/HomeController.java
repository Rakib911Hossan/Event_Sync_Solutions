package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Component
public class HomeController {

    @FXML
    private Button exit;
    @FXML
    private Button handleHome;
    @FXML
    private Button logOut;
    @FXML
    private Button allUser;
    @FXML
    private Button allOrder;
    @FXML
    private Button otherOrder;
    @FXML
    private Button createOrder;
    @FXML
    private Button updateUserDashboard;
    @FXML
    private Button handleWeekDays;
    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label officeIdLabel;


    @FXML
    private Button allMenuItems;

    @FXML
    private ImageView userImageView;

    private UserDTO loggedInUser;

    @Autowired
    private OrderService orderService;
    private UserService userService;
    @Autowired
    private ApplicationContext applicationContext;

    private UserDTO fetchLoggedInUser() {
        Integer userId = getUserId(); // Assume this method fetches the logged-in user's ID
        return userService.getUserById(userId);
    }
    @FXML
    private Label currentDateTimeLabel; // New label to display current date and time


    @FXML
    public void initialize() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            currentDateTimeLabel.setText(now.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }


    public UserDTO getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserDTO userDTO) {
        this.loggedInUser = userDTO;

        // Set the text for each label based on user data
        nameLabel.setText(userDTO.getName());

        // Check if phone is null, and if so, set a default value or leave it empty
        phoneLabel.setText(userDTO.getPhone() != null ? userDTO.getPhone() : "N/A"); // Or an empty string if preferred

        emailLabel.setText(userDTO.getEmail());

        // Check if address is null, and if so, set a default value or leave it empty
        addressLabel.setText(userDTO.getAddress() != null ? userDTO.getAddress() : "N/A"); // Or an empty string if preferred

        departmentLabel.setText(userDTO.getDepartment());
        officeIdLabel.setText(String.valueOf(userDTO.getOfficeId()));

//        updateUserDashboard.setVisible(!"USER".equals(userDTO.getRole()));
        allUser.setVisible(!"USER".equals(userDTO.getRole()));
        allOrder.setVisible(!"USER".equals(userDTO.getRole()));
        allMenuItems.setVisible(!"USER".equals(userDTO.getRole()));
        updateImageView();
    }
    private void updateImageView() {
        if (loggedInUser != null && loggedInUser.getUserPic() != null && !loggedInUser.getUserPic().isEmpty()) {
            try {
                // Load the image from the file path in UserDTO
                Image image = new Image(new java.io.File(loggedInUser.getUserPic()).toURI().toString());
                userImageView.setImage(image); // Display the image
            } catch (Exception e) {
                System.out.println("Error loading user image: " + e.getMessage());
            }
        } else {
            userImageView.setImage(null); // Clear image if no path is set
        }
    }

    // New method to get userId
    public Integer getUserId() {
        return loggedInUser != null ? loggedInUser.getId() : null;
    }
    public String getUserRole() {
        return loggedInUser != null ? loggedInUser.getRole() : null;
    }

    // Button action handlers
    @FXML
    private void handleLogout() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            loader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene homeScene = new Scene(loader.load());
            // Set the scene to home page
            Stage currentStage = (Stage) logOut.getScene().getWindow();
            currentStage.setScene(homeScene);

            showSuccessDialog("User logged out successfully and redirected to the home page");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error", "Could not load the home page. Please try again.");
        }
    }


    @FXML
    private void handleHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            Parent root = loader.load();

            // Optionally, pass any necessary controllers or data to the new view
            Stage stage = (Stage) handleHome.getScene().getWindow();
            stage.setScene(new Scene(root));
//            stage.setTitle("Home");
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error message to the user
        }
    }


    @FXML
    public void userDetails() {
        try {
            // Retrieve the current stage from the scene
            Stage stage = (Stage) updateUserDashboard.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/updateUser.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene updateUserScene = new Scene(fxmlLoader.load());
            stage.setScene(updateUserScene);
        } catch (IOException e) {
            // Log or handle the error
            e.printStackTrace();  // Or replace with more sophisticated error handling
        }
    }

    @FXML
    public void handleWeekDays() {
        try {
            Stage stage = (Stage) handleWeekDays.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/weekDays.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene weekDays = new Scene(fxmlLoader.load());
            stage.setScene(weekDays);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void switchToCreateOrder() {
        try {
            // Get the current stage
            Stage currentStage = (Stage) createOrder.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/createOrder.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Scene createOrderScene = new Scene(loader.load());
            CreateOrderController createOrderController = loader.getController();
            createOrderController.setOrderService(orderService);
            createOrderController.setPrimaryStage(currentStage);
            createOrderController.setHomeController(this);
            currentStage.setScene(createOrderScene);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading Create Order window." + "Could not load the Create Order window. Please try again.");
        }
    }


    @FXML
    public void allUser(){

        try {
            Stage stage = (Stage) allUser.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/allUsers.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);
        } catch (IOException e) {

            e.printStackTrace();
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
    private void allOrder() {
        try {
            Stage stage = (Stage) allOrder.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/universityOrders.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene allOrder = new Scene(fxmlLoader.load());
            stage.setScene(allOrder);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void otherOrder() {
        try {
            Stage stage = (Stage) allOrder.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/otherOrders.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene allOrder = new Scene(fxmlLoader.load());
            stage.setScene(allOrder);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    public void allMenuItems() {
        try {
            Stage stage = (Stage) allMenuItems.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/allMenuItems.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene allMenuItems = new Scene(fxmlLoader.load());
            stage.setScene(allMenuItems);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        // Logic to exit the application
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
        System.out.println("Application exited");
    }

    private void showSuccessDialog(String message) {
        showDialog(message, "#4CAF50");
    }
    private void showErrorDialog(String message) {
        showDialog(message, "#E74C3C");
    }
    private void showDialog(String message, String color) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(nameLabel.getScene().getWindow());
        dialogStage.setTitle("Message");

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
}
