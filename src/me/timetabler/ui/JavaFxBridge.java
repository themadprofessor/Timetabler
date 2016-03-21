package me.timetabler.ui;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Created by stuart on 26/02/16.
 */
public class JavaFxBridge {
    public static void createAlert(Alert.AlertType type, String title, String header, String message, boolean closeOnExit) {
        Platform.runLater(() -> {
            Label label = new Label(message);

            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.setResizable(true);

            if (closeOnExit) {
                alert.setOnCloseRequest(value -> close());
            }
            alert.show();
        });
    }

    public static void close() {
        Platform.exit();
    }
}
