package me.timetabler.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by stuart on 06/12/15.
 */
public interface ConfigParser {
    Map<String, Map<String, String>> parse() throws IOException;
    static ConfigParser getParser(ConfigType type) {
        switch (type) {
            case YAML: return new YamlConfigParser("assets/config.yaml");
            default: return null;
        }
    }
}
