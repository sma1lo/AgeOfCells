package com.aoc.util;

import com.aoc.config.Config;

import java.util.List;

public class Element {
    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(Config.RAND.nextInt(list.size()));
    }
}