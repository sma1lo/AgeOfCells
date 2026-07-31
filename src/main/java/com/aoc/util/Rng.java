package com.aoc.util;

import java.util.Random;

public class Rng {
    public static final Random RAND = new Random();

    public static int nextInt(int bound) {
        return RAND.nextInt(bound);
    }

    public static boolean nextBoolean() {
        return RAND.nextBoolean();
    }
}
