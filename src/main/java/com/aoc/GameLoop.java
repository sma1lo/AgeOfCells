package com.aoc;

import java.util.Random;

public class GameLoop {
    private static final Random rand = new Random();

    public static void run() throws InterruptedException {
        World world = new World();
        world.init();

        while (true) {
            Time.tick();
            world.update();

            Screen.clear();
            System.out.println("Game tick: " + Time.getCurrentTick() + " | Active Nations: " + world.getNations().size());
            world.generateGrid();

            Thread.sleep(300);
        }
    }
}