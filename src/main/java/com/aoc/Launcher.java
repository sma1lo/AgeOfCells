package com.aoc;

import org.fusesource.jansi.AnsiConsole;

public class Launcher {
    public static void main(String[] args) throws InterruptedException {
        AnsiConsole.systemInstall();
        GameLoop.run();
    }
}