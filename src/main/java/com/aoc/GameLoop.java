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
            Screen.clear();
            System.out.println("Game tick: " + Time.getCurrentTick());
            rotateState();
            World.generateGrid();
            Thread.sleep(500);
        }
    }

    public static void rotateState() {
        int currentTick = Time.getCurrentTick();

        if (currentTick % 30 == 0) {
            int chance = rand.nextInt(100);

            if (chance < 50) {
                for (Nation nation : World.nations) {
                    nation.state = SituationState.WAR;
                }
            } else {
                for (Nation nation : World.nations) {
                    nation.state = SituationState.PEACE;
                }
            }
        }
    }
}