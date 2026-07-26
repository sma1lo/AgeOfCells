package com.aoc;

import com.aoc.cell.*;
import com.aoc.config.Config;
import com.aoc.map.MapGenerator;
import com.aoc.nation.Nation;
import com.aoc.nation.NationType;
import com.aoc.nation.SituationState;
import com.aoc.render.WorldRenderer;
import com.aoc.util.Element;
import com.aoc.util.Time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World {
    private static final Cell[][] cells = new Cell[Config.HEIGHT][Config.WIDTH];
    private static final List<Nation> nations = new ArrayList<>();

    private static final MapGenerator generator = new MapGenerator();
    private static final WorldRenderer renderer = new WorldRenderer(Config.WIDTH, Config.HEIGHT);

    public static void init() {
        generator.generateTerrain(cells, Config.WIDTH, Config.HEIGHT);
        fillNations();
        generator.spawnCapitals(cells, nations, Config.WIDTH, Config.HEIGHT);
    }

    public static void generateGrid() {
        renderer.render(cells, Config.WIDTH, Config.HEIGHT);
    }

    private static void fillNations() {
        nations.clear();
        nations.add(new Nation("Rome", NationType.ROME, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Bavaria", NationType.BAVARIA, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("England", NationType.ENGLAND, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Russia", NationType.RUSSIA, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Austria", NationType.AUSTRIA, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Scotland", NationType.SCOTLAND, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("France", NationType.FRANCE, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Prussia", NationType.PRUSSIA, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Ottomans", NationType.OTTOMANS, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Spain", NationType.SPAIN, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Sweden", NationType.SWEDEN, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Poland", NationType.POLAND, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Venice", NationType.VENICE, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Portugal", NationType.PORTUGAL, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Denmark", NationType.DENMARK, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Netherlands", NationType.NETHERLANDS, Config.RAND.nextInt(100) + 50));
        nations.add(new Nation("Persia", NationType.PERSIA, Config.RAND.nextInt(100) + 50));
    }

    public static void update() {
        collectEconomy();
        if (Time.getCurrentTick() % 4 == 0) {
            for (Nation nation : nations) {
                nation.strengthenFromGold();
            }
        }

        rotateState();
        check();
        checkCapitals();
    }

    public static void collectEconomy() {
        for (Nation nation : nations) {
            for (Cell cell : nation.getOwnedCells()) {
                long income = switch (cell.getType()) {
                    case CAPITAL -> 8;
                    case LAND -> 3;
                    case SHIP -> 2;
                    default -> 0;
                };

                if (income > 0) {
                    nation.addGold(income);
                }
            }
        }
    }

    public static void check() {
        for (int y = 0; y < Config.HEIGHT; y++) {
            for (int x = 0; x < Config.WIDTH; x++) {
                Cell cell = cells[y][x];
                Nation owner = cell.getOwner();

                if (owner != null && (cell.isCapital() || cell.isLand())) {
                    if (Config.RAND.nextInt(100) < 15) {
                        tryExpand(x, y, owner);
                    }
                }
                if (owner != null && cell.isShip()) {
                    if (Config.RAND.nextInt(100) < 20) {
                        trySail(x, y, owner);
                    }
                }
            }
        }
    }

    public static void checkCapitals() {
        Iterator<Nation> iterator = nations.iterator();
        while (iterator.hasNext()) {
            Nation nation = iterator.next();
            if (!hasCapital(nation)) {
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
        List<Cell> cellsToClear = new ArrayList<>(nation.getOwnedCells());
        for (Cell cell : cellsToClear) {
            cell.setType(CellType.NONE);
            claimCell(cell, null);
        }
    }

    private static void tryExpand(int x, int y, Nation attacker) {
        if (attacker.getPower() < 8) return;

        int nx = x + (Config.RAND.nextInt(3) - 1);
        int ny = y + (Config.RAND.nextInt(3) - 1);
        if (nx < 0 || nx >= Config.WIDTH || ny < 0 || ny >= Config.HEIGHT) return;

        Cell target = cells[ny][nx];
        long cost = 5;
        int chance = (int) (15 + attacker.getPower() / 7);

        if (attacker.getState() == SituationState.WAR) {
            chance += 20;
            cost *= 2;
        }

        if (target.isGround() && !target.isOwned()) {
            if (attacker.spendGold(cost)) {
                claimCell(target, attacker);
                target.setType(CellType.LAND);
                attacker.addPower(1);
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.UNION && target.getOwner().getState() == SituationState.UNION) {
                return;
            }

            if (attacker.getState() == SituationState.WAR && Config.RAND.nextInt(100) < chance) {
                if (attacker.spendGold(cost)) {
                    claimCell(target, attacker);
                    if (!target.isCapital()) {
                        target.setType(CellType.LAND);
                    }
                    attacker.addPower(2);
                }
            }
        } else if (target.isWater() && !target.isOwned()) {
            if (Config.RAND.nextInt(100) < 8 && attacker.spendGold(4)) {
                if (attacker.getShipCount() < 10) {
                    claimCell(target, attacker);
                    target.setType(CellType.SHIP);
                    attacker.incrementShipCount();
                }
            }
        }
    }

    private static void trySail(int x, int y, Nation attacker) {
        int nx = x + (Config.RAND.nextInt(3) - 1);
        int ny = y + (Config.RAND.nextInt(3) - 1);
        if (nx < 0 || nx >= Config.WIDTH || ny < 0 || ny >= Config.HEIGHT) return;

        Cell target = cells[ny][nx];
        Cell current = cells[y][x];

        if ((target.isWater() || target.isGround()) && !target.isOwned()) {
            if (attacker.spendGold(3)) {
                claimCell(target, attacker);
                if (target.isWater()) {
                    target.setType(CellType.SHIP);
                } else {
                    target.setType(CellType.LAND);
                    attacker.decrementShipCount();
                }
                claimCell(current, null);
                current.setType(CellType.NONE);
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.WAR && Config.RAND.nextInt(100) < 30) {
                if (attacker.spendGold(8)) {
                    claimCell(target, attacker);
                    if (!target.isCapital()) {
                        target.setType(CellType.LAND);
                    }
                    claimCell(current, null);
                    current.setType(CellType.NONE);
                    attacker.decrementShipCount();
                    attacker.addPower(3);
                }
            }
        }
    }

    private static void rotateState() {
        int tick = Time.getCurrentTick();
        if (tick % 35 == 0) {
            for (Nation nation : nations) {
                int chance = Config.RAND.nextInt(100);

                if (chance < 55) {
                    nation.setState(SituationState.WAR);
                } else if (chance < 80 && nations.size() > 1) {
                    Nation partner = Element.getRandomElement(nations);
                    if (partner != null && partner != nation) {
                        nation.setState(SituationState.UNION);
                        partner.setState(SituationState.UNION);
                    }
                } else {
                    nation.setState(SituationState.PEACE);
                }
            }
        }
    }

    public static void claimCell(Cell cell, Nation newOwner) {
        Nation oldOwner = cell.getOwner();
        if (oldOwner != null) {
            if (cell.isShip()) {
                oldOwner.decrementShipCount();
            }
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