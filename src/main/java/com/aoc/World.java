package com.aoc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    static final int WIDTH = 205;
    static final int HEIGHT = 53;
    static Random rand = new Random();

    static Cell[][] cells = new Cell[HEIGHT][WIDTH];
    static List<Nation> nations = new ArrayList<>();

    public static void init() {
        fillWater();
        fillGround();
        for (int i = 0; i < 4; i++) {
            smoothMap();
        }
        fillNation();
    }

    public static void generateGrid() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell cell = cells[y][x];

                if (cell.isWater() && !cell.isOwned()) {
                    sb.append(Color.BLUE).append("~").append(Color.RESET);
                } else if (cell.isOwned()) {
                    Nation n = cell.getOwner();
                    String letter = n.getName().substring(0, 1);

                    if (cell.isCapital()) {
                        sb.append(n.getColor()).append(letter.toUpperCase()).append(Color.RESET);
                    } else if (cell.isLand()) {
                        sb.append(n.getColor()).append(letter.toLowerCase()).append(Color.RESET);
                    } else if (cell.isShip()) {
                        sb.append(n.getColor()).append("^").append(Color.RESET);
                    }
                } else {
                    sb.append("0");
                }
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
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
        nations.add(new Nation("Rome", NationType.ROME, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("Bavaria", NationType.BAVARIA, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("England", NationType.ENGLAND, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("Russia", NationType.RUSSIA, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("Austria", NationType.AUSTRIA, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("Scotland", NationType.SCOTLAND, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("France", NationType.FRANCE, BigInteger.valueOf(rand.nextInt(100) + 50)));

        for (Nation nation : nations) {
            spawnCapital(nation);
        }
    }

    private static void spawnCapital(Nation nation) {
        while (true) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            Cell cell = cells[y][x];
            if (cell.isGround() && !cell.isOwned()) {
                cell.setOwner(nation);
                cell.setType(CellType.CAPITAL);
                break;
            }
        }
    }

    public static void collectEconomy() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Cell cell = cells[y][x];
                Nation owner = cell.getOwner();
                if (owner == null) continue;

                BigInteger income = switch (cell.getType()) {
                    case CAPITAL -> BigInteger.valueOf(8);
                    case LAND -> BigInteger.valueOf(3);
                    case SHIP -> BigInteger.valueOf(2);
                    default -> BigInteger.ZERO;
                };

                if (income.compareTo(BigInteger.ZERO) > 0) {
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

    private static void tryExpand(int x, int y, Nation attacker) {
        if (attacker.getPower().compareTo(BigInteger.valueOf(8)) < 0) return;

        int nx = x + (rand.nextInt(3) - 1);
        int ny = y + (rand.nextInt(3) - 1);
        if (nx < 0 || nx >= WIDTH || ny < 0 || ny >= HEIGHT) return;

        Cell target = cells[ny][nx];
        BigInteger cost = BigInteger.valueOf(5);
        int chance = 15 + attacker.getPower().intValue() / 7;

        if (attacker.getState() == SituationState.WAR) {
            chance += 20;
            cost = cost.multiply(BigInteger.valueOf(2));
        }
        if (target.isGround() && !target.isOwned()) {
            if (attacker.spendGold(cost)) {
                target.setOwner(attacker);
                target.setType(CellType.LAND);
                attacker.addPower(BigInteger.ONE);
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.WAR && rand.nextInt(100) < chance) {
                if (attacker.spendGold(cost)) {
                    target.setOwner(attacker);
                    target.setType(CellType.LAND);
                    attacker.addPower(BigInteger.valueOf(2));
                }
            }
        } else if (target.isWater() && !target.isOwned()) {
            if (rand.nextInt(100) < 8 && attacker.spendGold(BigInteger.valueOf(4))) {
                target.setOwner(attacker);
                target.setType(CellType.SHIP);
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
            if (attacker.spendGold(BigInteger.valueOf(3))) {
                target.setOwner(attacker);
                target.setType(target.isWater() ? CellType.SHIP : CellType.LAND);
                current.setOwner(null);
                current.setType(CellType.NONE);
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.WAR && rand.nextInt(100) < 30) {
                if (attacker.spendGold(BigInteger.valueOf(8))) {
                    target.setOwner(attacker);
                    target.setType(CellType.LAND);
                    current.setOwner(null);
                    current.setType(CellType.NONE);
                    attacker.addPower(BigInteger.valueOf(3));
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