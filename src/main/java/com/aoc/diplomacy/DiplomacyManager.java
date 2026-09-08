package com.aoc.diplomacy;

import com.aoc.cell.Cell;
import com.aoc.cell.CellType;
import com.aoc.nation.Nation;
import com.aoc.nation.SituationState;
import com.aoc.util.Element;
import com.aoc.util.Rng;

import java.util.List;

public final class DiplomacyManager {
    private DiplomacyManager() {
        throw new UnsupportedOperationException("Utility class");
    }

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
        for (Nation nation : List.copyOf(nations)) {
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

    public static void rotateState(int tick, List<Nation> nations) {
        if (tick % 35 != 0) return;

        for (Nation nation : nations) {
            if (nation.isVassal() || nation.getState() == SituationState.UNION) continue;

            int chance = Rng.nextInt(100);
            if (chance < 55) {
                nation.setState(SituationState.WAR);
            } else if (chance < 80 && nations.size() > 1) {
                Nation partner = Element.getRandomElement(nations);
                if (partner != null && partner != nation && !partner.isVassal()) {
                    nation.setState(SituationState.UNION);
                    partner.setState(SituationState.UNION);
                }
            } else {
                nation.setState(SituationState.PEACE);
            }
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

        if (vassal.getLibertyDesire() >= 80 && Rng.nextInt(100) < 15) {
            triggerIndependenceWar(vassal);
        }

    }

    private static void processStabilityAndCrisis(Nation nation) {
        if (nation.getGold() <= 0) {
            nation.setStability(nation.getStability() - 1);
        } else if (nation.getStability() < 100) {
            nation.setStability(nation.getStability() + 1);
        }
        int castleBonus = 0;
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.getType() == CellType.CASTLE) {
                castleBonus++;
            }
        }

        if (castleBonus > 0 && nation.getStability() < 90 && Rng.nextInt(10) == 0) {
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
