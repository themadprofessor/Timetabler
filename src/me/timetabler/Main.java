package me.timetabler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.timetabler.ui.Controller;
import me.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by stuart on 24/08/15.
 */
public class Main extends Application{
    private School school;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--debug".equals(args[i])) {
                Log.DEBUG = true;
            }
        }
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        try {
            school = new School(new File("assets"));
            Log.out("Loaded School Map");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main.fxml"));
        Controller controller = new Controller(school.subjects, school.staff);
        loader.setController(controller);
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Timetabler");

        primaryStage.show();
    }
}
