
package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.dto.mapper.OrderMapper;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
import com.Corporate.Event_Sync.service.orderService.OrderListService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
public class OtherOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private MenuItemListService menuItemListService;

    @Autowired
    private OrderMapper orderMapper;

    private UserDTO loggedInUser;
    @Autowired
    private ApplicationContext applicationContext;
    private Stage primaryStage;
    @Autowired
    private OrderListService orderListService;

    public void setLoggedInUser(UserDTO userDTO) {
        this.loggedInUser = userDTO;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    private TextField nameField;
    @FXML
    private Button homeDashboard;
    @FXML
    private TableView<OrderDTO> orderTable;
    @FXML
    private TableColumn<OrderDTO, Long> orderIdColumn;
    @FXML
    private TableColumn<OrderDTO, Integer> userIdColumn;
    @FXML
    private TableColumn<OrderDTO, Integer> menuItemIdColumn;
    @FXML
    private TableColumn<OrderDTO, String> statusColumn;
    @FXML
    private TableColumn<OrderDTO, LocalDateTime> orderDateColumn;
    @FXML
    private TableColumn<OrderDTO, Void> deleteOrderColumn;
    @FXML
    private Button report;

    @FXML
    private ComboBox<Integer> orderIdComboBox; // Combo box for order IDs
    @FXML
    private ComboBox<Integer> menuItemComboBox; // Combo box for menu item IDs
    @FXML
    private ComboBox<String> statusComboBox; // Combo box for order statuses
    @FXML
    private DatePicker orderDatePicker; // Date picker for order date

    private CreateOrderController createOrderController;

    @FXML
    public void initialize() {
        // Set up columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        menuItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set up the order date column with formatting
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        orderDateColumn.setCellFactory(column -> new TableCell<OrderDTO, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd   HH:mm:ss");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String formattedTime = item.format(formatter);
                    setText(formattedTime);
                }
            }
        });

        // Set up the delete column with buttons
        deleteOrderColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<OrderDTO, Void> call(TableColumn<OrderDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction(event -> {
                            OrderDTO order = getTableView().getItems().get(getIndex());
                            deleteOrder(order); // Call your delete method
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

        // Load orders into the table
        loadOrders();
        loadMenuItems();
    }

//    public void setCreateOrderController(CreateOrderController createOrderController) {
//        this.createOrderController = createOrderController;
//        loadOrders(); // Load orders once createOrderController is available
//    }

    private void loadOrders() {
        try {
            List<OrderDTO> orderDTOs = orderMapper.toDTOList(orderListService.getAllOrders());
            List<OrderDTO> ordersWithNoLocation = orderDTOs.stream()
                    .filter(order -> order.getLatitude() != 0.0)
                    .collect(Collectors.toList());
            List<Integer> orderIds = ordersWithNoLocation.stream()
                    .map(OrderDTO::getOrderId)
                    .collect(Collectors.toList());

            orderIdComboBox.setItems(FXCollections.observableArrayList(orderIds));

            ObservableList<OrderDTO> orders = FXCollections.observableArrayList(ordersWithNoLocation);
            orderTable.setItems(orders);

            // Populate orderIdComboBox with order IDs, reversed
        } catch (Exception e) {
            showErrorDialog("Error loading orders: " + e.getMessage());
        }
    }

    private void loadMenuItems() {
        try {
            // Populate menuItemComboBox with available menu item IDs
            List<Integer> menuItemIds = menuItemListService.getAllMenuItems().stream()
                    .map(MenuItemDto::getId)
                    .collect(Collectors.toList());
            menuItemComboBox.setItems(FXCollections.observableArrayList(menuItemIds));
        } catch (Exception e) {
            showErrorDialog("Error loading menu items: " + e.getMessage());
        }
    }


    private void deleteOrder(OrderDTO order) {
        showConfirmationDialog("Are you sure you want to delete order: " + order.getOrderId() + "?", () -> {
            orderService.deleteOrderById(Long.valueOf(order.getOrderId()));
            showSuccessDialog("Deleted order: " + order.getOrderId());
            // Refresh the list after deletion
            loadOrders();
        });
    }

    // Method to get selected order ID from orderIdComboBox
    public Integer getSelectedOrderId() {
        return orderIdComboBox.getValue(); // Retrieve the selected order ID
    }

    // Update method that uses the selected order ID
    @FXML
    private void updateOrder() {
        try {
            // Validate selected order
            Integer selectedOrderId = Math.toIntExact(getSelectedOrderId());
            if (selectedOrderId == null) {
                showErrorDialog("Please select an order to update.");
                return; // Exit if validation fails
            }

            // Validate menu item
            String menuItemValue = String.valueOf(menuItemComboBox.getValue());
            if (menuItemValue == null || menuItemValue.isEmpty()) {
                showErrorDialog("Please select a menu item.");
                return; // Exit if validation fails
            }
            Integer menuItemId = Integer.valueOf(menuItemValue);

            // Validate order date
            if (orderDatePicker.getValue() == null) {
                showErrorDialog("Please select an order date.");
                return; // Exit if validation fails
            }
            String orderDate = String.valueOf(orderDatePicker.getValue().atStartOfDay());

            // Validate status
            String status = statusComboBox.getValue();
            if (status == null || status.isEmpty()) {
                showErrorDialog("Please select an order status.");
                return; // Exit if validation fails
            }

            // Perform the update operation
            orderService.updateOrderById(selectedOrderId, menuItemId, status, LocalDateTime.parse(orderDate));
            showSuccessDialog("Order updated successfully.");
            loadOrders(); // Refresh the order list after update

        } catch (Exception e) {
            // Handle unexpected errors
            showErrorDialog("PLease fill the required fields ");
        }
    }



    @FXML
    private void navigateToHome() {
        try {


            Stage currentStage = (Stage) homeDashboard.getScene().getWindow();
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
            showErrorDialog("Error generating report.");
        }
    }

    @FXML
    public void navigateToReport() {
        try {
            // Retrieve the current stage from the scene
            Stage stage = (Stage) report.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/reports.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene updateUserScene = new Scene(fxmlLoader.load());
            stage.setScene(updateUserScene);
        } catch (IOException e) {
            // Log or handle the error
            e.printStackTrace();  // Or replace with more sophisticated error handling
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
        dialogStage.initOwner(report.getScene().getWindow());
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
        dialogStage.initOwner(report.getScene().getWindow());
        dialogStage.setTitle("Confirmation");

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
