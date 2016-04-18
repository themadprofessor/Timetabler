package me.timetabler.installer;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import me.timetabler.installer.monitor.TaskMonitor;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles user events from the user interface.
 */
public class InstallerController implements Initializable {
    /**
     * The button which starts the installation of the timetabler.
     */
    public Button install;

    /**
     * The field where the user specifies their password.
     */
    public PasswordField password;

    /**
     * The field where the user specifies the install path for the timetabler.
     */
    public TextField installPath;

    /**
     * The button which opens a directory chooser to file installPath.
     */
    public Button browse;

    /**
     * {@inheritDoc}
     * Sets the onAction functions for the install and browse button.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        browse.setOnAction(event -> browseForPath());
        install.setOnAction(event -> startInstall());
    }

    /**
     * Checks if the install path and password are specified, creating an alert if not. Then, initialises the background
     * install thread, initialises a monitor for the thread, shows the monitor and starts the install thread. If the
     * monitor window is closed, the background task will be told to cancel at its next cancel check.
     */
    private void startInstall() {
        if (installPath.getText() == null || installPath.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Install Path Specified!");
            alert.setContentText("Please specify an install path before continuing.");
            alert.showAndWait();
        } else if (password.getText() == null || password.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Password Specified!");
            alert.setContentText("Please specify an password before continuing.");
            alert.showAndWait();
        } else {
            InstallThread installThread = new InstallThread(new File(installPath.getText()), password.getText().toCharArray());
            TaskMonitor monitor = new TaskMonitor(installThread);
            monitor.setOnClose(event -> installThread.cancel());
            monitor.setTitle("Installing Timetabler");
            if (monitor.ready) {
                monitor.show();
                Thread thread = new Thread(installThread, "Background Installer Thread");
                thread.start();
            }
        }
    }

    /**
     * Creates a DirectoryChooser, awaits for the user to select a directory, checks if the directory has read and write
     * permissions, and sets the installPath's text to the absolute path of the selected directory if it meets the checks.
     * If it fails the check, an Alert is created with information for the user and installPath's text is not changed.
     */
    private void browseForPath() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select install folder.");
        File path = chooser.showDialog(null);
        if (path != null && path.isDirectory()) {
            if (!path.canWrite()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Cannot write to the install directory!");
                alert.setContentText("The selected directory cannot be written to. Either select another directory or give ["
                        + System.getProperty("user.name") + "] write permissions to [" + path.getPath() + "].");
                alert.showAndWait();
            } else if (!path.canRead()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Cannot read the install directory!");
                alert.setContentText("The selected directory cannot be written to. Either select another directory or give ["
                        + System.getProperty("user.name") + "] read permissions to [" + path.getPath() + "].");
                alert.showAndWait();
            } else {
                installPath.setText(path.getAbsolutePath());
            }
        }
    }
}
