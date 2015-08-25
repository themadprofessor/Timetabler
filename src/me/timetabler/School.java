package me.timetabler;

import me.timetabler.Map.Building;
import me.timetabler.Map.SchoolMap;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * Created by stuart on 25/08/15.
 */
public class School {
    private HashMap<String, Building> buildings;
    private SchoolMap schoolMap;

    public School(File mapFolder) throws FileNotFoundException {
        if (mapFolder.isFile()) {
            throw new InvalidParameterException("Map folder is not a folder!");
        }
        File[] files = mapFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".csv"));
        buildings = new HashMap<>();
        for (File file : files) {
            if (file.getName().equals("school.csv")) {
                schoolMap = new SchoolMap(file);
            } else {
                buildings.put(file.getName().replace(".csv", ""), new Building(new SchoolMap(file)));
            }
        }

        if (schoolMap == null) {
            throw new FileNotFoundException("Could not find school.csv!");
        }
        if (buildings.size() == 0) {
            throw new InvalidStateException("No buildings found!");
        }
    }
}
