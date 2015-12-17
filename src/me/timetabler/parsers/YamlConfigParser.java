package me.timetabler.parsers;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by stuart on 06/12/15.
 */
public class YamlConfigParser implements ConfigParser {
    private File loc;
    private Pattern mapRegex = Pattern.compile(".*:\\s*");
    private Pattern itemRegex = Pattern.compile("\\s{4}-.*");


    public YamlConfigParser(File loc) {
        this.loc = loc;
    }

    public YamlConfigParser(String loc) {
        this.loc = new File(loc);
    }

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
            } else if (itemRegex.matcher(line).matches()) {
                String[] keyValue = line.trim().split(":");
                map.get(currentMap).put(String.valueOf(keyValue[0].subSequence(2, keyValue[0].length())), keyValue[1].trim());
            }
        }

        return Collections.unmodifiableMap(map);
    }

    private void checkFile(File file) throws IOException {
        assert file != null;
        if (!file.exists()) {
            throw new FileNotFoundException("Config file does not exist! It should be there: [" + file.getPath() + ']');
        } else if (file.isDirectory()) {
            throw new IOException("The config file is a directory! It should be a file.");
        } else if (!file.canRead()) {
            throw new IOException("The config file does not have read permissions! Give the file the read permission.");
        }
    }
}
