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
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by stuart on 16/09/15.
 */
public class Controller implements Initializable {
    @FXML WebView view;
    private WebEngine engine;
    private Map<String, Subject> subjects;
    private Map<String, Staff> staff;

    public Controller(Map<String, Subject> subjects, Map<String, Staff> staff) {
        this.subjects = subjects;
        this.staff = staff;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engine = view.getEngine();
        engine.load(String.valueOf(getClass().getResource("html/index.html")));
        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject bridge = (JSObject) engine.executeScript("window");
                bridge.setMember("java", new Bridge(subjects, staff));
                subjects.forEach((id, subject) -> bridge.call("displaySubject", id, subject.name));
                staff.forEach((id, staff) -> bridge.call("displayStaff", id, staff.name));
                engine.executeScript("console.log = function(msg) {java.out(msg);}");
                engine.executeScript("console.error = function(msg) {java.err(msg);}");
            }
        });
    }
}
