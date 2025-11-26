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
    @Autowired
    private DefaultWeekDaysMapper defaultWeekDaysMapper;

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
    private Label weekDaysLabel;
    @FXML
    private Button exitButton;
    @FXML
    private Button logOut;
    @FXML
    private Button userHome;
    @FXML
    private Button confirmButton;
    // Meal category checkboxes
    @FXML
    private CheckBox breakfastCheckBox;
    @FXML
    private CheckBox lunchCheckBox;
    @FXML
    private CheckBox snacksCheckBox;
    @FXML
    private CheckBox dinnerCheckBox;
    @FXML
    private Label selectedCategoriesLabel;

    private ObservableList<DefaultWeekDaysDto> weekDaysList;
    private List<String> selectedWeekDays = new ArrayList<>();
    private double latitude;
    private double longitude;

    @FXML
    private void initialize() {
        // Set user details
        List<String> daysList = extractDays(
                defaultWeekDaysService.getDaysByUserId(homeController.getLoggedInUser().getId())
        );
        String formattedDays = formatDaysInPairs(daysList);

        nameLabel.setText(homeController.getLoggedInUser().getName());
        phoneLabel.setText(homeController.getLoggedInUser().getPhone());
        weekDaysLabel.setText(formattedDays);

        // Setup meal category listeners
        setupMealCategoryListeners();

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

        // Initialize the days
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

    private void setupMealCategoryListeners() {
        breakfastCheckBox.setOnAction(e -> updateSelectedCategories());
        lunchCheckBox.setOnAction(e -> updateSelectedCategories());
        snacksCheckBox.setOnAction(e -> updateSelectedCategories());
        dinnerCheckBox.setOnAction(e -> updateSelectedCategories());

        // Initial update
        updateSelectedCategories();
    }

    private void updateSelectedCategories() {
        List<String> selected = new ArrayList<>();
        if (breakfastCheckBox.isSelected()) selected.add("Breakfast");
        if (lunchCheckBox.isSelected()) selected.add("Lunch");
        if (snacksCheckBox.isSelected()) selected.add("Snacks");
        if (dinnerCheckBox.isSelected()) selected.add("Dinner");

        String displayText = selected.isEmpty() ? "None selected" : "Selected: " + String.join(", ", selected);
        selectedCategoriesLabel.setText(displayText);
    }

    private List<String> getSelectedCategories() {
        List<String> categories = new ArrayList<>();
        if (breakfastCheckBox.isSelected()) categories.add(Category.BREAKFAST.name());
        if (lunchCheckBox.isSelected()) categories.add(Category.LUNCH.name());
        if (snacksCheckBox.isSelected()) categories.add(Category.SNACKS.name());
        if (dinnerCheckBox.isSelected()) categories.add(Category.DINNER.name());
        return categories;
    }

    private List<String> extractDays(List<DefaultWeekDaysDto> weekDaysDtos) {
        return weekDaysDtos.stream()
                .map(DefaultWeekDaysDto::getDays)
                .toList();
    }

    private String formatDaysInPairs(List<String> daysList) {
        StringBuilder formattedDays = new StringBuilder();
        List<String> allDays = new ArrayList<>();

        for (String days : daysList) {
            allDays.addAll(List.of(days.split(",")));
        }

        int daysCount = allDays.size();
        for (int i = 0; i < allDays.size(); i++) {
            formattedDays.append(allDays.get(i));
            if (i % 2 == 0 && i + 1 < allDays.size()) {
                formattedDays.append(" , ");
            }
            if (i % 2 != 0 && i + 1 < allDays.size()) {
                formattedDays.append("\n\n");
            }
        }
        return daysCount + " days\n\n" + formattedDays.toString().trim();
    }

    @FXML
    private void handleConfirmAction() {
        List<String> selectedCategories = getSelectedCategories();

        if (selectedCategories.isEmpty()) {
            showErrorDialog("Please select at least one meal category.");
            return;
        }

        if (selectedWeekDays.isEmpty()) {
            showErrorDialog("No weekdays selected.");
            return;
        }

        int userId = homeController.getUserId();
        defaultWeekDaysService.createWeekDays(userId, String.join(",", selectedWeekDays), true,
                latitude, longitude, selectedCategories);

//        String currentDay = java.time.LocalDate.now().getDayOfWeek().toString();
//
//        for (String day : selectedWeekDays) {
//            if (day.equalsIgnoreCase(currentDay)) {
//                createOrdersForCategories(userId, selectedCategories);
//                System.out.println("Orders created for " + day + " with categories: " + selectedCategories);
//                break;
//            }
//        }

        showSuccessDialog("Weekdays and orders confirmed for selected meal categories.");
    }

    private Integer getRandomMenuItemIdByCategory(String category, Random random) {
        int min, max;

        switch (category.toUpperCase()) {
            case "BREAKFAST":
                min = 1;
                max = 5;
                break;
            case "LUNCH":
                min = 6;
                max = 10;
                break;
            case "SNACKS":
                min = 11;
                max = 15;
                break;
            case "DINNER":
                min = 16;
                max = 20;
                break;
            default:
                return null;
        }

        // Generate random ID within range (inclusive)
        return min + random.nextInt(max - min + 1);
    }
    private void createOrdersForCategories(int userId, List<String> categories) {
        Random random = new Random();

        for (String category : categories) {
            // Get random menu item ID based on category range
            Integer menuItemId = getRandomMenuItemIdByCategory(category, random);

            if (menuItemId == null) {
                System.out.println("Warning: Invalid category: " + category);
                continue;
            }

            // Fetch the menu item by ID
            MenuItem menuItem = menuItemRepository.findById(menuItemId).orElse(null);

            if (menuItem == null) {
                System.out.println("Warning: Menu item not found for ID: " + menuItemId);
                continue;
            }

            MenuItemDto selectedItem = defaultWeekDaysMapper.mapToDto(menuItem);

            orderService.createOrder(
                    userId,
                    selectedItem.getId(),
                    "ORDERED",
                    LocalDateTime.now(),
                    latitude,
                    longitude,
                    selectedItem.getPrice()
            );

            System.out.println("Order created for category: " + category + " - Item ID: " + selectedItem.getId());
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
            Scene homeScene = new Scene(loader.load());
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