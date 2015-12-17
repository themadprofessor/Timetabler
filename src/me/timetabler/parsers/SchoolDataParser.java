package me.timetabler.parsers;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;

import java.util.Map;

/**
 * Created by stuart on 06/12/15.
 */
public interface SchoolDataParser {
    Map<String, Staff> readStaff();
    Map<String, Subject> readSubjects();
    Map<String, SchoolClass> readClasses();
    boolean writeStaff(Map<String, Staff> staffMap);
    boolean writeSubjects(Map<String, Subject> subjectMap);
    boolean writeClasses(Map<String, SchoolClass> classMap);

    static SchoolDataParser getParser(Map<String, String> config) {
        String type = config.get("data_type");
        switch (type) {
            case "CSV":
                return new CsvDataParser(config);
            default:
                return null;
        }
    }
}
