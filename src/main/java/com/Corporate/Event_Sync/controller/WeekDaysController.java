package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.DefaultWeekDaysDto;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.mapper.DefaultWeekDaysMapper;
import com.Corporate.Event_Sync.entity.MenuItem;
import com.Corporate.Event_Sync.repository.MenuItemRepository;
import com.Corporate.Event_Sync.service.defaultWeekDaysService.DefaultWeekDaysService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import com.Corporate.Event_Sync.utils.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@Component
public class WeekDaysController {

    @Autowired
    private DefaultWeekDaysService defaultWeekDaysService;
    @Autowired
    private HomeController homeController;
    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @FXML
    private TableView<DefaultWeekDaysDto> weekDaysTable;

    @FXML
    private TableColumn<DefaultWeekDaysDto, String> daysColumn;

    @FXML
    private TableColumn<DefaultWeekDaysDto, String> weekDaysColumn;

    @FXML
    private Label updateOrderLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label officeIdLabel;

    @FXML
    private Label weekDaysLabel;

    @FXML
    private Button exitButton;

    @FXML
    private Button logOut;

    @FXML
    private Button userHome;

    @FXML
    private Button confirmButton;  // Add reference for the confirm button

    private ObservableList<DefaultWeekDaysDto> weekDaysList;

    private List<String> selectedWeekDays = new ArrayList<>();

    @Autowired
    private DefaultWeekDaysMapper defaultWeekDaysMapper;


    @FXML
    private void initialize() {
        // Set user details
        List<String> daysList = extractDays(
                defaultWeekDaysService.getDaysByUserId(homeController.getLoggedInUser().getId())
        );
        String formattedDays = formatDaysInPairs(daysList);

        nameLabel.setText(homeController.getLoggedInUser().getName());
        phoneLabel.setText(homeController.getLoggedInUser().getPhone());
        departmentLabel.setText(homeController.getLoggedInUser().getDepartment());
        officeIdLabel.setText(String.valueOf(homeController.getLoggedInUser().getOfficeId()));
        weekDaysLabel.setText(formattedDays);

        // Table column setup
        daysColumn.setCellValueFactory(new PropertyValueFactory<>("days"));
        weekDaysColumn.setCellValueFactory(new PropertyValueFactory<>("isWeekDays"));

        // Days column styling
        daysColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        // WeekDays column with Yes/No buttons
        weekDaysColumn.setCellFactory(column -> new TableCell<>() {
            private final Button yesButton = new Button("Yes");
            private final Button noButton = new Button("No");

            {
                yesButton.setOnAction(event -> {
                    DefaultWeekDaysDto weekDay = getTableView().getItems().get(getIndex());
                    weekDay.setWeekDays(true);

                    if (!selectedWeekDays.contains(weekDay.getDays())) {
                        selectedWeekDays.add(weekDay.getDays());
                    }

                    updateButtonStyles(weekDay);
                    refreshLabel();
                    weekDaysTable.refresh();
                });

                noButton.setOnAction(event -> {
                    DefaultWeekDaysDto weekDay = getTableView().getItems().get(getIndex());
                    weekDay.setWeekDays(false);

                    selectedWeekDays.remove(weekDay.getDays());

                    updateButtonStyles(weekDay);
                    refreshLabel();
                    weekDaysTable.refresh();
                });
            }

            private void updateButtonStyles(DefaultWeekDaysDto weekDay) {
                yesButton.setStyle(weekDay.isWeekDays()
                        ? "-fx-background-color: green; -fx-text-fill: white;"
                        : "-fx-background-color: lightgray; -fx-text-fill: black;");
                noButton.setStyle(!weekDay.isWeekDays()
                        ? "-fx-background-color: red; -fx-text-fill: white;"
                        : "-fx-background-color: lightgray; -fx-text-fill: black;");
            }

            private void refreshLabel() {
                String selectedDaysText = formatDaysInPairs(selectedWeekDays);
                weekDaysLabel.setText(selectedDaysText);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    DefaultWeekDaysDto weekDay = getTableView().getItems().get(getIndex());
                    updateButtonStyles(weekDay);

                    HBox buttonBox = new HBox(5, yesButton, noButton);
                    buttonBox.setAlignment(Pos.CENTER);
                    setGraphic(buttonBox);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        // Initialize the days (if not already populated)
        weekDaysList = FXCollections.observableArrayList(
                new DefaultWeekDaysDto(1, 1, "Monday", false),
                new DefaultWeekDaysDto(2, 1, "Tuesday", false),
                new DefaultWeekDaysDto(3, 1, "Wednesday", false),
                new DefaultWeekDaysDto(4, 1, "Thursday", false),
                new DefaultWeekDaysDto(5, 1, "Friday", false),
                new DefaultWeekDaysDto(6, 1, "Saturday", false),
                new DefaultWeekDaysDto(7, 1, "Sunday", false)
        );

        weekDaysTable.setItems(weekDaysList);
    }

    private List<String> extractDays(List<DefaultWeekDaysDto> weekDaysDtos) {
        return weekDaysDtos.stream()
                .map(DefaultWeekDaysDto::getDays)
                .toList();
    }
    // Method to group the days into pairs with newline between them
    private String formatDaysInPairs(List<String> daysList) {
        StringBuilder formattedDays = new StringBuilder();
        List<String> allDays = new ArrayList<>();

        // Split each day's string and add it to a flat list
        for (String days : daysList) {
            allDays.addAll(List.of(days.split(","))); // Split the days by comma
        }

        int daysCount = allDays.size();
        for (int i = 0; i < allDays.size(); i++) {
            formattedDays.append(allDays.get(i));
            // If it's not the last day in the pair, add a comma
            if (i % 2 == 0 && i + 1 < allDays.size()) {
                formattedDays.append(" , ");
            }
            // If it's the second day in the pair, add a newline character
            if (i % 2 != 0 && i + 1 < allDays.size()) {
                formattedDays.append("\n\n"); // New line after each pair
            }
        }
        return daysCount + " days\n\n" + formattedDays.toString().trim();
    }

    @FXML
    private void handleConfirmAction() {
        // Finalize the selection and update in the database
        if (!selectedWeekDays.isEmpty()) {
            int userId = homeController.getUserId(); // Example userId, replace as needed
            defaultWeekDaysService.createWeekDays(userId, String.join(",", selectedWeekDays), true);

            // Get the current day's name (e.g., "Monday", "Tuesday")
            String currentDay = java.time.LocalDate.now().getDayOfWeek().toString().toLowerCase();

            for (String day : selectedWeekDays) {
                if (day.equalsIgnoreCase(currentDay)) {
                    createOrder(userId);
                    System.out.println("Order created for " + day + " as it matches the current day.");
                } else {
                    System.out.println("No order created for " + day + " as it does not match the current day.");
                }
            }

            showSuccessDialog("Weekdays and orders confirmed.");
        } else {
            showErrorDialog("No weekdays selected.");
        }
    }


    private void createOrder(int userId) {
        List<MenuItem> lunchMenuItems = menuItemRepository.findByCategory(Category.LUNCH.name());
        List<MenuItem> snackMenuItems = menuItemRepository.findByCategory(Category.SNACKS.name());

        Random random = new Random();
        if (!lunchMenuItems.isEmpty() && !snackMenuItems.isEmpty()) {
            MenuItemDto lunchMenuItem = defaultWeekDaysMapper.mapToDto(
                    lunchMenuItems.get(random.nextInt(lunchMenuItems.size()))
            );
            MenuItemDto snacksMenuItem = defaultWeekDaysMapper.mapToDto(
                    snackMenuItems.get(random.nextInt(snackMenuItems.size()))
            );

            // Create orders using IDs from MenuItemDto
            orderService.createOrder(userId, lunchMenuItem.getId(), "ORDERED", LocalDateTime.now());
            orderService.createOrder(userId, snacksMenuItem.getId(), "ORDERED", LocalDateTime.now());
        } else {
            throw new IllegalStateException("No lunch or snack items available for ordering.");
        }
    }


    @FXML
    private void navigateToMain() {
        try {
            Stage stage = (Stage) logOut.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            showSuccessDialog("User logged out successfully and redirected to the home page");
            stage.setScene(loginScene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
            try {
                Stage currentStage = (Stage) userHome.getScene().getWindow();
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

            }
        }


    @FXML
    private void handleExitAction() {
        System.out.println("Exiting...");
        System.exit(0);
    }

    private void showSuccessDialog(String message) {
        showDialog(message, "#4CAF50");
    }

    private void showErrorDialog(String message) {
        showDialog(message, "#E74C3C");
    }

    private void showDialog(String message, String color) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(confirmButton.getScene().getWindow());
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
