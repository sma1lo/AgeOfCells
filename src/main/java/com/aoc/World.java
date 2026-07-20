package com.aoc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    static final int WIDTH = 50;
    static final int HEIGHT = 50;
    static Random rand = new Random();
    static TerrainType[][] grid = new TerrainType[HEIGHT][WIDTH];
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
                if (grid[y][x] == TerrainType.WATER) {
                    sb.append(Color.BLUE).append("~").append(Color.RESET);
                } else if (grid[y][x] == TerrainType.ROME_CAPITAL) {
                    sb.append(Color.RED).append("R").append(Color.RESET);
                } else if (grid[y][x] == TerrainType.BAVARIA_CAPITAL) {
                    sb.append(Color.CYAN).append("B").append(Color.RESET);
                } else if (grid[y][x] == TerrainType.ENGLAND_CAPITAL) {
                    sb.append(Color.GREEN).append("E").append(Color.RESET);
                } else if (grid[y][x] == TerrainType.ROME_LAND) {
                    sb.append(Color.RED).append("r").append(Color.RESET);
                } else if (grid[y][x] == TerrainType.BAVARIA_LAND) {
                    sb.append(Color.CYAN).append("b").append(Color.RESET);
                } else if (grid[y][x] == TerrainType.ENGLAND_LAND) {
                    sb.append(Color.GREEN).append("e").append(Color.RESET);
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
        nations.add(new Nation("Rome", NationType.ROME, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("Bavaria", NationType.BAVARIA, BigInteger.valueOf(rand.nextInt(100) + 50)));
        nations.add(new Nation("England", NationType.ENGLAND, BigInteger.valueOf(rand.nextInt(100) + 50)));

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
            } else if (grid[ny][nx] == TerrainType.ROME_LAND || grid[ny][nx] == TerrainType.BAVARIA_LAND || grid[ny][nx] == TerrainType.ENGLAND_LAND) {
                Nation attacker = null;
                if (landType == TerrainType.ROME_LAND) attacker = nations.get(0);
                if (landType == TerrainType.BAVARIA_LAND) attacker = nations.get(1);
                if (landType == TerrainType.ENGLAND_LAND) attacker = nations.get(2);

                if (attacker != null && attacker.state == SituationState.WAR) {
                    if (rand.nextInt(100) < 25) {
                        grid[ny][nx] = landType;
                    }
                }

            } else if (grid[ny][nx] == TerrainType.WATER) {
                //TODO seafaring
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