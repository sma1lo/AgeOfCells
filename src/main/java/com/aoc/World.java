package com.aoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class World {
    public static final int WIDTH = 205;
    public static final int HEIGHT = 53;

    private static final Random rand = new Random();
    private static final Cell[][] cells = new Cell[HEIGHT][WIDTH];
    private static final List<Nation> nations = new ArrayList<>();

    private static final MapGenerator generator = new MapGenerator();
    private static final WorldRenderer renderer = new WorldRenderer(WIDTH, HEIGHT);

    public static void init() {
        generator.generateTerrain(cells, WIDTH, HEIGHT);
        fillNations();
        generator.spawnCapitals(cells, nations, WIDTH, HEIGHT);
    }

    public static void generateGrid() {
        renderer.render(cells, WIDTH, HEIGHT);
    }

    private static void fillNations() {
        nations.clear();
        nations.add(new Nation("Rome", NationType.ROME, rand.nextInt(100) + 50));
        nations.add(new Nation("Bavaria", NationType.BAVARIA, rand.nextInt(100) + 50));
        nations.add(new Nation("England", NationType.ENGLAND, rand.nextInt(100) + 50));
        nations.add(new Nation("Russia", NationType.RUSSIA, rand.nextInt(100) + 50));
        nations.add(new Nation("Austria", NationType.AUSTRIA, rand.nextInt(100) + 50));
        nations.add(new Nation("Scotland", NationType.SCOTLAND, rand.nextInt(100) + 50));
        nations.add(new Nation("France", NationType.FRANCE, rand.nextInt(100) + 50));
        nations.add(new Nation("Prussia", NationType.PRUSSIA, rand.nextInt(100) + 50));
        nations.add(new Nation("Ottomans", NationType.OTTOMANS, rand.nextInt(100) + 50));
        nations.add(new Nation("Spain", NationType.SPAIN, rand.nextInt(100) + 50));
        nations.add(new Nation("Sweden", NationType.SWEDEN, rand.nextInt(100) + 50));
        nations.add(new Nation("Poland", NationType.POLAND, rand.nextInt(100) + 50));
        nations.add(new Nation("Venice", NationType.VENICE, rand.nextInt(100) + 50));
        nations.add(new Nation("Portugal", NationType.PORTUGAL, rand.nextInt(100) + 50));
        nations.add(new Nation("Denmark", NationType.DENMARK, rand.nextInt(100) + 50));
        nations.add(new Nation("Netherlands", NationType.NETHERLANDS, rand.nextInt(100) + 50));
        nations.add(new Nation("Persia", NationType.PERSIA, rand.nextInt(100) + 50));
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
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell cell = cells[y][x];
                Nation owner = cell.getOwner();

                if (owner != null && (cell.isCapital() || cell.isLand())) {
                    if (rand.nextInt(100) < 15) {
                        tryExpand(x, y, owner);
                    }
                }
                if (owner != null && cell.isShip()) {
                    if (rand.nextInt(100) < 20) {
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

        int nx = x + (rand.nextInt(3) - 1);
        int ny = y + (rand.nextInt(3) - 1);
        if (nx < 0 || nx >= WIDTH || ny < 0 || ny >= HEIGHT) return;

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
            if (attacker.getState() == SituationState.WAR && rand.nextInt(100) < chance) {
                if (attacker.spendGold(cost)) {
                    claimCell(target, attacker);
                    if (!target.isCapital()) {
                        target.setType(CellType.LAND);
                    }
                    attacker.addPower(2);
                }
            }
        } else if (target.isWater() && !target.isOwned()) {
            if (rand.nextInt(100) < 8 && attacker.spendGold(4)) {
                if (attacker.getShipCount() < 10) {
                    claimCell(target, attacker);
                    target.setType(CellType.SHIP);
                    attacker.incrementShipCount();
                }
            }
        }
    }

    private static void trySail(int x, int y, Nation attacker) {
        int nx = x + (rand.nextInt(3) - 1);
        int ny = y + (rand.nextInt(3) - 1);
        if (nx < 0 || nx >= WIDTH || ny < 0 || ny >= HEIGHT) return;

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
            if (attacker.getState() == SituationState.WAR && rand.nextInt(100) < 30) {
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
                if (rand.nextInt(100) < 55) {
                    nation.setState(SituationState.WAR);
                } else {
                    nation.setState(SituationState.PEACE);
                }
            }
        }
    }

    public static void claimCell(Cell cell, Nation newOwner) {
        Nation oldOwner = cell.getOwner();
        if (oldOwner != null) {
            oldOwner.removeCell(cell);
        }
        if (newOwner != null) {
            newOwner.addCell(cell);
        }
    }

    public static Cell[][] getCells() {
        return cells;
    }

    public static List<Nation> getNations() {
        return nations;
    }
}