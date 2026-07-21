package com.aoc;

import java.util.Random;

public class GameLoop {
    static Random rand = new Random();

    public static void run() throws InterruptedException {
        World.init();
        while (true) {
            Time.tick();
            World.collectEconomy();

            if (Time.getCurrentTick() % 4 == 0) {
                for (Nation n : World.nations) {
                    n.strengthenFromGold();
                }
            }

            World.check();
            World.checkCapitals();

            Screen.clear();
            System.out.println("Game tick: " + Time.getCurrentTick() + " | Active Nations: " + World.nations.size());

            rotateState();
            World.generateGrid();

            Thread.sleep(300);
        }
    }

    public static void rotateState() {
        int tick = Time.getCurrentTick();
        if (tick % 35 == 0) {
            for (Nation nation : World.nations) {
                if (rand.nextInt(100) < 55) {
                    nation.setState(SituationState.WAR);
                } else {
                    nation.setState(SituationState.PEACE);
                }
            }
        }
    }
}