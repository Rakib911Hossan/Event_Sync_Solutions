package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.dto.mapper.OrderMapper;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Component
public class OrderListController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;

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
    private TableColumn<OrderDTO, Void> deleteOrderColumn;

    @FXML
    private ComboBox<Long> orderIdComboBox; // Combo box for order IDs
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
                    orderIdComboBox.setItems(FXCollections.observableArrayList()); // Populate the combo box

                    // Populate menuItemComboBox with available menu item IDs
                    List<Integer> menuItemIds = orderDTOs.stream()
                            .map(OrderDTO::getMenuItemId) // Assuming you want to use menuItemId from OrderDTO
                            .distinct()
                            .collect(Collectors.toList());
                    menuItemComboBox.setItems(FXCollections.observableArrayList(menuItemIds)); // Populate menu item combo box
                } catch (Exception e) {
                    System.err.println("Error loading orders: " + e.getMessage());
                }
            } else {
                System.err.println("User ID is not set.");
            }
        }
    }


    private void deleteOrder(OrderDTO order) {
        orderService.deleteOrderById(Long.valueOf(order.getOrderId()));
        loadOrders(); // Refresh the list after deletion
    }

    // Method to get selected order ID from orderIdComboBox
    public Long getSelectedOrderId() {
        return orderIdComboBox.getValue(); // Retrieve the selected order ID
    }

    // Update method that uses the selected order ID
    private void updateOrder() {
        Integer selectedOrderId = Math.toIntExact(getSelectedOrderId()); // Get selected order ID
        Integer menuItemId = Integer.valueOf(menuItemComboBox.getValue()); // Get selected menu item ID
        String orderDate = String.valueOf(orderDatePicker.getValue().atStartOfDay()); // Get selected order date
        String status = statusComboBox.getValue(); // Get selected status ORDERED, PREPARED, SERVED

        // Perform the update operation using selectedOrderId, menuItemId, orderDate, and status
        orderService.updateOrderById(selectedOrderId, menuItemId,status, LocalDateTime.parse(orderDate)); // Pass status as a string or convert to enum if needed
        loadOrders(); // Refresh the order list after update
    }

}
