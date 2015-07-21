package me.timetabler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import me.util.Log;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML ListView<String> subjects;
    @FXML ListView<String> teachers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void addSubject() {
        AddSubjectDialog dialog = new AddSubjectDialog();
        dialog.setOnCloseRequest(event -> {
            if (dialog.subject != null) {
                Log.out("Name [" + dialog.subject.name + "]   ID [" + dialog.subject.id + ']');
                subjects.getItems().add(dialog.subject.name);
            }
        });
    }

    @FXML
    public void addTeacher() {
        AddTeacherDialog dialog = new AddTeacherDialog();
        dialog.setOnCloseRequest(event -> {
            if (dialog.teacher != null) {
                Log.out("Name [" + dialog.teacher.name + "]   ID [" + dialog.teacher.id + ']');
                teachers.getItems().add(dialog.teacher.name);
            }
        });
    }
}
