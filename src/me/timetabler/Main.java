package me.timetabler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.timetabler.config.ConfigParser;
import me.timetabler.config.ConfigType;
import me.timetabler.data.flatfiles.SchoolDataParser;
import me.timetabler.ui.Controller;
import me.util.Log;
import me.util.LogLevel;
import me.util.MultipleOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * The entry point of the program. It handles command line parameters.
 */
public class Main extends Application{
    private School school;
    private SchoolDataParser parser;
    private static ConfigType configType = ConfigType.YAML;

    /**
     * Entry point to the program and handles command line parameters.
     * @param args The command line parameters.
     */
    public static void main(String[] args) {
        String lvl = "e";
        for (int i = 0; i < args.length; i++) {
            if ("-l".equals(args[i])) {
                i++;
                lvl = args[i];
            }
        }
        setupLogging(lvl);
        launch(args);
    }

    /**
     * Called before UI is initialised. Loads and parses all school data and initialises the map.
     */
    @Override
    public void init() {
        try {
            Map<String, Map<String, String>> config = ConfigParser.getParser(configType, "assets/config.yaml").parse();
            if (config == null) {
                Log.error("Unknown config type!");
                System.exit(1);
            }
            school = new School(config.get("map"));
            Log.info("Loaded School Map");
            parser = SchoolDataParser.getParser(config.get("data"));
            if (parser == null) {
                Log.error("Unknown data type!");
            }
            school.staff = parser.readStaff();
            Log.debug("Read " + school.staff.size() + " Staff");
            school.subjects = parser.readSubjects();
            Log.debug("Read " + school.subjects.size() + " Subjects");
            school.classes = parser.readClasses();
            Log.debug("Read " + school.classes.size() + " Classes");
            Log.info("Loaded School Data");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Initialises the UI.
     * @param primaryStage The primary JavaFX stage for the Ui to placed in.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main.fxml"));
            Controller controller = new Controller(school.subjects, school.staff, school.classes);
            loader.setController(controller);
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Timetabler");
            primaryStage.show();

        } catch (Exception e) {
            Log.error(e);
        }
    }

    /**
     * Called after the UI is closed. Exports school data to CSV.
     */
    @Override
    public void stop() {
        try {
            parser.writeStaff(school.staff);
            Log.debug("Wrote " + school.staff.size() + " Staff");
            parser.writeSubjects(school.subjects);
            Log.debug("Wrote " + school.subjects.size() + " Subjects");
            parser.writeClasses(school.classes);
            Log.debug("Wrote " + school.classes.size() + " Classes");
            Log.info("Saved School Data");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    private static void setupLogging(String level) {
        if ("v".equals(level)) {
            Log.LEVEL = LogLevel.VERBOSE;
        } else if ("d".equals(level)) {
            Log.LEVEL = LogLevel.DEBUG;
        } else if ("i".equals(level)) {
            Log.LEVEL = LogLevel.INFO;
        } else if ("w".equals(level)) {
            Log.LEVEL = LogLevel.WARNING;
        } else if ("e".equals(level)) {
            Log.LEVEL = LogLevel.ERROR;
        } else if ("n".equals(level)) {
            Log.error("Log Level [NONE] Is Not Recommended!");
            Log.LEVEL = LogLevel.NONE;
        } else {
            Log.error("Unknown Log Level [" + level + "] Setting To Default [ERROR]");
        }

        try {
            File outFile = new File("log/" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll(":", "-") + "_out.log");
            outFile.getParentFile().mkdirs();
            outFile.createNewFile();

            File errFile = new File("log/" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll(":", "-") + "_err.log");
            outFile.getParentFile().mkdirs();
            errFile.createNewFile();

            MultipleOutputStream out = new MultipleOutputStream(System.out, new BufferedOutputStream(new FileOutputStream(outFile)));
            MultipleOutputStream err = new MultipleOutputStream(System.err, new BufferedOutputStream(new FileOutputStream(errFile)));

            Log.NORMAL_WRITER = new PrintWriter(out);
            Log.ERROR_WRITER = new PrintWriter(err);
        } catch (java.io.IOException e) {
            Log.error(e);
        }
    }
}

