package com.aoc.map;

import com.aoc.World;
import com.aoc.cell.Cell;
import com.aoc.cell.CellType;
import com.aoc.config.Config;
import com.aoc.nation.Nation;
import com.aoc.cell.TerrainType;
import com.aoc.util.Rng;

import java.util.List;

public class MapGenerator {

    public void generateTerrain(World world, Cell[][] cells, int width, int height) {
        TerrainType[][] terrainMap = new TerrainType[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                terrainMap[y][x] = TerrainType.WATER;
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Rng.nextInt(100) < 50) {
                    terrainMap[y][x] = TerrainType.GROUND;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            terrainMap = smooth(terrainMap, width, height);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (terrainMap[y][x] == TerrainType.GROUND) {
                    int roll = Rng.nextInt(1000);
                    if (roll < 3) {
                        terrainMap[y][x] = TerrainType.GOLD;
                    } else if (roll < 5) {
                        terrainMap[y][x] = TerrainType.IRON;
                    } else if (roll < 10) {
                        terrainMap[y][x] = TerrainType.COAL;
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(terrainMap[y][x]);
            }
        }
    }

    public void spawnCapitals(World world, Cell[][] cells, List<Nation> nations, int width, int height) {
        for (Nation nation : nations) {
            int attempts = 0;
            while (attempts < 100) {
                int x = Rng.nextInt(width);
                int y = Rng.nextInt(height);
                Cell cell = cells[y][x];
                if (cell.isGround() && !cell.isOwned()) {
                    world.claimCell(cell, nation);
                    cell.setType(CellType.CAPITAL);
                    break;
                }
                attempts++;
            }
        }
    }

    public void spawnMarauderCamp(World world, Cell[][] cells, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = cells[y][x];
                if (cell.isGround() && !cell.isOwned() && cell.getType() == CellType.NONE) {
                    int roll = Rng.nextInt(1000);
                    if (roll < 5) {
                        cell.setType(CellType.CAMP);
                    }
                }
            }
        }
    }

    private TerrainType[][] smooth(TerrainType[][] currentMap, int width, int height) {
        TerrainType[][] buffer = new TerrainType[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int groundNeighbors = countGroundNeighbors(currentMap, x, y, width, height);
                if (groundNeighbors > Config.get().smooth()) {
                    buffer[y][x] = TerrainType.GROUND;
                } else if (groundNeighbors < Config.get().smooth()) {
                    buffer[y][x] = TerrainType.WATER;
                } else {
                    buffer[y][x] = currentMap[y][x];
                }
            }
        }
        return buffer;
    }

    private int countGroundNeighbors(TerrainType[][] map, int cx, int cy, int width, int height) {
        int count = 0;
        for (int yMod = -1; yMod <= 1; yMod++) {
            for (int xMod = -1; xMod <= 1; xMod++) {
                if (xMod == 0 && yMod == 0) continue;
                int nx = cx + xMod;
                int ny = cy + yMod;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    TerrainType type = map[ny][nx];
                    if (type == TerrainType.GROUND || type == TerrainType.GOLD
                        || type == TerrainType.IRON || type == TerrainType.COAL) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
