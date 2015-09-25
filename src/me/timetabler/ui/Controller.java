package me.timetabler.ui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by stuart on 16/09/15.
 */
public class Controller implements Initializable {
    @FXML WebView view;
    private WebEngine engine;
    private JSObject bridge;
    private List<Subject> subjects;
    private List<Staff> staff;

    public Controller(List<Subject> subjects, List<Staff> staff) {
        this.subjects = subjects;
        this.staff = staff;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = view.getEngine();
        engine.load(String.valueOf(getClass().getResource("html/index.html")));
        engine.getLoadWorker().stateProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                bridge = (JSObject) engine.executeScript("window");
                bridge.setMember("bridge", new Bridge());
                bridge.setMember("subjects", subjects);
                bridge.setMember("staff", staff);
            }
        }));
    }
}
