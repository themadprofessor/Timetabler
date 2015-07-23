package me.timetabler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import me.timetabler.add_dialogs.AddSubjectDialog;
import me.timetabler.add_dialogs.AddTeacherDialog;
import me.util.Log;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML ListView<Subject> subjects;
    @FXML ListView<Teacher> teachers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subjects = new ListView<>();
        teachers = new ListView<>();
    }

    @FXML
    public void addSubject() {
        AddSubjectDialog dialog = new AddSubjectDialog(teachers);
        dialog.setOnCloseRequest(event -> {
            if (dialog.subject != null) {
                Log.out("Name [" + dialog.subject.name + "]   ID [" + dialog.subject.id + ']');
                subjects.getItems().add(dialog.subject);
            }
        });
    }

    @FXML
    public void addTeacher() {
        AddTeacherDialog dialog = new AddTeacherDialog(subjects);
        dialog.setOnCloseRequest(event -> {
            if (dialog.teacher != null) {
                Log.out("Name [" + dialog.teacher.name + "]   ID [" + dialog.teacher.id + ']');
                teachers.getItems().add(dialog.teacher);
            }
        });
    }
}
