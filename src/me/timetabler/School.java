package me.timetabler;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.map.Building;
import me.timetabler.map.SchoolMap;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stuart on 25/08/15.
 */
public class School {
    public HashMap<String, Building> buildings;
    public List<Subject> subjects;
    public List<Staff> staff;
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
                /*buildings.forEach((name, building) -> {

                });*/
            } else {
                //buildings.put(file.getName().replace(".csv", ""), new Building(new SchoolMap(file), file.getName().replace(".csv", "")));
            }
        }

        if (schoolMap == null) {
            throw new FileNotFoundException("Could not find school.csv!");
        }
        if (buildings.size() == 0) {
            throw new InvalidStateException("No buildings found!");
        }
        staff = Collections.synchronizedList(new ArrayList<>());
        subjects = Collections.synchronizedList(new ArrayList<>());
    }
}
