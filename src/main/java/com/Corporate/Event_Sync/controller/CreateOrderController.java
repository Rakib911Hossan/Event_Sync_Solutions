package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderController {

    @Autowired
    private MenuItemListService menuItemListService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private OrderService orderService;

    private Stage primaryStage;
    private HomeController homeController;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
    @FXML
    private AnchorPane orderDashboard;

    @FXML
    private ComboBox<Integer> menuItemComboBox;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private DatePicker orderDatePicker;

    @FXML
    private Label messageLabel;

    @FXML
    private TableColumn<MenuItemDto, Void> previewImageColumn;

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
    private TableColumn<MenuItemDto, Integer> priceColumn;

    @FXML
    private TableColumn<MenuItemDto, String> picColumn;

    @FXML
    private TableColumn<Order, Void> deleteOrderColumn;

    @FXML
    public void initialize() {
        // Initialize other columns
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailableTime()));
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));

        // For picColumn, display the image from URL
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

        loadMenuItems();
    }

    private void loadMenuItems() {
        try {
            List<MenuItemDto> menuItems = Optional.ofNullable(menuItemListService.getAllMenuItems())
                    .orElse(Collections.emptyList());
            menuItemTable.setItems(FXCollections.observableArrayList(menuItems));
            List<Integer> menuItemIds = menuItems.stream()
                    .map(MenuItemDto::getId) // Get order IDs
                    .collect(Collectors.toList()).reversed();
            menuItemComboBox.setItems(FXCollections.observableArrayList(menuItemIds));
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Failed to load menu items. Please try again later.");
        }
    }


    private void showLargerImage(Image image) {
        Stage imageStage = new Stage();
        imageStage.setTitle("Image Preview");

        // Create an ImageView to display the image in larger size
        ImageView largerImageView = new ImageView(image);
        largerImageView.setFitWidth(600);  // Set width of the image
        largerImageView.setFitHeight(450); // Set height of the image

        // Optionally, add the ImageView to a layout container like StackPane
        StackPane root = new StackPane();
        root.getChildren().add(largerImageView);

        // Create a Scene for the new Stage
        Scene scene = new Scene(root, 650, 500);
        imageStage.setScene(scene);

        // Show the new Stage
        imageStage.show();
    }

    @FXML
    private void handleCreateOrder() {
        try {
            Integer userId = homeController.getUserId();
            Integer menuItemId = menuItemComboBox.getValue(); // Get the selected ID

            if (menuItemId == null) {
                showErrorDialog( "Selection Error "+" Please select a menu item ID.");
                return;
            }

            String status = statusComboBox.getValue();
            LocalDateTime orderDateTime = getOrderDateTime();

            if (orderDateTime == null) {
                showErrorDialog("Date Error "+" Please select a valid date.");
                return;
            }

            orderService.createOrder(userId, menuItemId, status, orderDateTime);

            // Show success message with an OK button
            showSuccessDialog("Order created successfully!");
            // Show the button after successful order creation

        } catch (NumberFormatException e) {
            showErrorDialog("Invalid User ID.");
        } catch (Exception e) {
            showErrorDialog( "Error creating order: ");
        }
    }



    private LocalDateTime getOrderDateTime() {
        LocalDate orderDate = orderDatePicker.getValue();
        if (orderDate != null) {
            LocalTime currentTime = LocalTime.now();
            return LocalDateTime.of(orderDate, currentTime);
        }
        return null;
    }



    @FXML
    private void navigateToHome() {
        try {
            // Create a new stage for the dashboard and close the current one
            Stage currentStage = (Stage) menuItemTable.getScene().getWindow();
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

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard.");
        }
    }
 // Injected OrderService
    @FXML
    private void switchTohandleOrderList() {
        try {
            Stage currentStage = (Stage) orderDashboard.getScene().getWindow();

            // Load the OrderList FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/orderList.fxml"));
            loader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene orderListScene = new Scene(loader.load());

            // Set OrderListController properties
            OrderListController orderListController = loader.getController();
            orderListController.setOrderService(orderService);
            orderListController.setPrimaryStage(currentStage);
            orderListController.setCreateOrderController(this);

            // Set new scene and show
            currentStage.setScene(orderListScene);

        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error loading order list: " + e.getMessage());
        }
    }

    public Integer getUserId() {
        return homeController.getUserId();
    }


    @FXML
    private void handleExit() {
        Platform.exit(); // Exits the application
    }
    private void showSuccessDialog(String message) {
        showDialog(message, "#4CAF50");
    }

    private void showErrorDialog(String message) {
        showDialog(message, "#E74C3C");
    }

    private void showDialog(String message, String color) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(orderDatePicker.getScene().getWindow());
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
}

