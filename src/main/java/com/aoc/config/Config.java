package com.aoc.config;

import com.aoc.util.Loader;
import java.util.Map;

public record Config(
    int width,
    int height,
    int smooth,
    int tickDelayMs,
    int nations
) {
    private static Config instance;

    public static void load(String path) {
        Map<String, Object> rawData = Loader.load(path);
        instance = new Config(
            readInt(rawData, "width"),
            readInt(rawData, "height"),
            readInt(rawData, "smooth"),
            readInt(rawData, "tickDelayMs"),
            readInt(rawData, "nations")
        );

    }

    public static Config get() {
        if (instance == null) {
            throw new IllegalStateException("Config has not been loaded.");
        }
        return instance;
    }

    private static int readInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required configuration key: '" + key + "'");
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new IllegalArgumentException("Configuration key '" + key + "' must be a number, but got: " + value.getClass().getSimpleName());
    }
}
