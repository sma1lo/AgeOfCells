package com.aoc;


import java.math.BigInteger;
import java.util.Random;

public class World {
    static final int WIDTH = 40;
    static final int HEIGHT = 20;
    static Random rand = new Random();
    static TerrainType[][] grid = new TerrainType[HEIGHT][WIDTH];

    public static void init() {
        fillWater();
        fillGround();
        fillNation();
        generateGrid();
    }

    public static void generateGrid() {

        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == TerrainType.WATER) {
                    sb.append("~");
                } else if (grid[y][x] == TerrainType.ROME_CAPITAL) {
                    sb.append("R");
                } else if (grid[y][x] == TerrainType.BAVARIA_CAPITAL) {
                    sb.append("B");
                } else if (grid[y][x] == TerrainType.ENGLAND_CAPITAL) {
                    sb.append("E");
                } else if (grid[y][x] == TerrainType.ROME_LAND) {
                    sb.append("r");
                } else if (grid[y][x] == TerrainType.BAVARIA_LAND) {
                    sb.append("b");
                } else if (grid[y][x] == TerrainType.ENGLAND_LAND) {
                    sb.append("e");
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
        Nation rome = new Nation("Rome", NationType.ROME, BigInteger.valueOf(rand.nextInt(100) + 50));
        Nation bavaria = new Nation("Bavaria", NationType.BAVARIA, BigInteger.valueOf(rand.nextInt(100) + 50));
        Nation england = new Nation("England", NationType.ENGLAND, BigInteger.valueOf(rand.nextInt(100) + 50));

        spawnCapital(TerrainType.ROME_CAPITAL);

        spawnCapital(TerrainType.BAVARIA_CAPITAL);

        spawnCapital(TerrainType.ENGLAND_CAPITAL);
    }

    private static void spawnCapital(TerrainType capitalType) {
        while (true) {
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);

            if (grid[y][x] == TerrainType.GROUND) {
                grid[y][x] = capitalType;
                break;
            }
        }
    }

    public static void check() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == TerrainType.ROME_CAPITAL || grid[y][x] == TerrainType.ROME_LAND) {

                    if (rand.nextInt(100) < 15) {
                        tryExpand(x, y, TerrainType.ROME_LAND);
                    }
                } else if (grid[y][x] == TerrainType.BAVARIA_CAPITAL || grid[y][x] == TerrainType.BAVARIA_LAND) {
                    if (rand.nextInt(100) < 15) {
                        tryExpand(x, y, TerrainType.BAVARIA_LAND);
                    }
                } else if (grid[y][x] == TerrainType.ENGLAND_CAPITAL || grid[y][x] == TerrainType.ENGLAND_LAND) {
                    if (rand.nextInt(100) < 15) {
                        tryExpand(x, y, TerrainType.ENGLAND_LAND);
                    }
                }
            }
        }
    }


    private static void tryExpand(int x, int y, TerrainType landType) {
        int nx = x + (rand.nextInt(3) - 1);
        int ny = y + (rand.nextInt(3) - 1);

        if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT) {
            if (grid[ny][nx] == TerrainType.GROUND) {
                grid[ny][nx] = landType;
            }
        }
    }
}
