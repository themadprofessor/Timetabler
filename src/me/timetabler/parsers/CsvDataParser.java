package me.timetabler.parsers;

import me.timetabler.data.Staff;
import me.timetabler.data.Subject;
import me.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by stuart on 06/12/15.
 */
public class CsvDataParser implements SchoolDataParser {
    private Map<String, String> config;

    public CsvDataParser(Map<String, String> config) {
        this.config = config;
    }

    /**
     * Writes the given map of subjects to the given CSV file.
     * @param map The map containing the subjects
     * @return Returns false if the map failed to write or the map is empty. Returns true otherwise
     */
    public boolean writeSubjects(Map<String, Subject> map) {
        StringBuilder builder = new StringBuilder();
        String[] names = new String[map.keySet().size()];
        map.keySet().toArray(names);
        if (names.length == 0) {
            return false;
        }
        for (int i = 0; i < map.size()-1; i++) {
            Subject subject = map.get(names[i]);
            builder.append(subject.id).append(',').append(subject.name).append('\n');
        }
        Subject subject = map.get(names[names.length-1]);
        builder.append(subject.id).append(',').append(subject.name);
        return write(String.valueOf(config.get("subjects_location")), builder.toString());
    }

    /**
     * Writes the given map of staff to the given CSV file.
     * @param map The map containing the staff
     * @return Returns false if the map failed to write or the map is empty. Returns true otherwise
     */
    public boolean writeStaff(Map<String, Staff> map) {
        StringBuilder builder = new StringBuilder();
        String[] names = new String[map.keySet().size()];
        map.keySet().toArray(names);
        if (names.length == 0) {
            return false;
        }
        for (int i = 0; i < map.size()-1; i++) {
            Staff staff = map.get(names[i]);
            builder.append(staff.id).append(',').append(staff.name).append('\n');
        }
        Staff staff = map.get(names[names.length-1]);
        builder.append(staff.id).append(',').append(staff.name);
        return write(String.valueOf(config.get("staff_location")), builder.toString());
    }

    /**
     * Writes the given string to the given file
     * @param file The file to write to
     * @param string The string to write
     * @return Returns true if string wrote successfully. Returns false otherwise
     */
    private boolean write(String file, String string) {
        boolean result = true;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(string.getBytes());
        } catch (IOException e) {
            Log.err(e);
            result = false;
        } finally {
            assert out != null;
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.err(e);
            }
        }
        return result;
    }

    /**
     * Reads the staff data from the given file into a synchronised LinkedHashMap
     * @return A map containing the parsed staff data
     */
    public Map<String, Staff> readStaff() {
        File file = new File(String.valueOf(config.get("staff_location")));
        Map<String, Staff> staff = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return staff;
        }
        try {
            Files.lines(file.toPath()).forEach(line -> {
                Staff st = new Staff();
                String[] split = line.split(",");
                st.id = split[0];
                st.name = split[1];
                staff.put(st.id, st);
            });
        } catch (IOException e) {
            Log.err(e);
        }

        return staff;
    }

    /**
     * Reads the staff data from the given file into a synchronised LinkedHashMap
     * @return Returns the map containing the subject data
     */
    public Map<String, Subject> readSubjects() {
        File file = new File(String.valueOf(config.get("subjects_location")));
        Map<String, Subject> subjects = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return subjects;
        }
        try {
            Files.lines(file.toPath()).forEach(line -> {
                Subject subject = new Subject();
                String[] split = line.split(",");
                subject.id = split[0];
                subject.name = split[1];
                subjects.put(subject.id, subject);
            });
        } catch (IOException e) {
            Log.err(e);
        }
        return subjects;
    }
}
