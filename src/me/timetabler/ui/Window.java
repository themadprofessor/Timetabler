package me.timetabler.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;

import java.util.List;

/**
 * Created by stuart on 16/09/15.
 */
public class Window extends Application {
    private static List<Subject> subjects;
    private static List<Staff> staff;
    public static void open(List<Subject> subjects, List<Staff> staff) {
        Window.subjects = subjects;
        Window.staff = staff;
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Controller controller = new Controller(subjects, staff);
        loader.setController(controller);
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Timetabler");

        primaryStage.show();
    }
}
