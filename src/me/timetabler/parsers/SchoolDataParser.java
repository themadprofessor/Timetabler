package me.timetabler.parsers;

import me.timetabler.data.SchoolClass;
import me.timetabler.data.Staff;
import me.timetabler.data.Subject;

import java.io.IOException;
import java.util.Map;

/**
 * A way of parsing all the files which contain data about the school into Maps. The type of map is determined by the implementation.<br>
 * A static factory method is provided to get the correct implementation of SchoolDataParser for the data_type specified in the given config map.
 */
public interface SchoolDataParser {
    /**
     * Reads the staff data from the file specified in the config map. The type of map is determined by the implementation.
     * @return Returns a map containing all the staff defined in the staff file. The key is the name of the staff member which is also defined with the Staff object.
     * @throws IOException Thrown if any IOException occurs.
     */
    Map<Integer, Staff> readStaff() throws IOException;

    /**
     * Reads the subject data from the file specified in the config map. The type of map is determined by the implementation.
     * @return Returns a map containing all the subjects defined in the subject file. The key is the name of the subject which is also defined with the Subject object.
     * @throws IOException Thrown if any IOException occurs.
     */
    Map<Integer, Subject> readSubjects() throws IOException;

    /**
     * Reads the class data from the file specified in the config map. The type of map is determined by the implementation.
     * @return Returns a map containing all the classes defined in the class file. The key is the name of the class which is also defined with the SchoolClass object.
     * @throws IOException Thrown if any IOException occurs.
     */
    Map<Integer, SchoolClass> readClasses() throws IOException;

    /**
     * Writes the staff data from the given map to the staff file specified in the config map.
     * @param staffMap The map the be written.
     * @return Returns true if the data was written successfully, otherwise returns false.
     */
    boolean writeStaff(Map<Integer, Staff> staffMap);

    /**
     * Writes the subject data from the given map to the subject file specified in the config map.
     * @param subjectMap The map the be written.
     * @return Returns true if the data was written successfully, otherwise returns false.
     */
    boolean writeSubjects(Map<Integer, Subject> subjectMap);

    /**
     * Writes the class data from the given map to the class file specified in the config map.
     * @param classMap The map the be written.
     * @return Returns true if the data was written successfully, otherwise returns false.
     */
    boolean writeClasses(Map<Integer, SchoolClass> classMap);

    /**
     * A factory method to get the appropriate SchoolDataParser implementation for the file type specified in the given config map, and gives the implementation the given config map.
     * @param config The config map the be used to get the correct implementation and to be given to said implementation.
     * @return Returns the implementation, or null if the specified data type does not have an implementation.
     */
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
