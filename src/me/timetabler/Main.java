package me.timetabler;

import me.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stuart on 24/08/15.
 */
public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        File mapFile = new File("assets/map.csv");
        ArrayList<ArrayList<CellType>> map = new ArrayList<>();
        Pattern stairRegex = Pattern.compile("-[0-9]+");
        try {
            Files.lines(mapFile.toPath()).forEachOrdered(line -> {
                String[] rowStrings = line.split(",");
                ArrayList<CellType> row = new ArrayList<>();
                for (String rowString : rowStrings) {
                    if ("".equals(rowString)) {
                        row.add(new Wall());
                    } else if ("0".equals(rowString)) {
                        row.add(new Path());
                    } else if (stairRegex.matcher(rowString).matches()) {
                        row.add(new StairCase(Integer.valueOf(rowString) * -1));
                    } else {
                        row.add(new ClassRoom(rowString));
                    }
                }
                map.add(row);
            });
        } catch (IOException e) {
            Log.err(e);
        }
        Log.out("done");
    }
}
