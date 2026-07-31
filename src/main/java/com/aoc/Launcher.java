package com.aoc;

import com.aoc.config.Config;
import org.fusesource.jansi.AnsiConsole;

public class Launcher {
    public static void main(String[] args) throws InterruptedException {
        AnsiConsole.systemInstall();
        Config.load("config.yaml");
        GameLoop.run();
    }
}
