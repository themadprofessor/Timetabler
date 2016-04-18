package me.timetabler.installer.monitor;

import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Binds the progress and message properties of a task to the progress bar and message label of the ui.
 */
public class MonitorController implements Initializable {
    public ProgressBar progress;
    public Label message;
    private Task task;

    public MonitorController(Task task) {
        this.task = task;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progress.progressProperty().bind(task.progressProperty());
        message.textProperty().bind(task.messageProperty());
    }
}
