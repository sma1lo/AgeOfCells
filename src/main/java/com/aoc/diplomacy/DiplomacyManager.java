package com.aoc.diplomacy;

import com.aoc.config.Config;
import com.aoc.nation.Nation;
import com.aoc.nation.SituationState;

import java.util.List;

public class DiplomacyManager {
    public static void makeVassal(Nation master, Nation vassal) {
        if (master == null || vassal == null || master == vassal) return;
        for (Nation subVassal : List.copyOf(vassal.getVassals())) {
            vassal.removeVassal(subVassal);
            master.addVassal(subVassal);
        }

        master.addVassal(vassal);
        vassal.setState(SituationState.PEACE);
    }

    public static void update(List<Nation> nations) {
        for (Nation nation : nations) {
            if (nation.isVassal()) {
                processTribute(nation);
                processLibertyDesire(nation);
            }
            processStabilityAndCrisis(nation);
        }
    }

    private static void processTribute(Nation vassal) {
        Nation master = vassal.getMaster();
        if (master == null) return;

        long tribute = (long) (vassal.getGold() * 0.10);
        if (tribute > 0 && vassal.spendGold(tribute)) {
            master.addGold(tribute);
        }
    }

    private static void processLibertyDesire(Nation vassal) {
        Nation master = vassal.getMaster();
        if (master == null) return;

        boolean powerGap = vassal.getPower() > master.getPower();
        boolean masterWeak = master.getGold() <= 0 || master.getStability() < 30;

        if (powerGap || masterWeak) {
            vassal.setLibertyDesire(vassal.getLibertyDesire() + 2);
        } else {
            vassal.setLibertyDesire(vassal.getLibertyDesire() - 1);
        }

        if (vassal.getLibertyDesire() >= 80 && Config.RAND.nextInt(100) < 15) {
            triggerIndependenceWar(vassal);
        }
    }

    private static void processStabilityAndCrisis(Nation nation) {
        if (nation.getGold() <= 0) {
            nation.setStability(nation.getStability() - 1);
        } else if (nation.getStability() < 100) {
            nation.setStability(nation.getStability() + 1);
        }
    }

    private static void triggerIndependenceWar(Nation vassal) {
        Nation master = vassal.getMaster();
        if (master == null) return;

        master.removeVassal(vassal);
        vassal.setLibertyDesire(0);
        vassal.setState(SituationState.WAR);
        master.setState(SituationState.WAR);
    }
}