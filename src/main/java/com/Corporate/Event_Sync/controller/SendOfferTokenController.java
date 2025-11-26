package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.service.mailService.EmailService;
import com.Corporate.Event_Sync.service.userService.UserListService;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
public class SendOfferTokenController {

    @FXML
    private ComboBox<String> offerTypeComboBox;

    @FXML
    private VBox customOfferBox;

    @FXML
    private TextField offerTitleField;

    @FXML
    private TextField discountField;

    @FXML
    private TextArea offerDescriptionArea;

    @FXML
    private CheckBox selectAllCheckBox;

    @FXML
    private CheckBox studentFilterCheckBox;

    @FXML
    private ComboBox<String> userCategoryComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private VBox userListContainer;

    @FXML
    private Label selectedCountLabel;

    @FXML
    private Label filteredCountLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button sendButton;

    @Autowired
    private UserListService userListService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    private UserDTO loggedInUser;
    private List<UserDTO> allUsers;
    private Map<CheckBox, UserDTO> userCheckBoxMap;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 8;

    // Predefined offer types
    private static final Map<String, OfferTemplate> OFFER_TEMPLATES = new HashMap<>();

    static {
        OFFER_TEMPLATES.put("Welcome Bonus - 20% OFF", new OfferTemplate(
                "Welcome Bonus",
                20,
                "Welcome to Event Sync! Enjoy 20% off on your first order."
        ));
        OFFER_TEMPLATES.put("Weekend Special - 15% OFF", new OfferTemplate(
                "Weekend Special",
                15,
                "Special weekend offer! Get 15% discount on all orders this weekend."
        ));
        OFFER_TEMPLATES.put("Flash Sale - 30% OFF", new OfferTemplate(
                "Flash Sale",
                30,
                "Limited time offer! Grab 30% off on selected items. Hurry!"
        ));
        OFFER_TEMPLATES.put("Loyalty Reward - 25% OFF", new OfferTemplate(
                "Loyalty Reward",
                25,
                "Thank you for being a loyal customer! Enjoy 25% off as a reward."
        ));
        OFFER_TEMPLATES.put("Student Discount - 10% OFF", new OfferTemplate(
                "Student Discount",
                10,
                "Special student discount! Get 10% off on all orders."
        ));
        OFFER_TEMPLATES.put("Custom Offer", new OfferTemplate(
                "Custom",
                0,
                ""
        ));
    }

    public void setLoggedInUser(UserDTO userDTO) {
        this.loggedInUser = userDTO;
    }

    @FXML
    public void initialize() {
        userCheckBoxMap = new HashMap<>();

        // Populate offer types
        offerTypeComboBox.getItems().addAll(OFFER_TEMPLATES.keySet());

        // Populate user category filter
        userCategoryComboBox.getItems().addAll("All Categories", "PREMIUM", "REGULAR", "NORMAL");
        userCategoryComboBox.setValue("All Categories");

        // Add listener for offer type selection
        offerTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Custom Offer".equals(newVal)) {
                customOfferBox.setVisible(true);
                customOfferBox.setManaged(true);
            } else {
                customOfferBox.setVisible(false);
                customOfferBox.setManaged(false);
            }
        });

        // Add listeners for filters
        studentFilterCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        userCategoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        try {
            allUsers = userListService.getActiveUsers();
            applyFilters();
        } catch (Exception e) {
            showStatus("Error loading users: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        List<UserDTO> filteredUsers = allUsers.stream()
                .filter(this::matchesFilters)
                .collect(Collectors.toList());

        displayUsers(filteredUsers);
        updateFilteredCount(filteredUsers.size());
    }

    private boolean matchesFilters(UserDTO user) {
        // Student filter
        if (studentFilterCheckBox.isSelected()) {
            if (user.getRole() == null || !user.getRole().equalsIgnoreCase("student")) {
                return false;
            }
        }

        // User category filter
        String selectedCategory = userCategoryComboBox.getValue();
        if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
            if (user.getUserCategory() == null || !user.getUserCategory().equalsIgnoreCase(selectedCategory)) {
                return false;
            }
        }

        // Search filter
        String searchText = searchField.getText().toLowerCase().trim();
        if (!searchText.isEmpty()) {
            return user.getName().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText);
        }

        return true;
    }

    private void displayUsers(List<UserDTO> users) {
        userListContainer.getChildren().clear();
        userCheckBoxMap.clear();

        for (UserDTO user : users) {
            HBox userRow = new HBox(10);
            userRow.setStyle("-fx-padding: 8px; -fx-background-color: #34495e; -fx-background-radius: 5;");

            CheckBox checkBox = new CheckBox();
            checkBox.setStyle("-fx-text-fill: white;");

            // Build user info with role and category
            StringBuilder userInfo = new StringBuilder();
            userInfo.append(user.getName())
                    .append(" (").append(user.getEmail()).append(")");

            if (user.getDepartment() != null) {
                userInfo.append(" - ").append(user.getDepartment());
            }

            if (user.getRole() != null) {
                userInfo.append(" | Role: ").append(user.getRole());
            }

            if (user.getUserCategory() != null) {
                userInfo.append(" | Category: ").append(user.getUserCategory());
            }

            Label userLabel = new Label(userInfo.toString());
            userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            userLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(userLabel, javafx.scene.layout.Priority.ALWAYS);

            checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                updateSelectedCount();
            });

            userCheckBoxMap.put(checkBox, user);
            userRow.getChildren().addAll(checkBox, userLabel);
            userListContainer.getChildren().add(userRow);
        }

        updateSelectedCount();
    }

    @FXML
    private void handleSelectAll() {
        boolean selectAll = selectAllCheckBox.isSelected();
        for (CheckBox checkBox : userCheckBoxMap.keySet()) {
            checkBox.setSelected(selectAll);
        }
        updateSelectedCount();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleRefreshUsers() {
        loadUsers();
        showStatus("User list refreshed!", "success");
    }

    @FXML
    private void handleClearFilters() {
        studentFilterCheckBox.setSelected(false);
        userCategoryComboBox.setValue("All Categories");
        searchField.clear();
        applyFilters();
        showStatus("Filters cleared", "info");
    }

    private void updateSelectedCount() {
        long count = userCheckBoxMap.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .count();
        selectedCountLabel.setText("Selected: " + count + " users");
    }

    private void updateFilteredCount(int count) {
        filteredCountLabel.setText("Showing: " + count + " / " + allUsers.size() + " users");
    }

    @FXML
    private void handleSendOffers() {
        // Validate offer selection
        if (offerTypeComboBox.getValue() == null) {
            showStatus("Please select an offer type", "error");
            return;
        }

        // Get selected users
        List<UserDTO> selectedUsers = getSelectedUsers();
        if (selectedUsers.isEmpty()) {
            showStatus("Please select at least one user", "error");
            return;
        }

        // Get offer details
        OfferTemplate offer = getOfferDetails();
        if (offer == null) {
            showStatus("Please fill in all offer details", "error");
            return;
        }

        // Confirm sending
        showConfirmDialog(
                "Send Offer Tokens",
                String.format("Send '%s' offer to %d user(s)?", offer.title, selectedUsers.size()),
                () -> sendOffersToUsers(selectedUsers, offer)
        );
    }

    private List<UserDTO> getSelectedUsers() {
        return userCheckBoxMap.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private OfferTemplate getOfferDetails() {
        String selectedOffer = offerTypeComboBox.getValue();

        if ("Custom Offer".equals(selectedOffer)) {
            // Validate custom offer fields
            String title = offerTitleField.getText().trim();
            String discountStr = discountField.getText().trim();
            String description = offerDescriptionArea.getText().trim();

            if (title.isEmpty() || discountStr.isEmpty() || description.isEmpty()) {
                return null;
            }

            try {
                int discount = Integer.parseInt(discountStr);
                return new OfferTemplate(title, discount, description);
            } catch (NumberFormatException e) {
                showStatus("Invalid discount percentage", "error");
                return null;
            }
        } else {
            return OFFER_TEMPLATES.get(selectedOffer);
        }
    }

    private void sendOffersToUsers(List<UserDTO> users, OfferTemplate offer) {
        sendButton.setDisable(true);
        showStatus("Sending offers... Please wait", "info");

        // Send in background thread
        new Thread(() -> {
            int successCount = 0;
            int failCount = 0;

            for (UserDTO user : users) {
                try {
                    String token = generateToken();
                    String emailBody = buildEmailBody(user, offer, token);

                    emailService.sendEmail(
                            user.getEmail(),
                            "Special Offer from Event Sync - " + offer.title,
                            emailBody
                    );
                    user.setDiscountToken(token);
                    user.setDiscountAmount(offer.discount);
                    userService.updateUser(user);
                    successCount++;
                    Thread.sleep(100); // Small delay between emails
                } catch (Exception e) {
                    failCount++;
                    e.printStackTrace();
                }
            }

            final int finalSuccess = successCount;
            final int finalFail = failCount;

            Platform.runLater(() -> {
                sendButton.setDisable(false);
                if (finalFail == 0) {
                    showStatus(String.format("Successfully sent offers to %d users!", finalSuccess), "success");
                } else {
                    showStatus(String.format("Sent: %d, Failed: %d", finalSuccess, finalFail), "warning");
                }
            });
        }).start();
    }

    private String buildEmailBody(UserDTO user, OfferTemplate offer, String token) {
        return String.format(
                "Dear %s,\n\n" +
                        "🎉 Great news! We have a special offer just for you!\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "  %s\n" +
                        "  💰 %d%% OFF\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "%s\n\n" +
                        "Your exclusive offer token: %s\n\n" +
                        "How to redeem:\n" +
                        "1. Log in to Event Sync\n" +
                        "2. Place your order\n" +
                        "3. Apply this token at checkout\n" +
                        "4. Enjoy your discount!\n\n" +
                        "This offer is exclusively for you. Don't miss out!\n\n" +
                        "Best regards,\n" +
                        "Event Sync Team\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Questions? Contact us at support@eventsync.com",
                user.getName(),
                offer.title,
                offer.discount,
                offer.description,
                token
        );
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }

    @FXML
    private void handlePreview() {
        OfferTemplate offer = getOfferDetails();
        if (offer == null) {
            showStatus("Please select or configure an offer first", "error");
            return;
        }

        List<UserDTO> selectedUsers = getSelectedUsers();
        if (selectedUsers.isEmpty()) {
            showStatus("Please select at least one user to preview", "error");
            return;
        }

        // Show preview dialog
        String sampleEmail = buildEmailBody(selectedUsers.get(0), offer, "SAMPLE123");
        showPreviewDialog(sampleEmail);
    }

    private void showPreviewDialog(String emailContent) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Email Preview");

        TextArea textArea = new TextArea(emailContent);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 10px 30px;");
        closeButton.setOnAction(e -> dialog.close());

        VBox layout = new VBox(15, textArea, closeButton);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center; -fx-background-color: #2C3E50;");
        VBox.setVgrow(textArea, javafx.scene.layout.Priority.ALWAYS);

        Scene scene = new Scene(layout, 600, 500);
        dialog.setScene(scene);
        dialog.show();
    }

    @FXML
    private void handleCancel() {
        handleBack();
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/home.fxml"));
            loader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene homeScene = new Scene(loader.load());

            HomeController homeController = loader.getController();
            if (homeController != null && loggedInUser != null) {
                homeController.setLoggedInUser(loggedInUser);
            }

            stage.setScene(homeScene);
        } catch (IOException e) {
            e.printStackTrace();
            showStatus("Error returning to home", "error");
        }
    }

    private void showStatus(String message, String type) {
        statusLabel.setText(message);
        switch (type) {
            case "success":
                statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 14px;");
                break;
            case "error":
                statusLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 14px;");
                break;
            case "warning":
                statusLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 14px;");
                break;
            default:
                statusLabel.setStyle("-fx-text-fill: #3ac5ac; -fx-font-size: 14px;");
        }
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button yesButton = new Button("Yes, Send");
        yesButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px 30px;");
        yesButton.setOnAction(e -> {
            dialog.close();
            onConfirm.run();
        });

        Button noButton = new Button("Cancel");
        noButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-padding: 10px 30px;");
        noButton.setOnAction(e -> dialog.close());

        HBox buttons = new HBox(15, yesButton, noButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        VBox layout = new VBox(20, msgLabel, buttons);
        layout.setStyle("-fx-padding: 30px; -fx-alignment: center; -fx-background-color: #2C3E50;");

        Scene scene = new Scene(layout);
        dialog.setScene(scene);
        dialog.show();
    }

    // Inner class for Offer Template
    private static class OfferTemplate {
        String title;
        int discount;
        String description;

        public OfferTemplate(String title, int discount, String description) {
            this.title = title;
            this.discount = discount;
            this.description = description;
        }
    }
}