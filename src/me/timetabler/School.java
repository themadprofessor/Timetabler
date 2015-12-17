package me.timetabler;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.map.SchoolMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A wrapper to all data related to the school.
 */
public class School {
    public Map<String, Subject> subjects;
    public Map<String, Staff> staff;
    public Map<String, SchoolClass> classes;

    public School(Map<String, String> config) throws FileNotFoundException {
        File mapFolder = new File(config.get("other_maps"));
        SchoolMap schoolMap = new SchoolMap(new File(config.get("top_map")));
        if (mapFolder.isFile()) {
            throw new InvalidParameterException("Map folder is not a folder!");
        }
        File[] files = mapFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".csv") && !pathname.getPath().endsWith(config.get("top_map")));
        HashMap<String, SchoolMap> buildings = new HashMap<>();
        for (File file : files) {
            buildings.put(file.getName().replace(".csv", ""), new SchoolMap(file));
        }

        if (buildings.size() == 0) {
            throw new IllegalStateException("Failed to find any buildings");
        }
        schoolMap.init(buildings);
        staff = Collections.synchronizedMap(new LinkedHashMap<>());
        subjects = Collections.synchronizedMap(new LinkedHashMap<>());
        classes = Collections.synchronizedMap(new LinkedHashMap<>());
    }
}
