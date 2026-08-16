package com.aoc.util;

import java.util.concurrent.ThreadLocalRandom;

public final class Rng {
    private Rng() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static int nextInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
