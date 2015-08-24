package me.timetabler;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by stuart on 22/07/15.
 */
public class Controller implements Initializable {
    @FXML WebView view;
    private WebEngine engine;
    public static ArrayList<Teacher> teachers = new ArrayList<>();
    public static ArrayList<Subject> subjects = new ArrayList<>();
    private JSObject bridge;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = view.getEngine();
        engine.getLoadWorker().stateProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                bridge = (JSObject) engine.executeScript("window");
                bridge.setMember("java", new JSBridge());
                bridge.call("showTeachers", teachers);
            }
        }));
        engine.load(getClass().getResource("html/index.html").toString());
    }
}
