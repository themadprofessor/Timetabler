package me.timetabler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.timetabler.ui.Controller;
import me.util.Log;

import java.io.File;

/**
 * The entry point of the program. It handles command line parameters
 */
public class Main extends Application{
    private School school;
    private SchoolDataParser parser;

    /**
     * Entry point to the program and handles command line parameters
     * @param args The command line parameters
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--debug".equals(args[i])) {
                Log.DEBUG = true;
                i++;
            }
        }
        launch(args);
    }

    /**
     * Called before UI is initialised. Loads and parses all school data and initialises the map
     */
    @Override
    public void init() {
        try {
            school = new School(new File("assets"));
            Log.out("Loaded School Map");
            parser = new SchoolDataParser();
            school.staff = parser.readStaff(new File("staff.csv"));
            Log.debug("Read " + school.staff.size() + " Staff");
            school.subjects = parser.readSubjects(new File("subjects.csv"));
            Log.debug("Read " + school.subjects.size() + " Subjects");
            Log.out("Loaded School Data");
        } catch (Exception e) {
            Log.err(e);
        }
    }

    /**
     * Initialises the UI
     * @param primaryStage The primary JavaFX stage for the Ui to placed in
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main.fxml"));
            Controller controller = new Controller(school.subjects, school.staff);
            loader.setController(controller);
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Timetabler");
            primaryStage.show();
        } catch (Exception e) {
            Log.err(e);
        }
    }

    /**
     * Called after the UI is closed. Exports school data to CSV.
     */
    @Override
    public void stop() {
        parser.writeStaff(new File("staff.csv"), school.staff);
        Log.debug("Wrote " + school.staff.size() + " Staff");
        parser.writeSubjects(new File("subjects.csv"), school.subjects);
        Log.debug("Wrote " + school.subjects.size() + " Subjects");
        Log.out("Saved School Data");
    }
}

