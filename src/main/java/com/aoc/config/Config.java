package com.aoc.config;

import java.util.Random;

public class Config {
    public static final int WIDTH = 205;
    public static final int HEIGHT = 53;

    public static final boolean USE_RANDOM_SEED = true;
    public static final long SEED = 42L;
    public static final Random RAND = USE_RANDOM_SEED ? new Random() : new Random(SEED);

    public static final int SMOOTH = 4;

    public static final int TICK_DELAY_MS = 300;
}