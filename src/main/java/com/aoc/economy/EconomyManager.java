package com.aoc.economy;

import com.aoc.cell.Cell;
import com.aoc.nation.Nation;

import java.util.List;

public class EconomyManager {
    public static void collectEconomy(List<Nation> nations) {
        for (Nation nation : nations) {
            long income = 0;

            for (Cell cell : nation.getOwnedCells()) {
                income += switch (cell.getType()) {
                    case CAPITAL -> 4;
                    case LAND, SHIP -> 1;
                    case CASTLE, VILLAGE -> 2;
                    case TOWN -> 3;
                    default -> 0;
                };

                income += switch (cell.getTerrain()) {
                    case GOLD -> 8;
                    case IRON -> 5;
                    case COAL -> 3;
                    default -> 0;
                };
            }

            if (income > 0) {
                nation.addGold(income);
            }
        }
    }
}
