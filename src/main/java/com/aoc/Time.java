package com.aoc;

public class Time {
    static int currentTick = 0;

    public static void tick() {
        currentTick++;
    }

    public static int getCurrentTick() {
        return currentTick;
    }
}
