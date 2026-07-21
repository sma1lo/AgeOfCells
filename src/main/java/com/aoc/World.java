package com.aoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class World {
    static final int WIDTH = 205;
    static final int HEIGHT = 53;
    static Random rand = new Random();

    static Cell[][] cells = new Cell[HEIGHT][WIDTH];
    static List<Nation> nations = new ArrayList<>();
    private static final StringBuilder gridBuilder = new StringBuilder(WIDTH * HEIGHT * 15);

    public static void init() {
        fillWater();
        fillGround();
        for (int i = 0; i < 4; i++) {
            smoothMap();
        }
        fillNation();
    }

    public static void generateGrid() {
        gridBuilder.setLength(0);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell cell = cells[y][x];

                if (cell.isWater() && !cell.isOwned()) {
                    gridBuilder.append(Color.BLUE).append("~").append(Color.RESET);
                } else if (cell.isOwned()) {
                    Nation n = cell.getOwner();
                    String letter = n.getName().substring(0, 1);

                    if (cell.isCapital()) {
                        gridBuilder.append(n.getColor()).append(letter.toUpperCase()).append(Color.RESET);
                    } else if (cell.isLand()) {
                        gridBuilder.append(n.getColor()).append(letter.toLowerCase()).append(Color.RESET);
                    } else if (cell.isShip()) {
                        gridBuilder.append(n.getColor()).append("^").append(Color.RESET);
                    }
                } else {
                    gridBuilder.append("0");
                }
            }
            gridBuilder.append("\n");
        }
        System.out.println(gridBuilder.toString());
    }

    private static void fillWater() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                cells[y][x] = new Cell(TerrainType.WATER);
            }
        }
    }

    private static void fillGround() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (rand.nextInt(100) < 50) {
                    cells[y][x].setTerrain(TerrainType.GROUND);
                }
            }
        }
    }

    public static void fillNation() {
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
        for (Nation nation : nations) {
            spawnCapital(nation);
        }
    }

    private static void spawnCapital(Nation nation) {
        int attempts = 0;
        while (attempts < 10000) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            Cell cell = cells[y][x];
            if (cell.isGround() && !cell.isOwned()) {
                cell.setOwner(nation);
                cell.setType(CellType.CAPITAL);
                break;
            }
            attempts++;
        }
    }

    public static void collectEconomy() {
        for (Nation nation : nations) {
            nation.resetTotalCells();
        }

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell cell = cells[y][x];
                Nation owner = cell.getOwner();
                if (owner == null) continue;

                long income = switch (cell.getType()) {
                    case CAPITAL -> 8;
                    case LAND -> 3;
                    case SHIP -> 2;
                    default -> 0;
                };

                if (income > 0) {
                    owner.addGold(income);
                    owner.incrementTotalCells();
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
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell cell = cells[y][x];
                if (cell.getOwner() == nation && cell.isCapital()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void eliminateNation(Nation nation) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell cell = cells[y][x];
                if (cell.getOwner() == nation) {
                    cell.setOwner(null);
                    cell.setType(CellType.NONE);
                }
            }
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
                target.setOwner(attacker);
                target.setType(CellType.LAND);
                attacker.addPower(1);
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.WAR && rand.nextInt(100) < chance) {
                if (attacker.spendGold(cost)) {
                    target.setOwner(attacker);
                    if (!target.isCapital()) {
                        target.setType(CellType.LAND);
                    }
                    attacker.addPower(2);
                }
            }
        } else if (target.isWater() && !target.isOwned()) {
            if (rand.nextInt(100) < 8 && attacker.spendGold(4)) {
                if (attacker.getShipCount() < 10) {
                    target.setOwner(attacker);
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
                target.setOwner(attacker);
                if (target.isWater()) {
                    target.setType(CellType.SHIP);
                } else {
                    target.setType(CellType.LAND);
                    attacker.decrementShipCount();
                }
                current.setOwner(null);
                current.setType(CellType.NONE);
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.WAR && rand.nextInt(100) < 30) {
                if (attacker.spendGold(8)) {
                    target.setOwner(attacker);
                    if (!target.isCapital()) {
                        target.setType(CellType.LAND);
                    }
                    current.setOwner(null);
                    current.setType(CellType.NONE);
                    attacker.decrementShipCount();
                    attacker.addPower(3);
                }
            }
        }
    }

    private static int countGroundNeighbors(int cx, int cy) {
        int count = 0;
        for (int yMod = -1; yMod <= 1; yMod++) {
            for (int xMod = -1; xMod <= 1; xMod++) {
                if (xMod == 0 && yMod == 0) continue;
                int nx = cx + xMod;
                int ny = cy + yMod;
                if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
                    if (cells[ny][nx].isGround()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static void smoothMap() {
        TerrainType[][] buffer = new TerrainType[HEIGHT][WIDTH];

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int groundNeighbors = countGroundNeighbors(x, y);
                if (groundNeighbors > 4) {
                    buffer[y][x] = TerrainType.GROUND;
                } else if (groundNeighbors < 4) {
                    buffer[y][x] = TerrainType.WATER;
                } else {
                    buffer[y][x] = cells[y][x].getTerrain();
                }
            }
        }

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                cells[y][x].setTerrain(buffer[y][x]);
            }
        }
    }
}