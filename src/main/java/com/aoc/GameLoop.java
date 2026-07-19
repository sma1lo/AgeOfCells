package com.aoc;

public class GameLoop {
    public static void run() throws InterruptedException {
        World.init();
        for (int i = 0; i < 10; i++) {
            Time.tick();
            Screen.clear();
            System.out.println("Game tick: " + Time.getCurrentTick());
            World.generateGrid();

            Thread.sleep(500);
        }
    }
}
