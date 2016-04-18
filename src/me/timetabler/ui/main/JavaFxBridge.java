package me.timetabler.ui.main;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Static utility methods for JavaFX.
 */
public class JavaFxBridge {
    /**
     * Creates and displays an alert to the user of the given type with the given title, header and message. If
     * closeOnExit is true, the system will close when the dialog is closed.
     * @param type The type of alert.
     * @param title The title of the alert.
     * @param header The header of the alert.
     * @param message The main text of the alert.
     * @param closeOnExit True if the system should exit when the alert closes.
     */
    public static void createAlert(Alert.AlertType type, String title, String header, String message, boolean closeOnExit) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.setResizable(true);

            if (closeOnExit) {
                alert.setOnCloseRequest(value -> close());
            }
            alert.showAndWait();
        });
    }

    /**
     * Closes the system. It allows JavaFX to close gracefully and call the Application's close method.
     */
    public static void close() {
        Platform.exit();
    }
}
