package com.aoc;

public class Screen {
    public static void clear() {
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }
}