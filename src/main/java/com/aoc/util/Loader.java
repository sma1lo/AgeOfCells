package com.aoc.util;

import org.yaml.snakeyaml.Yaml;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Loader {
    public static Map<String, Object> load(String path) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Loader.class.getClassLoader().getResourceAsStream(path)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + path);
            }
            return yaml.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Error: " + path, e);
        }
    }
}
