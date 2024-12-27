
package com.Corporate.Event_Sync.controller;

import com.Corporate.Event_Sync.EventSyncApplication;
import com.Corporate.Event_Sync.dto.MenuItemDto;
import com.Corporate.Event_Sync.dto.OrderDTO;
import com.Corporate.Event_Sync.dto.UserDTO;
import com.Corporate.Event_Sync.dto.mapper.OrderMapper;
import com.Corporate.Event_Sync.service.menuItemService.MenuItemListService;
import com.Corporate.Event_Sync.service.orderService.OrderListService;
import com.Corporate.Event_Sync.service.orderService.OrderService;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Component
public class ReportController {

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
    private TableColumn<OrderDTO, Integer> priceColumn;
    @FXML
    private TableColumn<OrderDTO, Void> deleteOrderColumn;
    @FXML
    private Button generate;
    @FXML
    private Button allOrderDashboard;

    @FXML// Date picker for order date
    private TextField reportDatePickerAll;
    @FXML
    private Label totalPriceLabel;
    private CreateOrderController createOrderController;

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        menuItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
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
        loadOrders();
        updateTotalPrice();
    }

    private void loadOrders() {
        try {
            List<OrderDTO> orderDTOs = orderMapper.toDTOList(orderListService.getAllOrders());
            ObservableList<OrderDTO> orders = FXCollections.observableArrayList(orderDTOs);
            orderTable.setItems(orders);

        } catch (Exception e) {
            showErrorDialog("Error loading orders: " + e.getMessage());
        }
    }

    @FXML
    private void updateTotalPrice() {
        double totalPrice = orderTable.getItems().stream()
                .mapToDouble(OrderDTO::getPrice)
                .sum();
        totalPriceLabel.setText("Total Price: BDT " + String.format("%.2f ", totalPrice));
    }

    private void deleteOrder(OrderDTO order) {
        showConfirmationDialog("Are you sure you want to delete order: " + order.getOrderId() + "?", () -> {
            orderService.deleteOrderById(Long.valueOf(order.getOrderId()));
            showSuccessDialog("Deleted order: " + order.getOrderId());
            // Refresh the list after deletion
            loadOrders();
        });
    }

    // Update method that uses the selected order ID
    @FXML
    private void updateOrder() {

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
            showErrorDialog("Error moving to home.");
        }
    }

    @FXML
    private void allOrderDashboard() {
        try {
            Stage stage = (Stage) allOrderDashboard.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.Corporate.Event_Sync/allOrders.fxml"));
            fxmlLoader.setControllerFactory(EventSyncApplication.context::getBean);
            Scene loginScene = new Scene(fxmlLoader.load());
            stage.setScene(loginScene);

        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error loading dashboard.");
        }
    }

    @FXML
    private void generateReport() {
        if (orderTable.getItems() == null || orderTable.getItems().isEmpty()) {
            showErrorDialog("No orders available to generate the report.");
            return;
        }
        String input = reportDatePickerAll.getText();
        final String[] selectedDate = {null};
        final String[] selectedMonth = {null};
        final String[] selectedYear = {null};

// Try parsing date in different formats
        if (input.matches("\\d{2}/\\d{2}/\\d{4}")) {
            selectedDate[0] = input; // Full date (dd/MM/yyyy)
        } else if (input.matches("\\d{2}/\\d{4}")) {
            selectedMonth[0] = input.substring(0, 2); // Month (MM)
            selectedYear[0] = input.substring(3, 7);  // Year (yyyy)
        } else if (input.matches("\\d{4}")) {
            selectedYear[0] = input; // Year only (yyyy)
        }
// Stream through the orders and apply the filters
        List<OrderDTO> filteredOrders;
// If all inputs are null, take the full list
        if (selectedDate[0] == null && selectedMonth[0] == null && selectedYear[0] == null) {
            filteredOrders = new ArrayList<>(orderTable.getItems());
        } else {
            filteredOrders = orderTable.getItems().stream()
                    .filter(order -> {
                        // Format order date into required strings
                        String orderDateStr = order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        String orderMonthYear = order.getOrderDate().format(DateTimeFormatter.ofPattern("MM/yyyy"));
                        String orderMonth = order.getOrderDate().format(DateTimeFormatter.ofPattern("MM"));
                        String orderYear = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy"));

                        // Check each condition based on user selection
                        if (selectedDate[0] != null) {
                            return orderDateStr.equals(selectedDate[0]); // Match exact date
                        }
                        if (selectedMonth[0] != null && selectedYear[0] != null) {
                            return orderMonthYear.equals(selectedMonth[0] + "/" + selectedYear[0]); // Match month/year
                        }
                        if (selectedMonth[0] != null) {
                            return orderMonth.equals(selectedMonth[0]); // Match month only
                        }
                        if (selectedYear[0] != null) {
                            return orderYear.equals(selectedYear[0]); // Match year only
                        }
                        return false; // Exclude orders if no specific filter is applied
                    })
                    .collect(Collectors.toList());
        }

// Check if filteredOrders is empty or input does not match any existing date/month/year
        boolean isDateMismatch = selectedDate[0] != null && filteredOrders.stream()
                .noneMatch(order -> order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).equals(selectedDate[0]));

        boolean isMonthYearMismatch = selectedMonth[0] != null && selectedYear[0] != null && filteredOrders.stream()
                .noneMatch(order -> order.getOrderDate().format(DateTimeFormatter.ofPattern("MM/yyyy")).equals(selectedMonth[0] + "/" + selectedYear[0]));

        boolean isMonthMismatch = selectedMonth[0] != null && filteredOrders.stream()
                .noneMatch(order -> order.getOrderDate().format(DateTimeFormatter.ofPattern("MM")).equals(selectedMonth[0]));

        boolean isYearMismatch = selectedYear[0] != null && filteredOrders.stream()
                .noneMatch(order -> order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy")).equals(selectedYear[0]));

// Show error dialog if no orders match the selected input
        if (filteredOrders.isEmpty() || isDateMismatch || isMonthYearMismatch || isMonthMismatch || isYearMismatch) {
            showErrorDialog("No orders match the selected input.");
            return;  // Stop further processing if no orders are found
        }

        // Generate report with filteredOrders
        String pdfPath = "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/order_report.pdf";
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(pdfPath));
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            // Set background color for the entire page
            Rectangle pageSize = pdfDoc.getDefaultPageSize();
            PdfCanvas canvas = new PdfCanvas(pdfDoc.addNewPage());
            canvas.setFillColor(new DeviceRgb(139, 0, 0)); // Dark Red
            canvas.rectangle(0, 0, pageSize.getWidth(), pageSize.getHeight());
            canvas.fill();

            // Add current date and time in the top-right corner
            float yPosition = 790; // Starting Y position for the paragraphs
            float xMargin = 40;    // Left margin for alignment

            String currentDateTime = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

// Add the current date-time paragraph
            document.add(new Paragraph(currentDateTime)
                    .setFixedPosition(53, yPosition, 500)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(13)
                    .setFontColor(ColorConstants.BLACK));

// Add the selected date/month/year or default message
            if (selectedDate[0] != null) {
                document.add(new Paragraph("Selected Date: " + selectedDate[0])
                        .setFixedPosition(xMargin, yPosition , 500) // Adjust Y position
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(13)
                        .setFontColor(ColorConstants.BLACK));
            } else if (selectedMonth[0] != null && selectedYear[0] != null) {
                document.add(new Paragraph("Selected Month: " + selectedMonth[0] + "/" + selectedYear[0])
                        .setFixedPosition(xMargin, yPosition , 500) // Adjust Y position
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(13)
                        .setFontColor(ColorConstants.BLACK));
            } else if (selectedYear[0] != null) {
                document.add(new Paragraph("Selected Year: " + selectedYear[0])
                        .setFixedPosition(xMargin, yPosition , 500) // Adjust Y position
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(13)
                        .setFontColor(ColorConstants.BLACK));
            } else {
                document.add(new Paragraph("Nothing selected.")
                        .setFixedPosition(xMargin, yPosition , 500) // Adjust Y position
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontSize(13)
                        .setFontColor(ColorConstants.BLACK));
            }

            String logoPath = "/home/rakib/Downloads/rakib/SDP_2/Event_Sync/Images/10466750.jpg"; // Provide your logo path
            ImageData logo = ImageDataFactory.create(logoPath);
            Image logoImage = new Image(logo);
            logoImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            logoImage.setWidth(80); // Adjust size as needed
            logoImage.setHeight(80);
            document.add(logoImage);

            document.add(new Paragraph("Event Sync. Solutions")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));
            // Add report title
            document.add(new Paragraph("Order Report")
                    .setFontSize(18)
                    .setBold()
                    .setFontColor(ColorConstants.BLACK)
                    .setMarginBottom(10)
                    .setTextAlignment(TextAlignment.CENTER));

            Table table = new Table(new float[]{2, 2, 2, 2, 2, 2, 3});
            float pageWidth = PageSize.A4.getWidth(); // Use A4 page size or your specific page size
            float tableWidth = pageWidth * 0.85f; // 90% of the page width

            table.setWidth(tableWidth);
            // Set the table margins (you can adjust these values to suit your design)// Right margin
            table.setHorizontalAlignment(HorizontalAlignment.CENTER); // Center the table
            // Add headers with styling
            Arrays.asList("Order ID", "User ID", "Department", "Item ID", "Status", "Price", "Order Date")
                    .forEach(header -> table.addHeaderCell(
                            new com.itextpdf.layout.element.Cell()
                                    .add(new Paragraph(header).setBold().setFontColor(ColorConstants.BLACK))
                                    .setBackgroundColor(new DeviceRgb(165, 42, 42)) // Lighter Red for Header Background
                                    .setTextAlignment(TextAlignment.CENTER)));

            Map<Integer, String> menuItemCategoryMap = menuItemListService.getAllMenuItems().stream()
                    .collect(Collectors.toMap(MenuItemDto::getId, MenuItemDto::getCategory));

            long[] categoryCounts = {0, 0, 0, 0}; // Order: BREAKFAST, LUNCH, SNACKS, DINNER
            long totalOrders = 0;
            double totalPrice = 0.0;
            Map<String, Double> departmentWiseTotal = new HashMap<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");

            for (OrderDTO order : filteredOrders) {
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(order.getOrderId()))).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(order.getUserId()))).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(order.getDepartment())).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(order.getMenuItemId()))).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(order.getStatus())).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(order.getPrice()))).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE));
                table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(order.getOrderDate().format(formatter))).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.WHITE));

                totalPrice += order.getPrice();
                departmentWiseTotal.merge(order.getDepartment(), Double.valueOf(order.getPrice()), Double::sum);

                String category = menuItemCategoryMap.get(order.getMenuItemId());
                if (category != null) {
                    switch (category.toUpperCase()) {
                        case "BREAKFAST" -> categoryCounts[0]++;
                        case "LUNCH" -> categoryCounts[1]++;
                        case "SNACKS" -> categoryCounts[2]++;
                        case "DINNER" -> categoryCounts[3]++;
                    }
                }
                totalOrders++;
            }
            document.add(table.setMarginBottom(10));
            // Add order summary
            // Create a table with two columns
            Table alignmentTable = new Table(new float[]{3, 1}); // Adjust column width ratio as needed
            alignmentTable.setWidth(UnitValue.createPercentValue(100)); // Table width 100% of the page
// Add the first cell (Approved By)
            Cell orderSummaryCell = new Cell()
                    .setBorder(Border.NO_BORDER) // No border for a clean look
                    .setTextAlignment(TextAlignment.LEFT); // Align to the left side of its column
            orderSummaryCell.add(new Paragraph("Order Summary by Category:")
                    .setFontSize(13)
                    .setBold()
                    .setMarginLeft(6)
                    .setFontColor(ColorConstants.BLACK));
            alignmentTable.addCell(orderSummaryCell);
            Cell approvedByCell = new Cell()
                    .setBorder(Border.NO_BORDER) // No border for a clean look
                    .setTextAlignment(TextAlignment.RIGHT); // Align to the right side of its column
            approvedByCell.add(new Paragraph("Approved By:")
                    .setFontSize(13)
                    .setBold().setMarginRight(65)
                    .setFontColor(ColorConstants.BLACK));
            alignmentTable.addCell(approvedByCell);
            document.add(alignmentTable);

            document.add(new Paragraph("Breakfast: " + categoryCounts[0] +
                    ",\n Lunch: " + categoryCounts[1] +
                    ",\n Snacks: " + categoryCounts[2] +
                    ",\n Dinner: " + categoryCounts[3])
                    .setFontColor(ColorConstants.WHITE)
                    .setMarginLeft(8));
            document.add(new Paragraph("Total Orders: " + totalOrders)
                    .setBold().setFontColor(ColorConstants.BLACK).setMarginLeft(7));
            // Add department-wise total price summary
            document.add(new Paragraph("\nDepartment-wise Total Price:")
                    .setFontSize(13).setBold().setFontColor(ColorConstants.BLACK).setMarginLeft(8));
            for (Map.Entry<String, Double> entry : departmentWiseTotal.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": BDT " + String.format("%.2f", entry.getValue()))
                        .setFontColor(ColorConstants.WHITE).setMarginLeft(8));
            }
            // Add total price
            document.add(new Paragraph("Total Price: BDT " + String.format("%.2f", totalPrice))
                    .setBold().setFontColor(ColorConstants.BLACK).setMarginLeft(8));
            showSuccessDialog("Report generated Successfully. You can download it from: " + pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog("Error generating report.");
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
        // Check if 'generate' has a valid scene
        if (generate.getScene() != null && generate.getScene().getWindow() != null) {
            dialogStage.initOwner(generate.getScene().getWindow());
        } else {
            System.err.println("Scene or Window is null. Using default stage.");
        }
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

        // Check if 'generate' has a valid scene
        if (generate.getScene() != null && generate.getScene().getWindow() != null) {
            dialogStage.initOwner(generate.getScene().getWindow());
        } else {
            System.err.println("Scene or Window is null. Using default stage.");
        }

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
