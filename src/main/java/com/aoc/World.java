package com.aoc;

import com.aoc.cell.*;
import com.aoc.config.Config;
import com.aoc.diplomacy.DiplomacyManager;
import com.aoc.map.MapGenerator;
import com.aoc.nation.Nation;
import com.aoc.nation.NationType;
import com.aoc.nation.SituationState;
import com.aoc.render.WorldRenderer;
import com.aoc.util.Element;
import com.aoc.util.Rng;
import com.aoc.util.Time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World {

    private static final Cell[][] cells = new Cell[Config.get().height()][Config.get().width()];
    private static final List<Nation> nations = new ArrayList<>();

    private static final MapGenerator generator = new MapGenerator();
    private static final WorldRenderer renderer = new WorldRenderer(Config.get().width(), Config.get().width());

    public static void init() {
        generator.generateTerrain(cells, Config.get().width(), Config.get().height());
        fillNations();
        generator.spawnCapitals(cells, nations, Config.get().width(), Config.get().height());
    }

    public static void generateGrid() {
        renderer.render(cells, Config.get().width(), Config.get().height());
    }

    private static void fillNations() {
        nations.clear();
        nations.add(new Nation("Rome", NationType.ROME, Rng.nextInt(100) + 50));
        nations.add(new Nation("Bavaria", NationType.BAVARIA, Rng.nextInt(100) + 50));
        nations.add(new Nation("England", NationType.ENGLAND, Rng.nextInt(100) + 50));
        nations.add(new Nation("Russia", NationType.RUSSIA, Rng.nextInt(100) + 50));
        nations.add(new Nation("Austria", NationType.AUSTRIA, Rng.nextInt(100) + 50));
        nations.add(new Nation("Scotland", NationType.SCOTLAND, Rng.nextInt(100) + 50));
        nations.add(new Nation("France", NationType.FRANCE, Rng.nextInt(100) + 50));
        nations.add(new Nation("Prussia", NationType.PRUSSIA, Rng.nextInt(100) + 50));
        nations.add(new Nation("Ottomans", NationType.OTTOMANS, Rng.nextInt(100) + 50));
        nations.add(new Nation("Spain", NationType.SPAIN, Rng.nextInt(100) + 50));
        nations.add(new Nation("Sweden", NationType.SWEDEN, Rng.nextInt(100) + 50));
        nations.add(new Nation("Poland", NationType.POLAND, Rng.nextInt(100) + 50));
        nations.add(new Nation("Venice", NationType.VENICE, Rng.nextInt(100) + 50));
        nations.add(new Nation("Portugal", NationType.PORTUGAL, Rng.nextInt(100) + 50));
        nations.add(new Nation("Denmark", NationType.DENMARK, Rng.nextInt(100) + 50));
        nations.add(new Nation("Netherlands", NationType.NETHERLANDS, Rng.nextInt(100) + 50));
        nations.add(new Nation("Persia", NationType.PERSIA, Rng.nextInt(100) + 50));
    }

    public static void update() {
        collectEconomy();
        if (Time.getCurrentTick() % 4 == 0) {
            for (Nation nation : nations) {
                nation.strengthenFromGold();
            }
        }
        DiplomacyManager.update(nations);
        rotateState();
        check();
        checkCapitals();

        for (Nation nation : nations) {
            int ships = countShips(nation);
            if (ships > 7) {
                int toRemove = ships - 7;
                for (Cell cell : new ArrayList<>(nation.getOwnedCells())) {
                    if (toRemove <= 0) break;
                    if (cell.isShip()) {
                        cell.setType(CellType.LAND);
                        toRemove--;
                    }
                }
            }
        }
    }

    private static int countShips(Nation nation) {
        int count = 0;
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.isShip()) count++;
        }
        return count;
    }

    public static void collectEconomy() {
        for (Nation nation : nations) {
            for (Cell cell : nation.getOwnedCells()) {
                long income = 0;

                income += switch (cell.getType()) {
                    case CAPITAL -> 4;
                    case LAND -> 1;
                    case SHIP -> 1;
                    default -> 0;
                };

                income += switch (cell.getTerrain()) {
                    case GOLD -> 8;
                    case IRON -> 5;
                    case COAL -> 3;
                    default -> 0;
                };

                if (income > 0) {
                    nation.addGold(income);
                }
            }
        }
    }

    public static void check() {
        for (int y = 0; y < Config.get().height(); y++) {
            for (int x = 0; x < Config.get().width(); x++) {
                Cell cell = cells[y][x];
                Nation owner = cell.getOwner();

                if (owner != null && (cell.isCapital() || cell.isLand())) {
                    if (Rng.nextInt(100) < 15) {
                        tryExpand(x, y, owner);
                    }
                }
                if (owner != null && cell.isShip()) {
                    if (Rng.nextInt(100) < 20) {
                        trySail(x, y, owner);
                    }
                }
            }
        }
    }

    private static boolean hasLand(Nation nation) {
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.isLand() || cell.isCapital()) {
                return true;
            }
        }
        return false;
    }

    public static void checkCapitals() {
        Iterator<Nation> iterator = nations.iterator();
        while (iterator.hasNext()) {
            Nation nation = iterator.next();

            boolean noLand = !hasLand(nation);
            boolean noCapitalAndNotVassal = !hasCapital(nation) && !nation.isVassal();

            if (noLand || noCapitalAndNotVassal) {
                eliminateNation(nation);
                iterator.remove();
            }
        }
    }

    private static boolean hasCapital(Nation nation) {
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.isCapital()) {
                return true;
            }
        }
        return false;
    }

    private static void eliminateNation(Nation nation) {
        if (nation.isVassal()) {
            nation.getMaster().removeVassal(nation);
        }
        for (Nation v : new ArrayList<>(nation.getVassals())) {
            nation.removeVassal(v);
        }

        List<Cell> cellsToClear = new ArrayList<>(nation.getOwnedCells());
        for (Cell cell : cellsToClear) {
            cell.setType(CellType.NONE);
            claimCell(cell, null);
        }
    }

    private static void tryExpand(int x, int y, Nation attacker) {
        if (attacker.getPower() < 10) return;

        int nx = x + (Rng.nextInt(3) - 1);
        int ny = y + (Rng.nextInt(3) - 1);
        if (nx < 0 || nx >= Config.get().width() || ny < 0 || ny >= Config.get().height()) return;

        Cell target = cells[ny][nx];
        long cost = 12;
        int chance = (int) (8 + attacker.getPower() / 15);

        if (attacker.getState() == SituationState.WAR) {
            chance += 15;
            cost *= 2;
        }

        if (attacker.getGold() < cost * 2) {
            return;
        }

        if (target.isGround() && !target.isOwned()) {
            if (attacker.spendGold(cost)) {
                claimCell(target, attacker);
                if (target.getType() != CellType.CAPITAL) {
                    target.setType(CellType.LAND);
                }
                attacker.addPower(1);
            }

        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.UNION && target.getOwner().getState() == SituationState.UNION) {
                return;
            }
            if (isFriendly(attacker, target.getOwner())) {
                return;
            }

            if (attacker.getState() == SituationState.WAR) {
                if (Rng.nextInt(100) < chance) {
                    if (attacker.spendGold(cost)) {
                        handleCapitalCaptureIfNeeded(target, attacker);
                        claimCell(target, attacker);
                        if (!target.isCapital()) {
                            target.setType(CellType.LAND);
                        }
                        attacker.addPower(1);
                    }
                }
            } else {
                if (Rng.nextInt(100) < 2) {
                    attacker.setState(SituationState.WAR);
                    target.getOwner().setState(SituationState.WAR);
                }
            }

        } else if (target.isWater() && !target.isOwned()) {
            if (Rng.nextInt(100) < 4 && attacker.spendGold(8)) {
                if (countShips(attacker) < 7) {
                    claimCell(target, attacker);
                    target.setType(CellType.SHIP);
                }
            }
        }
    }

    private static void trySail(int x, int y, Nation attacker) {
        int nx = x + (Rng.nextInt(3) - 1);
        int ny = y + (Rng.nextInt(3) - 1);
        if (nx < 0 || nx >= Config.get().width() || ny < 0 || ny >= Config.get().height()) return;

        Cell target = cells[ny][nx];
        Cell current = cells[y][x];

        if ((target.isWater() || target.isGround()) && !target.isOwned()) {
            if (attacker.spendGold(5)) {
                claimCell(current, null);
                current.setType(CellType.LAND);

                claimCell(target, attacker);

                if (target.isWater()) {
                    if (countShips(attacker) < 7) {
                        target.setType(CellType.SHIP);
                    } else {
                        target.setType(CellType.LAND);
                    }
                } else {
                    target.setType(CellType.LAND);
                }
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (isFriendly(attacker, target.getOwner())) {
                return;
            }
            if (attacker.getState() == SituationState.WAR && Rng.nextInt(100) < 25) {
                if (attacker.spendGold(10)) {
                    claimCell(current, null);
                    current.setType(CellType.LAND);

                    claimCell(target, attacker);
                    if (!target.isCapital()) {
                        target.setType(CellType.LAND);
                    }
                    attacker.addPower(2);
                }
            }
        }
    }

    private static void handleCapitalCaptureIfNeeded(Cell target, Nation attacker) {
        if (target.isCapital()) {
            Nation defender = target.getOwner();
            if (defender != null && defender != attacker) {
                if (Rng.nextInt(100) < 70) {
                    DiplomacyManager.makeVassal(attacker, defender);
                }
            }
        }
    }

    private static void rotateState() {
        int tick = Time.getCurrentTick();
        if (tick % 35 == 0) {
            for (Nation nation : nations) {
                if (nation.isVassal()) continue;

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
    }

    private static boolean isFriendly(Nation a, Nation b) {
        if (a == null || b == null || a == b) return true;
        if (a.getVassals().contains(b) || b.getVassals().contains(a)) {
            return true;
        }
        if (a.getState() == SituationState.UNION && b.getState() == SituationState.UNION) {
            return true;
        }

        return false;
    }

    public static void claimCell(Cell cell, Nation newOwner) {
        Nation oldOwner = cell.getOwner();
        if (oldOwner != null) {
            oldOwner.removeCell(cell);
        }
        if (newOwner != null) {
            newOwner.addCell(cell);
        } else {
            cell.setType(CellType.NONE);
        }
    }

    public static Cell[][] getCells() {
        return cells;
    }

    public static List<Nation> getNations() {
        return nations;
    }
}
