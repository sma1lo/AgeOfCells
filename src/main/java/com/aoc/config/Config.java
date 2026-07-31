package com.aoc.config;

import com.aoc.util.Loader;
import java.util.Map;

public record Config(
    int width,
    int height,
    int smooth,
    int tickDelayMs
) {
    private static Config instance;
    public static void load(String path) {
        Map<String, Object> rawData = Loader.load(path);
        instance = new Config(
            (int) rawData.get("width"),
            (int) rawData.get("height"),
            (int) rawData.get("smooth"),
            (int) rawData.get("tickDelayMs")
        );
    }

    public static Config get() {
        return instance;
    }
}
