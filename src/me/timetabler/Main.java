package me.timetabler;

import me.timetabler.ui.Window;
import me.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by stuart on 24/08/15.
 */
public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        School school = null;
        try {
            school = new School(new File("assets"));
            Log.out("Loaded School Map");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Window.open(school.subjects, school.staff);
    }
}
