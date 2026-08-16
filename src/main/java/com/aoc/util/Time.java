package com.aoc.util;

public final class Time {
    private int currentTick = 0;

    public void tick() {
        this.currentTick++;
    }

    public int getCurrentTick() {
        return this.currentTick;
    }

}
