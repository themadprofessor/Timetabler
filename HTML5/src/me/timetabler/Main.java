package me.timetabler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by stuart on 22/07/15.
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("main.fxml")));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Timetabler");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}