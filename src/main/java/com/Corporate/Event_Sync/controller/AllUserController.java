package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.service.userService.UserListService;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

@Controller
public class AllUserController {
    public AllUserController(UserService userService, UserListService userListService) {
        this.userService = userService;
        this.userListService = userListService;
    }

    @FXML
    private Button logOut;
    @FXML
    private Button home;
    @FXML
    private TableView<UserDTO> userTable;
    @FXML
    private TableColumn<UserDTO, Integer> userIdColumn;
    @FXML
    private TableColumn<UserDTO, String> userNameColumn;
    @FXML
    private TableColumn<UserDTO, String> phoneColumn;
    @FXML
    private TableColumn<UserDTO, String> emailColumn;
    @FXML
    private TableColumn<UserDTO, String> addressColumn;
    @FXML
    private TableColumn<UserDTO, String> departmentColumn;
    @FXML
    private TableColumn<UserDTO, Integer> officeIdColumn;
    @FXML
    private TableColumn<UserDTO, Void> deleteUserColumn;
    @FXML private TextField name, phone, address, officeId;
    @Autowired
    private UserListService userListService;
    private UserService userService;
    private ApplicationContext applicationContext;
    @FXML
    private ComboBox<String> departmentComboBox;
    @FXML
    private ComboBox<Integer> userIdComboBox;
    public void initialize() {
        // Populate department options
        departmentComboBox.setItems(FXCollections.observableArrayList("HR", "ADMIN", "ACCOUNTS", "IT", "MARKETING"));

        // Bind data to table columns
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
        officeIdColumn.setCellValueFactory(new PropertyValueFactory<>("officeId"));

        // Configure delete button for each row
        deleteUserColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    UserDTO user = getTableView().getItems().get(getIndex());
                    deleteUserById(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        // Load user data
        loadUserData();

        // Populate userIdComboBox with user IDs
        populateUserIdComboBox();
    }

    private void populateUserIdComboBox() {
        List<UserDTO> users = userListService.getActiveUsers();
        userIdComboBox.setItems(FXCollections.observableArrayList(users.stream()
                .map(UserDTO::getId)
                .toList()));
    }


    private void loadUserData() {
        List<UserDTO> users = userListService.getActiveUsers();
        userTable.setItems(FXCollections.observableArrayList(users));
    }

    public Integer getSelectedUserId() {
        return userIdComboBox.getValue(); // Retrieve the selected order ID
    }

    @FXML
    private void updateUser() {
        Integer selectedUserId = getSelectedUserId();
        if (selectedUserId == null) {
            showErrorDialog("No user selected for update.");
            return;
        }

        // Retrieve user by selected ID
        UserDTO userToUpdate = userListService.getUserById(selectedUserId); // Implement this method in UserListService
        if (userToUpdate == null) {
            showErrorDialog("User not found.");
            return;
        }

        // Retrieve data from input fields

        String updatedName = name.getText();
        String updatedPhone = phone.getText();
        String updatedAddress = address.getText();
        String updatedOfficeId = officeId.getText();
        String updatedDepartment = departmentComboBox.getValue();

        // Update fields
        userToUpdate.setName(updatedName.isEmpty() ? null : updatedName);
        userToUpdate.setPhone(updatedPhone.isEmpty() ? null : updatedPhone);
        userToUpdate.setAddress(updatedAddress.isEmpty() ? null : updatedAddress);
        userToUpdate.setOfficeId(updatedOfficeId.isEmpty() ? null : Integer.parseInt(updatedOfficeId));
        userToUpdate.setDepartment(updatedDepartment);

        try {
            // Update user in UserService
            UserDTO updatedUser = userService.updateUser(userToUpdate);
            showSuccessDialog("User updated successfully: " + updatedUser);
            // Refresh displayed data if needed
            loadUserData();
                 } catch (Exception e) {
            showErrorDialog("Failed to update user: " + e.getMessage());

        }
    }

    @FXML
    private void handleHome() {
        try {
            Stage currentStage = (Stage) home.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/home.fxml"));
            loader.setControllerFactory(EventSyncApplication.context::getBean);
            // Load the main scene
            Scene homeScene = new Scene(loader.load());
            // Get the HomeController instance and pass the updated user data
            HomeController homeController = loader.getController();
            if (homeController != null && homeController.getLoggedInUser() != null) {
                homeController.setLoggedInUser(homeController.getLoggedInUser());
            }
            currentStage.setScene(homeScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            showSuccessDialog("You have been logged out successfully.");
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
    private void deleteUserById(UserDTO user) {
        // Show a confirmation dialog before deleting the user
        showConfirmationDialog("Are you sure you want to delete user: " + user.getId() + "?", () -> {
            // Deletion logic
            userService.deleteUserById(user.getId());
            showSuccessDialog("Deleted user: " + user.getId());
            // Reload data after deletion
            loadUserData();
        });
    }
    private void showSuccessDialog(String message) {
        showDialog(message, "#4CAF50");
    }

    private void showErrorDialog(String message) {
        showDialog(message, "#E74C3C");
    }

    private void showDialog(String message, String color) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(home.getScene().getWindow());
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

    private void showConfirmationDialog(String message, Runnable onConfirm) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(home.getScene().getWindow());
        dialogStage.setTitle("Confirmation");

        // Confirmation dialog content
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px; -fx-padding: 20px;");
        Button yesButton = new Button("Yes");
        yesButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-size: 14px;");
        yesButton.setOnAction(event -> {
            dialogStage.close(); // Close the dialog
            onConfirm.run();    // Execute the confirm action
        });

        Button noButton = new Button("No");
        noButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-size: 14px;");
        noButton.setOnAction(event -> dialogStage.close());

        HBox buttonLayout = new HBox(10, yesButton, noButton);
        buttonLayout.setAlignment(javafx.geometry.Pos.CENTER);

        VBox dialogLayout = new VBox(10, messageLabel, buttonLayout);
        dialogLayout.setStyle("-fx-background-color: #2C3E50; -fx-padding: 20px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        dialogLayout.setAlignment(javafx.geometry.Pos.CENTER);

        Scene dialogScene = new Scene(dialogLayout);
        dialogStage.setScene(dialogScene);
        dialogStage.show();
    }
}
