package me.timetabler;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.timetabler.map.SchoolMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stuart on 25/08/15.
 */
public class School {
    public Map<String, Subject> subjects;
    public Map<String, Staff> staff;
    private SchoolMap schoolMap;

    public School(Map<String, String> config) throws FileNotFoundException {
        File mapFolder = new File(config.get("other_maps"));
        schoolMap = new SchoolMap(new File(config.get("top_map")));
        if (mapFolder.isFile()) {
            throw new InvalidParameterException("Map folder is not a folder!");
        }
        File[] files = mapFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".csv") && pathname.getPath().endsWith(config.get("top_map")));
        HashMap<String, SchoolMap> buildings = new HashMap<>();
        for (File file : files) {
            buildings.put(file.getName().replace(".csv", ""), new SchoolMap(file));
        }

        if (schoolMap == null) {
            throw new FileNotFoundException("Could not find school.csv!");
        }
        if (buildings.size() == 0) {
            throw new IllegalStateException("Failed to find any buildings");
        }
        schoolMap.init(buildings);
        staff = Collections.synchronizedMap(new HashMap<>());
        subjects = Collections.synchronizedMap(new HashMap<>());
    }
}
