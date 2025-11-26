package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.exceptions.NotFoundException;
import com.Corporate.Event_Sync.service.mailService.EmailService;
import com.Corporate.Event_Sync.service.userService.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;

@Component
public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField tokenField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Label instructionLabel;

    @FXML
    private Button sendTokenButton;

    @FXML
    private Button resetPasswordButton;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationContext context;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 6;

    @FXML
    public void initialize() {
        // Initially disable token and password fields
        tokenField.setDisable(true);
        newPasswordField.setDisable(true);
        confirmPasswordField.setDisable(true);
        resetPasswordButton.setDisable(true);
    }

    @FXML
    public void handleSendToken() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showErrorMessage("Please enter your email address");
            return;
        }

        try {
            // Check if user exists
            UserDTO user = userService.findByEmail(email);

            // Check if user already has a token
            if (user.getPassToken() != null && !user.getPassToken().isEmpty()) {
                showErrorMessage("A token has already been sent to this email. Please check your inbox or use the existing token.");

                // Disable email field and enable token field
                emailField.setDisable(true);
                tokenField.setDisable(false);
                newPasswordField.setDisable(false);
                confirmPasswordField.setDisable(false);
                resetPasswordButton.setDisable(false);
                sendTokenButton.setDisable(true);

                instructionLabel.setText("Enter the token from your email and your new password.");
                return;
            }

            // Generate random token
            String token = generateToken();

            // Save token to user
            user.setPassToken(token);
            userService.updateUser(user);

            // Send email with token
            String subject = "Password Reset Token - Event Sync";
            String body = "Hello " + user.getName() + ",\n\n" +
                    "You have requested to reset your password.\n\n" +
                    "Your password reset token is: " + token + "\n\n" +
                    "Please use this token to reset your password.\n\n" +
                    "If you did not request this, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Event Sync Team";

            emailService.sendEmail(email, subject, body);

            showSuccessMessage("Token sent successfully! Please check your email.");

            // Disable email field and enable token field
            emailField.setDisable(true);
            tokenField.setDisable(false);
            newPasswordField.setDisable(false);
            confirmPasswordField.setDisable(false);
            resetPasswordButton.setDisable(false);
            sendTokenButton.setDisable(true);

            instructionLabel.setText("Enter the token from your email and your new password.");

        } catch (NotFoundException e) {
            showErrorMessage("No account found with this email address");
        } catch (Exception e) {
            showErrorMessage("Failed to send token. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleResetPassword() {
        String email = emailField.getText().trim();
        String token = tokenField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (token.isEmpty()) {
            showErrorMessage("Please enter the token from your email");
            return;
        }

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("Please enter and confirm your new password");
            return;
        }

        if (newPassword.length() < 6) {
            showErrorMessage("Password must be at least 6 characters long");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match");
            return;
        }

        try {
            // Fetch user
            UserDTO user = userService.findByEmail(email);

            // Verify token
            if (user.getPassToken() == null || !user.getPassToken().equals(token)) {
                showErrorMessage("Invalid token. Please check your email and try again.");
                return;
            }

            // Update password using User entity
            com.Corporate.Event_Sync.entity.User userEntity = userService.fetchUserById(user.getId());
            userEntity.setPassword(newPassword); // Will be encoded in registerUser or you need to encode here

            // Clear the token
            user.setPassToken(null);
            userService.updateUser(user);

            // You need to update password separately since updateUser doesn't handle password
            // Add this method to UserService or update password directly here
            userService.registerUser(userEntity); // This will encode the password

            showSuccessMessage("Password reset successfully! Redirecting to login...");

            // Wait 2 seconds then redirect to login
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::goToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (NotFoundException e) {
            showErrorMessage("User not found");
        } catch (Exception e) {
            showErrorMessage("Failed to reset password. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/login.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Scene loginScene = new Scene(root);
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorMessage("Failed to load login page");
        }
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);

        for (int i = 0; i < TOKEN_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            token.append(CHARACTERS.charAt(index));
        }

        return token.toString();
    }

    private void showErrorMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-wrap-text: true;");
    }

    private void showSuccessMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3ac5ac; -fx-wrap-text: true;");
    }
}