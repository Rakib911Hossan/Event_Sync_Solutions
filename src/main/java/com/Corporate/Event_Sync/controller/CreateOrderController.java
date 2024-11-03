package com.Corporate.Event_Sync.controller;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private MainController mainController;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private ComboBox<Integer> menuItemComboBox;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private DatePicker orderDatePicker;

    @FXML
    private Label messageLabel;



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
    private TableColumn<Order, Void> deleteOrderColumn;

    @FXML
    public void initialize() {
        // Initialize table columns for MenuItemDto
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailableTime()));

        loadMenuItems();
        statusComboBox.setItems(FXCollections.observableArrayList("ORDERED", "PREPARED", "SERVED"));
         // Initially hide the button
    }

    private void loadMenuItems() {
        try {
            List<MenuItemDto> menuItems = Optional.ofNullable(menuItemListService.getAllMenuItems())
                    .orElse(Collections.emptyList());

            if (!menuItems.isEmpty()) {
                // Populate the TableView with MenuItemDto items
                menuItemTable.setItems(FXCollections.observableArrayList(menuItems));

                // Populate menuItemComboBox with the IDs of menuItems
                List<Integer> menuItemIds = menuItems.stream().map(MenuItemDto::getId).toList();
                menuItemComboBox.setItems(FXCollections.observableArrayList(menuItemIds));
            } else {
                showAlert(Alert.AlertType.INFORMATION, "No Items", "No menu items available.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Clear any data in case of failure
            menuItemTable.getItems().clear();
            menuItemComboBox.getItems().clear();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Failed to load menu items. Please try again later.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK); // Show only OK button
        alert.showAndWait();
    }

    @FXML
    private void handleCreateOrder() {
        try {
            Integer userId = mainController.getUserId();
            Integer menuItemId = menuItemComboBox.getValue(); // Get the selected ID

            if (menuItemId == null) {
                showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a menu item ID.");
                return;
            }

            String status = statusComboBox.getValue();
            LocalDateTime orderDateTime = getOrderDateTime();

            if (orderDateTime == null) {
                showAlert(Alert.AlertType.WARNING, "Date Error", "Please select a valid date.");
                return;
            }

            orderService.createOrder(userId, menuItemId, status, orderDateTime);

            // Show success message with an OK button
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order created successfully!");
            // Show the button after successful order creation

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "User ID Error", "Invalid User ID.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Order Error", "Error creating order: " + e.getMessage());
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
    private void navigateToDashboard() {
        try {
            // Create a new stage for the dashboard and close the current one
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            Parent dashboardRoot = loader.load();
            Scene dashboardScene = new Scene(dashboardRoot);

            // Close the current stage (this is optional based on your design)
            Stage currentStage = (Stage) menuItemTable.getScene().getWindow();
            currentStage.close(); // Close current stage

            Stage newStage = new Stage(); // Create new stage for the new scene
            newStage.setScene(dashboardScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading dashboard.");
        }
    }

    @FXML
    private void switchTohandleOrderList() {
        try {
            // Load the FXML for the OrderList
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/orderList.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            // Get the controller from the loaded FXML
            OrderListController orderListController = loader.getController();

            // Pass the current instance of CreateOrderController to OrderListController
            orderListController.setOrderService(orderService);
            orderListController.setCreateOrderController(this);

            // Create a new stage for the OrderList
            Stage stage = new Stage();
            stage.setTitle("Order List");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error loading order list: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("An unexpected error occurred.");
        }
    }




    public Integer getUserId() {
        return mainController.getUserId();
    }


    @FXML
    private void handleExit() {
        Platform.exit(); // Exits the application
    }
}

