package com.aoc;

import com.aoc.config.Config;
import com.aoc.util.Screen;
import com.aoc.util.Time;

public class GameLoop {
    public static void run() throws InterruptedException {
        World.init();

        while (true) {
            Time.tick();
            World.update();

            Screen.clear();
            System.out.println("Game tick: " + Time.getCurrentTick() + " | Active Nations: " + World.getNations().size());
            World.generateGrid();

            Thread.sleep(Config.get().tickDelayMs());
        }
    }
}
