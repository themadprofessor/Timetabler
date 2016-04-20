package me.timetabler.installer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The entry point to the installer program. It initialises JavaFX and the interface into it.
 */
public class Main extends Application {
    /**
     * The entry point the the installer program.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * {@inheritDoc}
     * @throws IOException Thrown if the file 'installer.fxml' cannot be found.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Timetabler Installer");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("installer.fxml"))));
        primaryStage.show();
    }
}
