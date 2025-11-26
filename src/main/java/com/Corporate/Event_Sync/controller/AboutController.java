package com.Corporate.Event_Sync.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AboutController {

    @FXML
    private Label titleLabel;

    @FXML
    private Button back;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label featuresLabel;

    @FXML
    private Label featuresDetailLabel;

    @FXML
    public void initialize() {
        // Setting title and description
        titleLabel.setText("About Event Sync Solutions");
        descriptionLabel.setText("Event Sync Solutions is an innovative Lunch Management Application designed for corporate environments. It streamlines meal management with advanced features, ensuring seamless operations, efficient order handling, and an enhanced dining experience for employees.");

        // Setting key features
        featuresLabel.setText("Key Features:");
        featuresDetailLabel.setText(
                "1. Intuitive User Dashboard:\n" +
                        "   - A centralized dashboard to view and manage orders.\n" +
                        "   - Real-time updates on order statuses and payment details.\n\n" +

                        "2. Role-Based Access Control (RBAC):\n" +
                        "   - Separate roles for Admin and User with distinct access permissions.\n" +
                        "   - Secure management of features and data.\n\n" +

                        "3. Predefined Menu Combinations:\n" +
                        "   - Offers five curated menu combinations.\n" +
                        "   - Simplifies ordering while ensuring dietary diversity.\n\n" +

                        "4. Automated Order Scheduling:\n" +
                        "   - Automatically schedules recurring orders using DefaultWeekDays logic.\n" +
                        "   - Customizable schedules for users.\n\n" +

                        "5. Dynamic Order Updates:\n" +
                        "   - Updates order statuses in real-time based on availability and time slots.\n" +
                        "   - Supports admin-driven modifications.\n\n" +

                        "6. User Profile Customization:\n" +
                        "   - Includes profile pictures and detailed user information fields.\n" +
                        "   - Admins can activate, deactivate, and manage users.\n\n" +

                        "7. Comprehensive Reporting Tools:\n" +
                        "   - Provides insights into order trends and menu preferences.\n" +
                        "   - Future-ready for AI-based analytics and suggestions.\n\n" +

                        "8. Modern UI Design:\n" +
                        "   - Developed using JavaFX for responsiveness and dynamic visuals.\n" +
                        "   - Galaxy-themed interface with gradient effects for a premium experience.\n\n" +

                        "How It Works:\n" +

                        "1. User Registration and Login:\n" +
                        "   - Employees register through a secure portal with role-based access.\n" +
                        "   - Login credentials are authenticated via Keycloak for enhanced security.\n\n" +

                        "2. Menu Management:\n" +
                        "   - Admins define up to 20 menu items categorized into Breakfast, Lunch, Snacks, and Dinner.\n" +
                        "   - Each menu item includes an image and details such as category and availability times.\n\n" +

                        "3. Ordering Process:\n" +
                        "   - Users can browse available menu combinations and place orders based on predefined choices.\n" +
                        "   - Orders are scheduled based on user-selected DefaultWeekDays or placed on demand.\n\n" +

                        "4. Order Status Updates:\n" +
                        "   - Order statuses are dynamically updated based on real-time factors like time slots and menu availability.\n" +
                        "   - Users receive live updates on their dashboards.\n\n" +

                        "5. Data Management:\n" +
                        "   - The app uses PostgreSQL to store user data, order history, and menu items.\n" +
                        "   - Automated deletion of inactive users’ orders ensures database integrity.\n\n" +

                        "6. Admin Controls:\n" +
                        "   - Admins manage users, orders, and menus through a dedicated interface.\n" +
                        "   - Built-in analytics provide insights into user behavior and menu preferences.\n\n" +

                        "System Requirements:\n" +

                        "Operating System:\n" +
                        "   - Windows 10/11 (64-bit)\n" +
                        "   - macOS 12 or later\n" +
                        "   - Linux distributions with Java 21+ support\n\n" +

                        "Hardware:\n" +
                        "   - Processor: Intel Core i5 or AMD Ryzen 5 equivalent (or higher)\n" +
                        "   - RAM: 8 GB (16 GB recommended)\n" +
                        "   - Storage: Minimum 512 MB free disk space for installation and database storage\n" +
                        "   - Graphics: Integrated GPU supporting OpenGL 2.0 or higher\n\n" +

                        "Software:\n" +
                        "   - Java Development Kit (JDK): Version 21.0.4 or higher\n" +
                        "   - Database: PostgreSQL 15+\n" +
                        "   - Dependencies: Spring Boot 3.3.4, JavaFX 22.0.1\n" +
                        "   - Web Browser: Latest version of Chrome, Firefox, or Edge for Keycloak access\n\n" +

                        "Additional Requirements:\n" +
                        "   - Internet connection for first-time setup and Keycloak integration\n" +
                        "   - Administrative privileges for installation\n\n" +

                        "Technologies and Architecture:\n" +

                        "Frontend:\n" +
                        "   - JavaFX provides an interactive and user-friendly interface.\n" +
                        "   - Custom CSS enables dynamic themes and designs, including a galaxy-inspired look.\n\n" +

                        "Backend:\n" +
                        "   - Powered by Spring Boot for scalable and robust server-side processing.\n" +
                        "   - Spring Security manages authentication and role-based access.\n\n" +

                        "Database:\n" +
                        "   - PostgreSQL is the primary database for storing all critical data.\n" +
                        "   - Hibernate ensures efficient object-relational mapping.\n\n" +

                        "Integration:\n" +
                        "   - Keycloak handles user authentication and management.\n" +
                        "   - REST APIs enable seamless communication between components.\n\n" +

                        "Other Technologies:\n" +
                        "   - Lombok for concise and maintainable code.\n" +
                        "   - MapStruct for efficient object mapping across layers.\n\n" +

                        "Future Enhancements:\n" +
                        "   - AI-Driven Recommendations: Suggests meals based on user preferences, history, and company trends.\n" +
                        "   - Wellness Integration: Aligns meal options with wellness programs for a healthier workforce.\n" +
                        "   - Cloud Deployment: Expands scalability with cloud-based hosting.\n" +
                        "   - Feedback System: Collects user feedback to refine menus and enhance user satisfaction.\n\n" +

                        "Event Sync Solutions delivers a unique blend of functionality and innovation, designed to cater to the specific needs of corporate meal management. By automating repetitive tasks, providing powerful analytics, and offering a seamless user experience, Event Sync is the ultimate tool for modern organizations."
        );
    }


    @FXML
    public void mainDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/main.fxml"));
            Parent root = loader.load();

            // Optionally, pass any necessary controllers or data to the new view
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Even Sync. Solutions");
        } catch (IOException e) {
            e.printStackTrace();
            // Show an error message to the user
        }
    }

    @FXML
    private void handleExit() {
        // Logic to handle exit action, e.g., closing the application
        System.exit(0);
    }
}
