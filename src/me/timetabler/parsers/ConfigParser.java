package me.timetabler.parsers;

import java.io.IOException;
import java.util.Map;

/**
 * A way of parsing a config file into a Map<String, Map<String, String>>. The type of Map is determined by the implementation.<br>
 * A static factory method is provided to get the correct type of parser for the file type given.
 */
public interface ConfigParser {
    /**
     * Parses a config file into a Map<String, Map<String, String>>. The type of map is determined by the implementation.
     * @return The parsed config Map
     * @throws IOException Thrown if any IOExceptions occur. The implementation can handle the IOExceptions if they do not require the caller to be notified.
     */
    Map<String, Map<String, String>> parse() throws IOException;

    /**
     * A factory method to get the correct type of ConfigParser for a given file type.
     * @param type The type of ConfigParser
     * @param file The path to the config file
     * @return The corresponding ConfigParser implementation, or null if the data type is yet to be implemented.
     */
    static ConfigParser getParser(ConfigType type, String file) {
        switch (type) {
            case YAML: return new YamlConfigParser(file);
            default: return null;
        }
    }
}
