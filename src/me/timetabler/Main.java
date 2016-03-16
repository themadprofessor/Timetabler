package me.timetabler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.timetabler.config.ConfigParser;
import me.timetabler.config.ConfigType;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.ui.Controller;
import me.util.Log;
import me.util.LogLevel;
import me.util.MultipleWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * The entry point of the program. It handles command line parameters.
 */
public class Main extends Application{
    private School school;
    private DaoManager daoManager;
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
            daoManager = DaoManager.getManager(config.get("data_source"));
            Log.info("Initialised DaoManger");
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
            Controller controller = new Controller(daoManager);
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
     * Called after the UI is closed.
     */
    @Override
    public void stop() {
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

            MultipleWriter out = new MultipleWriter(new PrintWriter(System.out, true), new FileWriter(outFile, true));
            MultipleWriter err = new MultipleWriter(new PrintWriter(System.err, true), new FileWriter(errFile, true));

            Log.NORMAL_WRITER = new PrintWriter(out, true);
            Log.ERROR_WRITER = new PrintWriter(err, true);
        } catch (java.io.IOException e) {
            Log.error(e);
        }
    }
}

