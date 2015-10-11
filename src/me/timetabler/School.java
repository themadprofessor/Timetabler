package me.timetabler;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.map.SchoolMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * Created by stuart on 25/08/15.
 */
public class School {
    public Map<String, Subject> subjects;
    public Map<String, Staff> staff;
    private SchoolMap schoolMap;

    public School(File mapFolder) throws FileNotFoundException {
        if (mapFolder.isFile()) {
            throw new InvalidParameterException("Map folder is not a folder!");
        }
        File[] files = mapFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".csv"));
        HashMap<String, SchoolMap> buildings = new HashMap<>();
        for (File file : files) {
            if (file.getName().equals("school.csv")) {
                schoolMap = new SchoolMap(file);
            } else {
                buildings.put(file.getName().replace(".csv", ""), new SchoolMap(file));
            }
        }

        if (schoolMap == null) {
            throw new FileNotFoundException("Could not find school.csv!");
        }
        if (buildings.size() == 0) {
            throw new Exception("Failed to find any buildings");
        }
        staff = Collections.synchronizedMap(new HashMap<>());
        subjects = Collections.synchronizedMap(new HashMap<>());
    }
}
