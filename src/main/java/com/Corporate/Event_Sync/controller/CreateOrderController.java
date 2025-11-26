package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.entity.Order;
import com.Corporate.Event_Sync.service.locationService.SetLocation;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import com.Corporate.Event_Sync.service.userService.UserService;
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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import netscape.javascript.JSObject;
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
    private MenuItemService menuItemService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private OrderService orderService;

    private Stage primaryStage;
    @Autowired
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
    private Button setLocations;

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
    private TextField offerTokenField;

    @FXML
    private Button applyTokenButton;

    @FXML
    private Label tokenStatusLabel;

    @FXML
    private Label discountLabel;

    @FXML
    private ImageView selectedItemImageView;

    @FXML
    private Button enlargeImageButton;

    @FXML
    private Label imagePreviewLabel;

    private Image currentPreviewImage = null;

    private String appliedToken = null;
    private int discountPercentage = 0;

    private SetLocation setLocation;
    private double latitude;
    private double longitude;
    @FXML
    public void initialize() {
        // Initialize other columns
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAvailableTime()));
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPrice()));

//        // For picColumn, display the image from URL
//        previewImageColumn.setCellFactory(new Callback<TableColumn<MenuItemDto, Void>, TableCell<MenuItemDto, Void>>() {
//            @Override
//            public TableCell<MenuItemDto, Void> call(TableColumn<MenuItemDto, Void> param) {
//                return new TableCell<MenuItemDto, Void>() {
//                    private final Button previewButton = new Button("Show");
//
//                    {
//                        previewButton.setOnAction(event -> {
//                            MenuItemDto menuItemDto = getTableView().getItems().get(getIndex());
//                            String imagePath = menuItemDto.getItemPic();
//                            if (imagePath != null && !imagePath.isEmpty()) {
//                                displayImageInPreview(imagePath, menuItemDto.getItemName());
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void updateItem(Void item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty) {
//                            setGraphic(null);
//                        } else {
//                            setGraphic(previewButton);
//                        }
//                    }
//                };
//            }
//        });


// ADD this at the end of initialize() method (after loadMenuItems()):
        menuItemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String imagePath = newSelection.getItemPic();
                if (imagePath != null && !imagePath.isEmpty()) {
                    displayImageInPreview(imagePath, newSelection.getItemName());
                }
            }
        });

        loadMenuItems();
        // Assuming getUserRole() returns a String like "STUDENT", "USER", "ADMIN", etc.


    }

    private void displayImageInPreview(String imagePath, String itemName) {
        try {
            currentPreviewImage = new Image("file:" + imagePath);
            selectedItemImageView.setImage(currentPreviewImage);
            imagePreviewLabel.setText("Preview: " + itemName);
            enlargeImageButton.setVisible(true);
        } catch (Exception e) {
            selectedItemImageView.setImage(null);
            imagePreviewLabel.setText("No image available");
            enlargeImageButton.setVisible(false);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEnlargeImage() {
        if (currentPreviewImage != null) {
            showLargerImage(currentPreviewImage);
        }
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
        String role = homeController.getUserRole();
        boolean visible = role.equalsIgnoreCase("STUDENT") || role.equalsIgnoreCase("USER") || role.equalsIgnoreCase("ADMIN");
        setLocations.setVisible(visible);
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
        private void setLoction() {
            Stage mapStage = new Stage();
            mapStage.setTitle("Select Location");

            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();

            String localUrl = getClass().getResource("/com.Corporate.Event_Sync/map.html").toExternalForm();
            engine.load(localUrl);

            // Initialize SetLocation field here
            setLocation = new SetLocation(mapStage, () -> {
                // This runnable runs after lat/lon is set from JS
                latitude = setLocation.getLatitude();
                longitude = setLocation.getLongitude();
                System.out.println("Coordinates updated: " + latitude + ", " + longitude);
            });

            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("javaApp", setLocation);  // expose the field
                }
            });

            Scene scene = new Scene(webView, 800, 600);
            mapStage.setScene(scene);
            mapStage.show();
        }



    @FXML
    private void handleCreateOrder() {
        try {
            Integer userId = homeController.getUserId();
            Integer menuItemId = menuItemComboBox.getValue();

            if (menuItemId == null) {
                showErrorDialog("Please select a menu item ID.");
                return;
            }

            LocalDateTime orderDateTime = getOrderDateTime();
            if (orderDateTime == null) {
                showErrorDialog("Please select a valid date.");
                return;
            }

            boolean isStudentOrUser = userService.isStudentOrUserById(userId);
            if(isStudentOrUser && latitude == 0.00) {
                showErrorDialog("Kindly set your location please");
                return;
            }
            MenuItem item = menuItemService.getMenuItemById(menuItemId);
            int price = item.getPrice();
            // Create order with discount
            Order order = orderService.saveOrder(userId, menuItemId, "ORDERED", orderDateTime, latitude, longitude, price);

            // Apply discount if token is valid
            if (appliedToken != null && discountPercentage > 0) {
                double discountedPrice = order.getPrice() * (1 - discountPercentage / 100.0);
                order.setPrice((int) discountedPrice);
                orderService.updateOrder(order);

                // Clear the token from user after use
                var userDTO = userService.getUserById(userId);
                userDTO.setDiscountToken(null);
                userDTO.setDiscountAmount(null);
                userService.updateUser(userDTO);
            }

            showSuccessDialog("Order created successfully!" +
                    (appliedToken != null ? " Discount applied!" : ""));

        } catch (Exception e) {
            showErrorDialog("Error creating order");
        }
    }

    @FXML
    private void handleApplyToken() {
        String token = offerTokenField.getText().trim();
        Integer userId = homeController.getUserId();

        if (token.isEmpty()) {
            tokenStatusLabel.setText("Please enter a token");
            tokenStatusLabel.setStyle("-fx-text-fill: #E74C3C;");
            tokenStatusLabel.setVisible(true);
            tokenStatusLabel.setManaged(true);
            return;
        }

        try {
            // Get user and validate token
            var userDTO = userService.getUserById(userId);

            if (userDTO.getDiscountToken() != null && userDTO.getDiscountToken().equals(token)) {
                appliedToken = token;
                discountPercentage = userDTO.getDiscountAmount();

                tokenStatusLabel.setText("✓ Token applied successfully!");
                tokenStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
                tokenStatusLabel.setVisible(true);
                tokenStatusLabel.setManaged(true);

                discountLabel.setText(String.format("💰 %d%% OFF Applied", discountPercentage));
                discountLabel.setVisible(true);
                discountLabel.setManaged(true);

                offerTokenField.setDisable(true);
                applyTokenButton.setDisable(true);
            } else {
                tokenStatusLabel.setText("✗ Invalid or expired token");
                tokenStatusLabel.setStyle("-fx-text-fill: #E74C3C;");
                tokenStatusLabel.setVisible(true);
                tokenStatusLabel.setManaged(true);
                discountLabel.setVisible(false);
                discountLabel.setManaged(false);
            }
        } catch (Exception e) {
            showErrorDialog("Error validating token");
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
            e.  printStackTrace();
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

