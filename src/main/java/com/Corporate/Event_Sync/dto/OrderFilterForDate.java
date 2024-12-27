package com.Corporate.Event_Sync.dto;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderFilterForDate {

    @FXML
    private DatePicker reportDatePickerAll;  // Injected via FXML
    @FXML
    private ComboBox<String> monthsComboBoxAll; // Injected via FXML
    @FXML
    private TableView<OrderDTO> orderTable; // Injected via FXML

    // Injected values will be initialized by FXML loader
    public void filterOrders() {
        // Extract filter values
        LocalDate selectedDate = reportDatePickerAll.getValue(); // DatePicker value
        String selectedMonth = monthsComboBoxAll.getValue(); // ComboBox value
        Integer selectedYear = selectedDate != null ? selectedDate.getYear() : null; // Extract year from selectedDate

        // Filter orders
        List<OrderDTO> filteredOrders = orderTable.getItems().stream()
                .filter(order -> {
                    boolean matchesDate = true;
                    boolean matchesMonth = true;
                    boolean matchesYear = true;

                    if (selectedDate != null) {
                        matchesDate = order.getOrderDate().equals(selectedDate);
                    }

                    if (selectedMonth != null) {
                        int orderMonth = order.getOrderDate().getMonthValue();
                        int selectedMonthValue = Month.valueOf(selectedMonth.toUpperCase()).getValue();
                        matchesMonth = orderMonth == selectedMonthValue;
                    }

                    if (selectedYear != null) {
                        int orderYear = order.getOrderDate().getYear();
                        matchesYear = orderYear == selectedYear;
                    }

                    if (selectedDate != null && selectedMonth != null && selectedYear != null) {
                        return matchesDate && matchesMonth && matchesYear;
                    } else if (selectedDate != null) {
                        return matchesDate;
                    } else if (selectedMonth != null && selectedYear != null) {
                        return matchesMonth && matchesYear;
                    } else if (selectedMonth != null) {
                        return matchesMonth;
                    } else if (selectedYear != null) {
                        return matchesYear;
                    }

                    return false;
                })
                .collect(Collectors.toList());

        orderTable.getItems().setAll(filteredOrders);
    }
}

