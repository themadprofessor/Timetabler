package me.timetabler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.timetabler.config.ConfigParser;
import me.timetabler.config.ConfigType;
import me.timetabler.data.dao.DaoManager;
import me.timetabler.ui.MainController;
import me.timetabler.ui.main.JavaFxBridge;
import me.util.Log;
import me.util.LogLevel;
import me.util.MultipleWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * The entry point of the program. It handles command line parameters.
 */
public class Main extends Application {
    private DaoManager daoManager;
    private static ConfigType configType = ConfigType.YAML;

    /**
     * Entry point to the program and handles command line parameters.
     *
     * @param args The command line parameters.
     */
    public static void main(String[] args) {
        String lvl = "e";
        for (int i = 0; i < args.length; i++) {
            if ("-l".equals(args[i])) {
                i++;
                lvl = args[i];
            } else if ("-c".equals(args[i])) {
                i++;
                configType = ConfigType.valueOf(args[i]);
            }
        }
        setupLogging(lvl);
        launch(args);
    }

    /**
     * Called before UI is initialised. Parses the config and initialises the dao manager based on the config.
     */
    @Override
    public void init() {
        try {
            Map<String, Map<String, String>> config = ConfigParser.getParser(configType, "assets/config.yaml").parse();
            if (config == null) {
                Log.error("Unknown config type!");
                System.exit(1);
            }

            daoManager = DaoManager.getManager(config.get("data_source"));
            Log.info("Initialised DaoManger");
        } catch (Exception e) {
            Log.error(e);
            JavaFxBridge.close();
        }
    }

    /**
     * Initialises the UI.
     *
     * @param primaryStage The primary JavaFX stage for the Ui to placed in.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ui/main/main.fxml"));
            MainController controller = new MainController(daoManager);
            loader.setController(controller);
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Timetabler");
            primaryStage.show();
        } catch (Exception e) {
            Log.error(e);
            JavaFxBridge.close();
        }
    }

    /**
     * Called after the UI is closed. It closes the daoManager.
     */
    @Override
    public void stop() {
        daoManager.close();
    }

    /**
     * Initialises the logging mechanism, based on the given level, which can be 'v', 'd', 'i', 'w', 'e' or 'n'. Also,
     * the log files are created and bound the logger.
     *
     * @param level The log level.
     */
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
            File logFolder = new File("log/");
            logFolder.mkdirs();

            if (!logFolder.exists() || !logFolder.canWrite()) {
                Log.warning("Cannot create log files. Will not log to files!");
            } else {
                File outFile = new File(logFolder ,LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll(":", "-") + "_out.log");
                File errFile = new File(logFolder, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).replaceAll(":", "-") + "_err.log");
                outFile.createNewFile();
                errFile.createNewFile();

                MultipleWriter out = new MultipleWriter(new PrintWriter(System.out, true), new FileWriter(outFile, true));
                MultipleWriter err = new MultipleWriter(new PrintWriter(System.err, true), new FileWriter(errFile, true));

                Log.NORMAL_WRITER = new PrintWriter(out);
                Log.ERROR_WRITER = new PrintWriter(err);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.warning("Cannot create log files. Will not log to files!");
        }
    }
}
