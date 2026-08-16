package com.aoc.util;

import java.util.List;

public final class Element {
    private Element() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(Rng.nextInt(list.size()));
    }
}
