package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemService;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemUpdateService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Component
public class AllMenuItemsController {

    // Define FXML elements
    @FXML
    private ComboBox<String> catagoryComboBox;

    @FXML
    private ComboBox<String> availableTimeComboBox;
    @FXML
    private Button logOut;
    @FXML
    private Label messageLabel;

    @FXML
    private Button goToDashboardButton;

    @FXML
    private TableView<MenuItemDto> menuItemTable;

    @FXML
    private TableColumn<MenuItemDto, Integer> idColumn;
    @FXML
    private TableColumn<MenuItemDto, String> nameColumn;
    @FXML
    private TableColumn<MenuItemDto, String> descriptionColumn;
    @FXML
    private TableColumn<MenuItemDto, String> categoryColumn;
    @FXML
    private TableColumn<MenuItemDto, String> timeColumn;
    @FXML
    private TableColumn<MenuItemDto, String> imageColumn;
    @FXML
    private TableColumn<MenuItemDto, Void> previewImageColumn;  // New column for preview button
    @FXML
    private TableColumn<MenuItemDto, Void> deleteMenuColumn;

    @FXML
    private TextField itemNameTextField, descriptionTextField, itemImageTextField,priceTextField;

    @FXML
    private Button chooseImageButton;

    @Autowired
    private MenuItemListService menuItemListService;
    @Autowired
    private MenuItemService menuItemService;
    @Autowired
    private MenuItemUpdateService menuItemUpdateService;

    @FXML
    public void initialize() {
        // Initialize columns for TableView
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailableTime()));

        // Setup the image column to display images
        previewImageColumn.setCellFactory(new Callback<TableColumn<MenuItemDto, Void>, TableCell<MenuItemDto, Void>>() {
            @Override
            public TableCell<MenuItemDto, Void> call(TableColumn<MenuItemDto, Void> param) {
                return new TableCell<MenuItemDto, Void>() {
                    private final Button previewButton = new Button("Preview");

                    {
                        previewButton.setOnAction(event -> {
                            MenuItemDto menuItemDto = getTableView().getItems().get(getIndex());
                            String imagePath = menuItemDto.getItemPic();
                            if (imagePath != null && !imagePath.isEmpty()) {
                                showLargerImage(new Image("file:" + imagePath)); // Show the preview image
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(previewButton);
                        }
                    }
                };
            }
        });


        // Setup delete button
        deleteMenuColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MenuItemDto, Void> call(TableColumn<MenuItemDto, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction(event -> {
                            MenuItemDto menuItemDto = getTableView().getItems().get(getIndex());
                            deleteMenuItem(menuItemDto); // Call delete method
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        });

        loadMenuItems();

        catagoryComboBox.setItems(FXCollections.observableArrayList("BREAKFAST", "LUNCH", "SNACKS", "DINNER"));
    }

    private void showLargerImage(Image image) {
        Stage imageStage = new Stage();
        imageStage.setTitle("Image Preview");

        // Create an ImageView to display the image in larger size
        ImageView largerImageView = new ImageView(image);
        largerImageView.setFitWidth(600);  // Set width of the image
        largerImageView.setFitHeight(450); // Set height of the image
        StackPane root = new StackPane();
        root.getChildren().add(largerImageView);

        // Create a Scene for the new Stage
        Scene scene = new Scene(root, 650, 500);
        imageStage.setScene(scene);

        // Show the new Stage
        imageStage.show();
    }

    private void deleteMenuItem(MenuItemDto menuItemDto) {
        showConfirmationDialog("Are you sure you want to delete order: " + menuItemDto.getItemName() + "?", () -> {
        menuItemUpdateService.deleteMenuItem(menuItemDto.getId());
            showSuccessDialog("Menu item: " + menuItemDto.getItemName() + " deleted successfully");
        loadMenuItems(); // Refresh the list after deletion
        });
    }

    private void loadMenuItems() {
        try {
            List<MenuItemDto> menuItems = Optional.ofNullable(menuItemListService.getAllMenuItems())
                    .orElse(Collections.emptyList());
            menuItemTable.setItems(FXCollections.observableArrayList(menuItems));
        } catch (Exception e) {
            showErrorDialog("Loading Error" + "Failed to load menu items. Please try again later.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
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

    // Button action handlers
    @FXML
    private void handleExit() {
        System.exit(0); // Code to handle exit action
    }

    @FXML
    private void handleHome() {
        try {
            // Navigate back to the main dashboard
            Stage currentStage = (Stage) goToDashboardButton.getScene().getWindow();
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
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        fileChooser.setTitle("Choose Image");

        Stage stage = (Stage) chooseImageButton.getScene().getWindow();

        java.io.File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            itemImageTextField.setText(file.getAbsolutePath()); // Set file path to text field
        }
    }

    @FXML
    private void createItem() {
        try {
            String itemName = itemNameTextField.getText();
            String description = descriptionTextField.getText();
            String category = catagoryComboBox.getValue();
            String availableTime = availableTimeComboBox.getValue();
            String itemImage = itemImageTextField.getText();
            String priceText = priceTextField.getText();
            Integer price = Math.toIntExact(priceText.isEmpty() ? 0L : Long.parseLong(priceText)); // Default to 0 if empty

            // Set itemImage to null if the field is empty
            if (itemImage == null || itemImage.trim().isEmpty()) {
                itemImage = null; // Ensures null value is passed if no image selected
            }

            MenuItemDto menuItemDto = new MenuItemDto(null, itemName, description, category, availableTime, itemImage, price);

            menuItemService.createMenuItem(menuItemDto);

            showSuccessDialog("Item created successfully!");
            loadMenuItems(); // Reload the menu items to update the table view
        } catch (Exception e) {
            showErrorDialog("Creation Error " + "Failed to create menu item. Please try again.");
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
        dialogStage.initOwner(chooseImageButton.getScene().getWindow());
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
        dialogStage.initOwner(chooseImageButton.getScene().getWindow());
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
