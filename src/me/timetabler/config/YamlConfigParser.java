package me.timetabler.config;

import me.util.Log;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A ConfigParser implementation for a YAML config file.
 */
public class YamlConfigParser implements ConfigParser {
    private File loc;
    private Pattern mapRegex = Pattern.compile(".*:\\s*");
    private Pattern itemRegex = Pattern.compile("\\s{4}-.*");

    /**
     * Initialises the config parser
     * @param loc The config file
     */
    public YamlConfigParser(File loc) {
        this.loc = loc;
        Log.debug("Using YAML Config Parser");
    }

    /**
     * Initialises the config parser
     * @param loc The path to the config file
     */
    public YamlConfigParser(String loc) {
        this.loc = new File(loc);
        Log.debug("Using YAML Config Parser");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Map<String, String>> parse() throws IOException {
        checkFile(loc);
        Map<String, Map<String, String>> map = new LinkedHashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(loc))));
        String line;
        String currentMap = "";
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("#.*", "");
            if (mapRegex.matcher(line).matches()) {
                currentMap = line.split(":")[0].trim();
                map.put(currentMap, new LinkedHashMap<>());
                Log.verbose("Added New Config Map [" + currentMap + ']');
            } else if (itemRegex.matcher(line).matches()) {
                String[] keyValue = line.trim().split(":");
                String key = String.valueOf(keyValue[0].subSequence(2, keyValue[0].length()));
                String value = keyValue[1].trim();
                map.get(currentMap).put(key, value);
                Log.verbose("Added New Key/Value Pair [" + key + '/' + value + "] To [" + currentMap + "]");
            }
        }

        return Collections.unmodifiableMap(map);
    }

    /**
     * Checks if the file exists, is a file and can be read. If any fails, it will throw the corresponding IOException
     * @param file The file to be checked
     * @throws IOException Thrown if the file does not exist, is not a file or cannot be read
     */
    private void checkFile(File file) throws IOException {
        assert file != null;
        if (!file.exists()) {
            throw new FileNotFoundException("Config file does not exist! It should be there: [" + file.getPath() + ']');
        } else if (!file.isFile()) {
            throw new IOException("The config file is not a file!");
        } else if (!file.canRead()) {
            throw new IOException("The config file does not have read permissions! Give the file the read permission.");
        }
    }
}
