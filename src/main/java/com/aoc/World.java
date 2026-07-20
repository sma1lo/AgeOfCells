package com.aoc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    static final int WIDTH = 205;
    static final int HEIGHT = 53;
    static Random rand = new Random();
    static TerrainType[][] grid = new TerrainType[HEIGHT][WIDTH];
    static Nation[][] nationGrid = new Nation[HEIGHT][WIDTH];
    static CellType[][] cellTypeGrid = new CellType[HEIGHT][WIDTH];

    static List<Nation> nations = new ArrayList<Nation>();

    public static void init() {
        fillWater();
        fillGround();

        for (int i = 0; i < 4; i++) {
            smoothMap();
        }

        fillNation();
        generateGrid();
    }

    public static void generateGrid() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {

                if (grid[y][x] == TerrainType.WATER && nationGrid[y][x] == null) {
                    sb.append(Color.BLUE).append("~").append(Color.RESET);
                } else if (nationGrid[y][x] != null) {
                    Nation n = nationGrid[y][x];
                    String letter = n.getName().substring(0, 1);

                    if (cellTypeGrid[y][x] == CellType.CAPITAL) {
                        sb.append(n.getColor()).append(letter.toUpperCase()).append(Color.RESET);
                    } else if (cellTypeGrid[y][x] == CellType.LAND) {
                        sb.append(n.getColor()).append(letter.toLowerCase()).append(Color.RESET);
                    } else if (cellTypeGrid[y][x] == CellType.SHIP) {
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

    public static void fillWater() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = TerrainType.WATER;
                cellTypeGrid[y][x] = CellType.NONE;
            }
        }
    }

    public static void fillGround() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (rand.nextInt(100) < 50) {
                    grid[y][x] = TerrainType.GROUND;
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

        spawnCapital(nations.get(0));
        spawnCapital(nations.get(1));
        spawnCapital(nations.get(2));
        spawnCapital(nations.get(3));
        spawnCapital(nations.get(4));
        spawnCapital(nations.get(5));
        spawnCapital(nations.get(6));
    }

    private static void spawnCapital(Nation nation) {
        while (true) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);

            if (grid[y][x] == TerrainType.GROUND && nationGrid[y][x] == null) {
                nationGrid[y][x] = nation;
                cellTypeGrid[y][x] = CellType.CAPITAL;
                break;
            }
        }
    }

    public static void check() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Nation cellOwner = nationGrid[y][x];

                if (cellOwner != null && (cellTypeGrid[y][x] == CellType.CAPITAL || cellTypeGrid[y][x] == CellType.LAND)) {
                    if (rand.nextInt(100) < 15) {
                        tryExpand(x, y, cellOwner);
                    }
                }

                if (cellOwner != null && (cellTypeGrid[y][x] == CellType.SHIP)) {
                    if (rand.nextInt(100) < 20) {
                        trySail(x, y, cellOwner);
                    }
                }
            }
        }
    }

    private static void tryExpand(int x, int y, Nation attacker) {
        int nx = x + (rand.nextInt(3) - 1);
        int ny = y + (rand.nextInt(3) - 1);

        if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {

            if (grid[ny][nx] == TerrainType.GROUND && nationGrid[ny][nx] == null) {
                nationGrid[ny][nx] = attacker;
                cellTypeGrid[ny][nx] = CellType.LAND;
            } else if (nationGrid[ny][nx] != null && nationGrid[ny][nx] != attacker && cellTypeGrid[ny][nx] != CellType.SHIP) {
                if (attacker.state == SituationState.WAR && rand.nextInt(100) < 25) {
                    nationGrid[ny][nx] = attacker;
                    cellTypeGrid[ny][nx] = CellType.LAND;
                }
            } else if (grid[ny][nx] == TerrainType.WATER && nationGrid[ny][nx] == null) {
                if (rand.nextInt(100) < 5) {
                    nationGrid[ny][nx] = attacker;
                    cellTypeGrid[ny][nx] = CellType.SHIP;
                }
            }
        }
    }

    private static void trySail(int x, int y, Nation attacker) {
        int nx = x + (rand.nextInt(3) - 1);
        int ny = y + (rand.nextInt(3) - 1);

        if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
            if (grid[ny][nx] == TerrainType.WATER && nationGrid[ny][nx] == null) {
                nationGrid[ny][nx] = attacker;
                cellTypeGrid[ny][nx] = CellType.SHIP;

                nationGrid[y][x] = null;
                cellTypeGrid[y][x] = CellType.NONE;
            } else if (grid[ny][nx] == TerrainType.GROUND && nationGrid[ny][nx] == null) {
                nationGrid[ny][nx] = attacker;
                cellTypeGrid[ny][nx] = CellType.LAND;

                nationGrid[y][x] = null;
                cellTypeGrid[y][x] = CellType.NONE;
            }else if (nationGrid[ny][nx] != null && nationGrid[ny][nx] != attacker && cellTypeGrid[ny][nx] != CellType.SHIP) {
                if (attacker.state == SituationState.WAR && rand.nextInt(100) < 25) {
                    nationGrid[ny][nx] = attacker;
                    cellTypeGrid[ny][nx] = CellType.LAND;

                    nationGrid[y][x] = null;
                    cellTypeGrid[y][x] = CellType.NONE;
                }
            }
        }
    }


    private static int countGroundNeighbors(int cx, int cy) {
        int count = 0;
        for (int yMod = -1; yMod <= 1; yMod++) {
            for (int xMod = -1; xMod <= 1; xMod++) {
                int neighborX = cx + xMod;
                int neighborY = cy + yMod;

                if (xMod == 0 && yMod == 0) continue;

                if (neighborX >= 0 && neighborX < WIDTH && neighborY >= 0 && neighborY < HEIGHT) {
                    if (grid[neighborY][neighborX] == TerrainType.GROUND) {
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
                    buffer[y][x] = grid[y][x];
                }
            }
        }
        grid = buffer;
    }
}