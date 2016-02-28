package me.timetabler.parsers;

import me.timetabler.data.SchoolClass;
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
 * A SchoolDataParser implementation for CSV data.
 */
public class CsvDataParser implements SchoolDataParser {
    /**
     * The config map for the school data.
     */
    private Map<String, String> config;

    /**
     * Initialises the data parser.
     * @param config The config map which contains the config for the school data.
     */
    public CsvDataParser(Map<String, String> config) {
        this.config = config;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public boolean writeStaff(Map<Object, Object> map) {
        StringBuilder builder = new StringBuilder();
        String[] names = new String[map.keySet().size()];
        map.keySet().toArray(names);
        if (names.length == 0) {
            return false;
        }
        for (int i = 0; i < map.size()-1; i++) {
            Object staff = map.get(names[i]);
            builder.append(staff.id).append(',').append(staff.name).append('\n');
        }
        Object staff = map.get(names[names.length-1]);
        builder.append(staff.id).append(',').append(staff.name);
        return write(String.valueOf(config.get("staff_location")), builder.toString());
    }

    /**
     * {@inheritDoc}
     */
    public boolean writeClasses(Map<String, SchoolClass> classMap) {
        StringBuilder builder = new StringBuilder();
        String[] ids = new String[classMap.keySet().size()];
        classMap.keySet().toArray(ids);
        if (ids.length == 0) {
            return false;
        }
        for (int i = 0; i< classMap.size()-1; i++) {
            SchoolClass clazz = classMap.get(ids[i]);
            builder.append(clazz.name).append(',').append(clazz.subjectId).append('\n');
        }
        SchoolClass clazz = classMap.get(ids[ids.length-1]);
        builder.append(clazz.name).append(',').append(clazz.subjectId);
        return write(String.valueOf(config.get("classes_location")), builder.toString());
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
            Log.error(e);
            result = false;
        } finally {
            assert out != null;
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.error(e);
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Object, Object> readStaff() throws IOException {
        File file = new File(String.valueOf(config.get("staff_location")));
        Map<K, V> staff = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return staff;
        }
            Files.lines(file.toPath()).forEach(line -> {
                Staff st = new Staff();
                int[] split = line.split(",");
                st.id = split[0];
                st.name = split[1];
                staff.put(st.id, st);
            });

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Subject> readSubjects() throws IOException {
        File file = new File(String.valueOf(config.get("subjects_location")));
        Map<String, Subject> subjects = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return subjects;
        }
            Files.lines(file.toPath()).forEach(line -> {
                Subject subject = new Subject();
                String[] split = line.split(",");
                subject.id = split[0];
                subject.name = split[1];
                subjects.put(subject.id, subject);
            });
        return subjects;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, SchoolClass> readClasses() throws IOException {
        File file = new File(String.valueOf(config.get("classes_location")));
        Map<String, SchoolClass> classes = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return classes;
        }
            Files.lines(file.toPath()).forEach(line -> {
                SchoolClass clazz = new SchoolClass();
                String[] split = line.split(",");
                clazz.name = split[0];
                clazz.subjectId = split[1];
                classes.put(clazz.name, clazz);
            });
        return classes;
    }
}
