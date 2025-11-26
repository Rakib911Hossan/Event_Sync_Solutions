package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.dto.mapper.OrderMapper;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.entity.User;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
import com.Corporate.Event_Sync.service.orderService.OrderListService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
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
    @Autowired
    private HomeController homeController;

    public void setLoggedInUser(UserDTO userDTO) {
        this.loggedInUser = homeController.getLoggedInUser();
        // Apply role-based UI restrictions after user is set
        // Use Platform.runLater to ensure UI components are fully initialized
        Platform.runLater(this::applyRoleBasedRestrictions);
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
    private TableColumn<OrderDTO, Number> priceColumn;
    @FXML
    private TableColumn<OrderDTO, LocalDateTime> orderDateColumn;
    @FXML
    private TableColumn<OrderDTO, Void> locationColumn;
    @FXML
    private TableColumn<OrderDTO, Void> deleteOrderColumn;
    @FXML
    private Button report;

    @FXML
    private ComboBox<Integer> orderIdComboBox;
    @FXML
    private ComboBox<Integer> menuItemComboBox;
    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button updateButton;

    @FXML
    private VBox updateSection; // Container for the entire update section

    @FXML
    private Label orderIdLabel;
    @FXML
    private Label menuItemLabel;
    @FXML
    private Label statusLabel;

    @Autowired
    private UserService userService;
    private CreateOrderController createOrderController;

    /**
     * Check if logged-in user is a DELIVERY_MAN
     */
    private boolean isDeliveryMan() {
        if (homeController != null && homeController.getLoggedInUser() != null) {
            return "DELIVERY_MAN".equalsIgnoreCase(homeController.getLoggedInUser().getRole());
        }
        return false;
    }

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
            private final java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(column -> new TableCell<OrderDTO, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    double priceValue = item.doubleValue();
                    setText(String.format("৳%.2f", priceValue));
                }
            }
        });

        // Set up the location column with buttons
        locationColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<OrderDTO, Void> call(TableColumn<OrderDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("View");

                    {
                        btn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                        btn.setOnAction(event -> {
                            OrderDTO order = getTableView().getItems().get(getIndex());
                            showLocationMap(order);
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

        // Set up the delete column with buttons (will be hidden for DELIVERY_MAN)
        deleteOrderColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<OrderDTO, Void> call(TableColumn<OrderDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Delete");

                    {
                        btn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
                        btn.setOnAction(event -> {
                            // Check role before allowing delete
                            if (isDeliveryMan()) {
                                showErrorDialog("You don't have permission to delete orders.");
                                return;
                            }
                            OrderDTO order = getTableView().getItems().get(getIndex());
                            deleteOrder(order);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || isDeliveryMan()) {
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

        // Check if user is already set (edge case)
        if (homeController != null && homeController.getLoggedInUser() != null) {
            Platform.runLater(this::applyRoleBasedRestrictions);
        }
    }

    /**
     * Apply UI restrictions based on user role
     */
    private void applyRoleBasedRestrictions() {
        System.out.println("Applying role-based restrictions. Is Delivery Man: " + isDeliveryMan());

        if (isDeliveryMan()) {
            // Option 1: Hide the entire update section container (if you have a VBox/HBox wrapping all update controls)
            if (updateSection != null) {
                updateSection.setVisible(false);
                updateSection.setManaged(false);
                System.out.println("Update section hidden");
            } else {
                // Option 2: Hide individual components
                hideUpdateComponents();
            }

            // Hide delete column
            if (deleteOrderColumn != null) {
                deleteOrderColumn.setVisible(false);
                System.out.println("Delete column hidden");
            }

            // Refresh the table to update the delete column visibility
            if (orderTable != null) {
                orderTable.refresh();
            }
        }
    }

    /**
     * Hide individual update components
     */
    private void hideUpdateComponents() {
        // Hide ComboBoxes
        if (orderIdComboBox != null) {
            orderIdComboBox.setVisible(false);
            orderIdComboBox.setManaged(false);
        }
        if (menuItemComboBox != null) {
            menuItemComboBox.setVisible(false);
            menuItemComboBox.setManaged(false);
        }
        if (statusComboBox != null) {
            statusComboBox.setVisible(false);
            statusComboBox.setManaged(false);
        }

        // Hide Update Button
        if (updateButton != null) {
            updateButton.setVisible(false);
            updateButton.setManaged(false);
        }

        // Hide Report Button
        if (report != null) {
            report.setVisible(false);
            report.setManaged(false);
        }

        // Hide Labels
        if (orderIdLabel != null) {
            orderIdLabel.setVisible(false);
            orderIdLabel.setManaged(false);
        }
        if (menuItemLabel != null) {
            menuItemLabel.setVisible(false);
            menuItemLabel.setManaged(false);
        }
        if (statusLabel != null) {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        }

        System.out.println("Individual update components and report button hidden");
    }

    private void showLocationMap(OrderDTO order) {
        Stage mapStage = new Stage();
        mapStage.initModality(Modality.APPLICATION_MODAL);
        mapStage.setTitle("Order Location - Order ID: " + order.getOrderId());

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15px; -fx-background-color: #2b2d42;");

        double latitude = order.getLatitude();
        double longitude = order.getLongitude();

        // Auto-detect and swap if coordinates are reversed
        if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
            double temp = latitude;
            latitude = longitude;
            longitude = temp;
            System.out.println("Coordinates were swapped! New - Lat: " + latitude + ", Lng: " + longitude);
        }

        // Validate coordinates
        if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) {
            showErrorDialog("Invalid coordinates! Latitude must be -90 to 90, Longitude must be -180 to 180.");
            return;
        }

        final double finalLat = latitude;
        final double finalLng = longitude;

        // Header with order info
        Label titleLabel = new Label("Order #" + order.getOrderId());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #4caf50;");

        HBox infoBox = new HBox(20);
        infoBox.setAlignment(javafx.geometry.Pos.CENTER);

        Label latLabel = new Label("Lat: " + String.format("%.5f", finalLat));
        latLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        Label lngLabel = new Label("Lng: " + String.format("%.5f", finalLng));
        lngLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        Label statusLabelMap = new Label("Status: " + order.getStatus());
        statusLabelMap.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

        infoBox.getChildren().addAll(latLabel, lngLabel, statusLabelMap);

        // WebView for the map
        javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
        javafx.scene.web.WebEngine webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            System.out.println("WebEngine state: " + newState);
            if (newState == javafx.concurrent.Worker.State.FAILED) {
                System.out.println("Failed to load map!");
                Throwable exception = webEngine.getLoadWorker().getException();
                if (exception != null) {
                    exception.printStackTrace();
                }
            }
        });

        String mapHtml = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='utf-8'>" +
                "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>" +
                "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
                "<style>" +
                "body{margin:0;padding:0;}" +
                "#map{height:100vh;width:100%;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div id='map'></div>" +
                "<script>" +
                "console.log('Starting map initialization...');" +
                "console.log('Coordinates: " + finalLat + ", " + finalLng + "');" +
                "setTimeout(function(){" +
                "try{" +
                "var map=L.map('map').setView([" + finalLat + "," + finalLng + "],13);" +
                "console.log('Map created');" +
                "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{" +
                "attribution:'OpenStreetMap'," +
                "maxZoom:19" +
                "}).addTo(map);" +
                "console.log('Tiles added');" +
                "var marker=L.marker([" + finalLat + "," + finalLng + "]).addTo(map);" +
                "marker.bindPopup('<b>Order #" + order.getOrderId() + "</b><br>Status: " + order.getStatus() + "').openPopup();" +
                "console.log('Marker added');" +
                "}catch(e){" +
                "console.error('Error:',e);" +
                "document.body.innerHTML='<div style=\"padding:20px;color:red;\">Map Error: '+e.message+'</div>';" +
                "}" +
                "},100);" +
                "</script>" +
                "</body>" +
                "</html>";

        webEngine.loadContent(mapHtml);
        webView.setPrefSize(700, 500);

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        Button openOSMButton = new Button("Open in OpenStreetMap");
        openOSMButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-size: 14px;");
        openOSMButton.setOnAction(e -> {
            String url = String.format("https://www.openstreetmap.org/?mlat=%.5f&mlon=%.5f#map=15/%.5f/%.5f",
                    finalLat, finalLng, finalLat, finalLng);
            openUrlInBrowser(url);
        });

        Button openGoogleButton = new Button("Open in Google Maps");
        openGoogleButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-size: 14px;");
        openGoogleButton.setOnAction(e -> {
            String url = String.format("https://www.google.com/maps?q=%.5f,%.5f", finalLat, finalLng);
            openUrlInBrowser(url);
        });

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-size: 14px;");
        closeButton.setOnAction(e -> mapStage.close());

        buttonBox.getChildren().addAll(openOSMButton, openGoogleButton, closeButton);

        content.getChildren().addAll(titleLabel, infoBox, webView, buttonBox);

        Scene scene = new Scene(content, 750, 650);
        mapStage.setScene(scene);
        mapStage.show();
    }

    private void openUrlInBrowser(String url) {
        try {
            EventSyncApplication app = (EventSyncApplication) EventSyncApplication.context.getBean(EventSyncApplication.class);
            app.getHostServices().showDocument(url);
        } catch (Exception ex) {
            showErrorDialog("Could not open the map in your browser.");
            ex.printStackTrace();
        }
    }

    private void loadOrders() {
        try {
            List<Order> allOrders = orderListService.getAllOrders();

            // Filter orders: user role is USER (student)
            List<Order> studentOrders = allOrders.stream()
                    .filter(order -> {
                        User user = order.getUser();
                        return user != null && ("USER".equals(user.getRole()) || "STUDENT".equals(user.getRole()));
                    })
                    .collect(Collectors.toList());

            // Convert to DTO after filtering
            List<OrderDTO> studentOrderDTOs = orderMapper.toDTOList(studentOrders);

            List<Integer> orderIds = studentOrderDTOs.stream()
                    .map(OrderDTO::getOrderId)
                    .collect(Collectors.toList());

            if (orderIdComboBox != null) {
                orderIdComboBox.setItems(FXCollections.observableArrayList(orderIds));
            }

            ObservableList<OrderDTO> orders = FXCollections.observableArrayList(studentOrderDTOs);
            orderTable.setItems(orders);
        } catch (Exception e) {
            showErrorDialog("Error loading student orders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadMenuItems() {
        try {
            List<Integer> menuItemIds = menuItemListService.getAllMenuItems().stream()
                    .map(MenuItemDto::getId)
                    .collect(Collectors.toList());
            if (menuItemComboBox != null) {
                menuItemComboBox.setItems(FXCollections.observableArrayList(menuItemIds));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading menu items: " + e.getMessage());
        }
    }

    private void deleteOrder(OrderDTO order) {
        // Double-check permission
        if (isDeliveryMan()) {
            showErrorDialog("You don't have permission to delete orders.");
            return;
        }

        showConfirmationDialog("Are you sure you want to delete order: " + order.getOrderId() + "?", () -> {
            orderService.deleteOrderById(Long.valueOf(order.getOrderId()));
            showSuccessDialog("Deleted order: " + order.getOrderId());
            loadOrders();
        });
    }

    public Integer getSelectedOrderId() {
        return orderIdComboBox != null ? orderIdComboBox.getValue() : null;
    }

    @FXML
    private void updateOrder() {
        // Check permission first
        if (isDeliveryMan()) {
            showErrorDialog("You don't have permission to update orders.");
            return;
        }

        try {
            Integer selectedOrderId = getSelectedOrderId();
            if (selectedOrderId == null) {
                showErrorDialog("Please select an order to update.");
                return;
            }

            Integer menuItemId = menuItemComboBox.getValue();
            if (menuItemId == null) {
                showErrorDialog("Please select a menu item.");
                return;
            }

            String status = statusComboBox.getValue();
            if (status == null || status.isEmpty()) {
                showErrorDialog("Please select an order status.");
                return;
            }

            Order currentOrder = orderService.getOrderById(Long.valueOf(selectedOrderId));
            if (currentOrder == null) {
                showErrorDialog("Order not found.");
                return;
            }

            orderService.updateOrderById(selectedOrderId, menuItemId, status, currentOrder.getOrderDate());
            showSuccessDialog("Order updated successfully.");
            loadOrders();

        } catch (Exception e) {
            showErrorDialog("Please fill the required fields: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToHome() {
        try {
            Stage currentStage = (Stage) homeDashboard.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/home.fxml"));
            loader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene homeScene = new Scene(loader.load());
            HomeController homeController = loader.getController();
            if (homeController != null && homeController.getLoggedInUser() != null) {
                homeController.setLoggedInUser(homeController.getLoggedInUser());
            }
            currentStage.setScene(homeScene);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error navigating to home.");
        }
    }

    @FXML
    public void navigateToReport() {
        try {
            Stage stage = (Stage) report.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/reports.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene updateUserScene = new Scene(fxmlLoader.load());
            stage.setScene(updateUserScene);
        } catch (IOException e) {
            e.printStackTrace();
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
            dialogStage.close();
            onConfirm.run();
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