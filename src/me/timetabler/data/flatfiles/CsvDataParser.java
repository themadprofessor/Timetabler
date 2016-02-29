package me.timetabler.data.flatfiles;

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
import java.util.Set;

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
    public boolean writeSubjects(Map<Integer, Subject> map) {
        StringBuilder builder = new StringBuilder();
        Set<Map.Entry<Integer, Subject>> entries = map.entrySet();

        entries.forEach(entry -> {
            Subject subject = entry.getValue();
            builder.append(subject.id).append(',').append(subject.name).append('\n');
        });

        builder.deleteCharAt(builder.length()-1);
        return write(String.valueOf(config.get("subjects_location")), builder.toString());
    }

    /**
     * {@inheritDoc}
     */
    public boolean writeStaff(Map<Integer, Staff> map) {
        StringBuilder builder = new StringBuilder();
        Set<Map.Entry<Integer, Staff>> entries = map.entrySet();

        entries.forEach(entry -> {
            Staff staff = entry.getValue();
            builder.append(staff.id).append(',').append(staff.name).append('\n');
        });

        builder.deleteCharAt(builder.length()-1);
        return write(config.get("staff_location"), builder.toString());
    }

    /**
     * {@inheritDoc}
     */
    public boolean writeClasses(Map<Integer, SchoolClass> map) {
        StringBuilder builder = new StringBuilder();
        Set<Map.Entry<Integer, SchoolClass>> entries = map.entrySet();

        entries.forEach(entry -> {
            SchoolClass clazz = entry.getValue();
            builder.append(clazz.id).append(',').append(clazz.name).append(',').append(clazz.subjectId).append('\n');
        });

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
    public Map<Integer, Staff> readStaff() throws IOException {
        File file = new File(String.valueOf(config.get("staff_location")));
        Map<Integer, Staff> staff = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return staff;
        }
            Files.lines(file.toPath()).forEach(line -> {
                Staff st = new Staff();
                String[] split = line.split(",");
                st.id = Integer.parseInt(split[0]);
                st.name = split[1];
                staff.put(st.id, st);
            });

        return staff;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Integer, Subject> readSubjects() throws IOException {
        File file = new File(String.valueOf(config.get("subjects_location")));
        Map<Integer, Subject> subjects = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return subjects;
        }
            Files.lines(file.toPath()).forEach(line -> {
                Subject subject = new Subject();
                String[] split = line.split(",");
                subject.id = Integer.parseInt(split[0]);
                subject.name = split[1];
                subjects.put(subject.id, subject);
            });
        return subjects;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Integer, SchoolClass> readClasses() throws IOException {
        File file = new File(String.valueOf(config.get("classes_location")));
        Map<Integer, SchoolClass> classes = Collections.synchronizedMap(new LinkedHashMap<>());
        if (!file.exists()) {
            return classes;
        }
            Files.lines(file.toPath()).forEach(line -> {
                SchoolClass clazz = new SchoolClass();
                String[] split = line.split(",");
                clazz.id = Integer.parseInt(split[0]);
                clazz.name = split[1];
                clazz.subjectId = Integer.parseInt(split[2]);
                classes.put(clazz.id, clazz);
            });
        return classes;
    }
}
