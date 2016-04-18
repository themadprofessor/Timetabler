package me.timetabler.installer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The entry point to the installer program. It initialises JavaFX and the interface into it.
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Timetabler Installer");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("installer.fxml"))));
        primaryStage.show();
    }
}
