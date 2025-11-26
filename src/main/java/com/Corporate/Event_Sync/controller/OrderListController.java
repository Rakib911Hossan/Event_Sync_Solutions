package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.dto.mapper.OrderMapper;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Component
public class OrderListController {

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

    public void setLoggedInUser(UserDTO userDTO) {
        this.loggedInUser = userDTO;
    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    private Button createOrderButton;
    @FXML
    private Button updateOrderButton;
    @FXML
    private Button homeDashboard;
    @FXML
    private TableView<OrderDTO> orderTable;
    @FXML
    private TableColumn<OrderDTO, Long> orderIdColumn;
    @FXML
    private TableColumn<OrderDTO, Integer> menuItemIdColumn;
    @FXML
    private TableColumn<OrderDTO, String> statusColumn;
    @FXML
    private TableColumn<OrderDTO, LocalDateTime> orderDateColumn;

    @FXML
    private TableColumn<OrderDTO, Integer> priceColumn;

    @FXML
    private TableColumn<OrderDTO, Void> deleteOrderColumn;
    @FXML
    private Label messageLabel;
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
        menuItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set up the order date column with formatting
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        priceColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice())
        );
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
                            deleteOrder(order);
//                            showSuccessDialog("Order deleted successfully");// Call your delete method
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
    }

    public void setCreateOrderController(CreateOrderController createOrderController) {
        this.createOrderController = createOrderController;
        loadOrders(); // Load orders once createOrderController is available
    }

    private void loadOrders() {
        if (createOrderController != null) {
            Integer userId = createOrderController.getUserId();
            if (userId != null) {
                try {
                    List<OrderDTO> orderDTOs = orderService.getOrdersByUserId(userId);
                    ObservableList<OrderDTO> orders = FXCollections.observableArrayList(orderDTOs);
                    orderTable.setItems(orders);

                    // Populate orderIdComboBox directly from the fetched orders
                    List<Integer> orderIds = orderDTOs.stream()
                            .map(OrderDTO::getOrderId) // Get order IDs
                            .collect(Collectors.toList()).reversed();
                    orderIdComboBox.setItems(FXCollections.observableArrayList(orderIds)); // Populate the combo box

                    // Populate menuItemComboBox with available menu item IDs
                    List<MenuItemDto> menuItems = Optional.ofNullable(menuItemListService.getAllMenuItems())
                            .orElse(Collections.emptyList());
                    List<Integer> menuItemIds = menuItems.stream().map(MenuItemDto::getId).toList();
                    menuItemComboBox.setItems(FXCollections.observableArrayList(menuItemIds)); // Populate menu item combo box
                } catch (Exception e) {
                    showErrorDialog("Error loading orders: " + e.getMessage());
                }
            } else {
                showErrorDialog("User ID is not set.");
            }
        }
    }

    private void deleteOrder(OrderDTO order) {
        showConfirmationDialog("Are you sure you want to delete order: " + order.getOrderId() + "?", () -> {
            orderService.deleteOrderById(Long.valueOf(order.getOrderId()));
            showSuccessDialog("Order: " + order.getOrderId() + " deleted successfully");
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
        Integer selectedOrderId = Math.toIntExact(getSelectedOrderId()); // Get selected order ID
        Integer menuItemId = Integer.valueOf(menuItemComboBox.getValue()); // Get selected menu item ID
        String orderDate = String.valueOf(orderDatePicker.getValue().atStartOfDay()); // Get selected order date
        String status = statusComboBox.getValue(); // Get selected status ORDERED, PREPARED, SERVED

        // Perform the update operation using selectedOrderId, menuItemId, orderDate, and status
        orderService.updateOrderById(selectedOrderId, menuItemId,status, LocalDateTime.parse(orderDate)); // Pass status as a string or convert to enum if needed
        showSuccessDialog("Order updated successfully");
        loadOrders(); // Refresh the order list after update
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
            showErrorDialog("Error loading dashboard.");
        }
    }

    @FXML
    private void navigateToCreateOrder() {
        try {
            Stage stage = (Stage) createOrderButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/createOrder.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error loading dashboard.");
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
        dialogStage.initOwner(updateOrderButton.getScene().getWindow());
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
        dialogStage.initOwner(updateOrderButton.getScene().getWindow());
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
