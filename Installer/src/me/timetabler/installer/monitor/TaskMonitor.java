package me.timetabler.installer.monitor;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * A dialog which displays the messages and progress of a task.
 */
public class TaskMonitor {
    /**
     * The task being monitored.
     */
    private Task task;

    /**
     * The stage which contains the user interface.
     */
    private Stage stage;

    /**
     * A boolean to signify if the monitor is ready to be shown.
     */
    public boolean ready = false;

    /**
     * Initialises the monitor. The ready boolean will be set to false if the scene for the stage cannot be initialised.
     * @param task The task to be monitored.
     */
    public TaskMonitor(Task task) {
        this.task = task;
        stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setController(new MonitorController(task));
        loader.setLocation(getClass().getResource("monitor.fxml"));
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("IOException has occurred!");
            alert.setContentText("The IOException [" + e.getMessage() + "] has occurred. Please contact the system maintainer.");
            alert.showAndWait();
            return;
        }

        ready = true;
    }

    /**
     * Sets the title for the monitor.
     * @param title The title for the monitor.
     */
    public void setTitle(String title) {
        stage.setTitle(title);
    }

    /**
     * Shows the monitor window.
     */
    public void show() {
        stage.show();
    }

    /**
     * Sets what should happen when the monitor window is closed.
     * @param handler The event handler for when the monitor closes.
     */
    public void setOnClose(EventHandler<WindowEvent> handler) {
        stage.setOnCloseRequest(handler);
    }
}
