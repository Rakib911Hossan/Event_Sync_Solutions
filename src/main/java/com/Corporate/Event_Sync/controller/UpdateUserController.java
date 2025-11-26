package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.dto.mapper.UserMapper;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Data
@Component
public class UpdateUserController {

    @FXML
    private Button chooseImageButton;
    @FXML
    private TextField itemImageTextField;
    @FXML
    private Button logOut;

    private final UserService userService;
    private final UserMapper userMapper;
    private final ApplicationContext applicationContext;
    private final HomeController homeController;
    private UserDTO loggedInUser;

    public UpdateUserController(UserService userService, UserMapper userMapper, ApplicationContext applicationContext, HomeController homeController) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.applicationContext = applicationContext;
        this.homeController = homeController;
    }

    @FXML private Label nameLabel, phoneLabel, departmentLabel, emailLabel, officeIdLabel, addressLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button handleUserDetailsButton, handleLogoutButton, handleHomeButton, handleExitButton;
    @FXML private TextField name, phone, address, officeId;

    @FXML
    public void initialize() {
        loadUserDetails();  // Load user details on initialization
        departmentCombo();   // Load department options in ComboBox
    }


    private void updateLabels() {
        if (loggedInUser != null) {
            nameLabel.setText(loggedInUser.getName());
            phoneLabel.setText(loggedInUser.getPhone() != null ? loggedInUser.getPhone() : "N/A");
            departmentLabel.setText(loggedInUser.getDepartment() );
            emailLabel.setText(loggedInUser.getEmail() );
            officeIdLabel.setText(String.valueOf(loggedInUser.getOfficeId()));
            addressLabel.setText(loggedInUser.getAddress() != null ? loggedInUser.getAddress() : "N/A");
//            userPic.setText(itemImageTextField != null ? loggedInUser.getUserPic() : "N/A");
        } else {
            showErrorDialog("No logged-in user data to update labels.");
        }
    }

    private void loadUserDetails() {
        UserDTO user = homeController.getLoggedInUser();
        if (user != null) {
            setLoggedInUser(user);  // Update the labels with user details
        } else {
            showErrorDialog("No user data found in HomeController.");
        }
    }

    private void departmentCombo() {
        statusComboBox.setItems(FXCollections.observableArrayList("HR", "ADMINISTRATION", "ACCOUNTS", "IT", "MARKETING"));
    }

//    @FXML
//    private void handleUserDetails() {
//        System.out.println("Updating user details...");
//        // Logic for handling user details update
//    }

    @FXML
    private void updateUser() {
        if (loggedInUser == null) {
            showSuccessDialog("No logged-in user to update.");
            return;
        }

        // Retrieve data from input fields
        String updatedName = name.getText();
        String updatedPhone = phone.getText();
        String updatedAddress = address.getText();
        String updatedOfficeId = officeId.getText();
        String updatedDepartment = statusComboBox.getValue();
        String updateUserPic = itemImageTextField.getText();

        // Update loggedInUser DTO with new values
        loggedInUser.setName(updatedName.isEmpty() ? null : updatedName);
        loggedInUser.setPhone(updatedPhone.isEmpty() ? null : updatedPhone);
        loggedInUser.setAddress(updatedAddress.isEmpty() ? null : updatedAddress);
//        loggedInUser.setOfficeId(updatedOfficeId.isEmpty() ? null : Integer.parseInt(updatedOfficeId));
        if (updatedOfficeId.isEmpty()) {
            loggedInUser.setOfficeId(null); // No value entered, set as null
        } else if (!updatedOfficeId.matches("\\d+")) { // Check if not numeric
            showErrorDialog("Invalid Office ID: must be a numeric value.");
            return; // Exit the method
        } else {
            loggedInUser.setOfficeId(Integer.parseInt(updatedOfficeId)); // Safe parsing
        }
        loggedInUser.setDepartment(updatedDepartment);
        loggedInUser.setUserPic(updateUserPic.isEmpty() ? null : updateUserPic);

        try {
            UserDTO updatedUser = userService.updateUser(loggedInUser);
            showSuccessDialog("User updated successfully: " + updatedUser.getName());

            setLoggedInUser(updatedUser); // Update UI labels and image with new data

        } catch (Exception e) {
            showErrorDialog("Failed to update user: " + e.getMessage());
        }
    }
    @FXML
    public void setLoggedInUser(UserDTO userDTO) {
        System.out.println("Setting logged-in user data...");
        System.out.println("UserDTO: " + userDTO);  // Print userDTO to verify its contents
        this.loggedInUser = userDTO;
        updateLabels();
        updateImageView(); // Load profile image if available
    }

    private void updateImageView() {
        if (loggedInUser != null && loggedInUser.getUserPic() != null && !loggedInUser.getUserPic().isEmpty()) {
            try {
                // Load the image from the file path in UserDTO
                Image image = new Image(new java.io.File(loggedInUser.getUserPic()).toURI().toString());
                userImageView.setImage(image); // Display the image
            } catch (Exception e) {
                showErrorDialog("Error loading user image: " + e.getMessage());
            }
        } else {
            userImageView.setImage(null); // Clear image if no path is set
        }
    }


    @FXML
    private void handleHome() {
        try {
            // Navigate back to the main dashboard
            Stage currentStage = (Stage) handleHomeButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/home.fxml"));
            loader.setControllerFactory(EventSyncApplication.context::getBean);
            // Load the main scene
            Scene homeScene = new Scene(loader.load());
            // Get the HomeController instance and pass the updated user data
            HomeController homeController = loader.getController();
            if (homeController != null && loggedInUser != null) {
                homeController.setLoggedInUser(loggedInUser);
            }

            currentStage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private ImageView userImageView;

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        fileChooser.setTitle("Choose Image");

        Stage stage = (Stage) chooseImageButton.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            itemImageTextField.setText(file.getAbsolutePath()); // Set file path to text field
            try {
                Image image = new Image(file.toURI().toString());
                userImageView.setImage(image); // Display the selected image
            } catch (Exception e) {
//                showErrorDialog("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Show a success alert dialog for logging out
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Logout Successful");
            alert.setHeaderText(null);
            alert.setContentText("You have been logged out successfully.");
            alert.showAndWait();  // Show the alert and wait for user confirmation

            // Load the home page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            loader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene homeScene = new Scene(loader.load());
            // Set the scene to home page
            Stage currentStage = (Stage) logOut.getScene().getWindow();
            currentStage.setScene(homeScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) handleExitButton.getScene().getWindow();
        stage.close(); // Close the application
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

