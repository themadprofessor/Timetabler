package me.timetabler;

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
        try {
            School school = new School(new File("assets"));
            Log.out("Loaded School Map");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
