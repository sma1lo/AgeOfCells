package com.aoc.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public final class Loader {
    private Loader() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Map<String, Object> load(String path) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Loader.class.getClassLoader().getResourceAsStream(path)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Resource file not found: " + path);
            }
            return yaml.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid YAML format in file: " + path, e);
        }

    }

}
