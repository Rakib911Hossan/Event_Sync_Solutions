package com.Corporate.Event_Sync.service.locationService;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class SetLocation {

    private final Stage stage;
    private double latitude;
    private double longitude;
    private final Runnable onLocationSetCallback;

    public SetLocation(Stage stage, Runnable onLocationSetCallback) {
        this.stage = stage;
        this.onLocationSetCallback = onLocationSetCallback;
    }

    // This method is called from JS
    public void setLocation(double latitude, double longitude) {
        System.out.println("Received from JavaScript: " + latitude + ", " + longitude);
        this.latitude = latitude;
        this.longitude = longitude;

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Location Selected");
            alert.setHeaderText("Success!");
            alert.setContentText("Latitude: " + latitude + "\nLongitude: " + longitude);
            alert.showAndWait();

            // Notify that location is set
            if (onLocationSetCallback != null) {
                onLocationSetCallback.run();
            }

            // Close the map window
            stage.close();
        });
    }
}
