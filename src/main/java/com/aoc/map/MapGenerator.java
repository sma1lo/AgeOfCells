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

    public void generateTerrain(Cell[][] cells, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(TerrainType.WATER);
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Rng.RAND.nextInt(100) < 50) {
                    cells[y][x].setTerrain(TerrainType.GROUND);
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            smooth(cells, width, height);
        }
    }

    public void spawnCapitals(Cell[][] cells, List<Nation> nations, int width, int height) {
        for (Nation nation : nations) {
            int attempts = 0;
            while (attempts < 10000) {
                int x = Rng.nextInt(width);
                int y = Rng.nextInt(height);
                Cell cell = cells[y][x];
                if (cell.isGround() && !cell.isOwned()) {
                    World.claimCell(cell, nation);
                    cell.setType(CellType.CAPITAL);
                    break;
                }
                attempts++;
            }
        }
    }

    private void smooth(Cell[][] cells, int width, int height) {
        TerrainType[][] buffer = new TerrainType[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int groundNeighbors = countGroundNeighbors(cells, x, y, width, height);
                if (groundNeighbors > Config.get().smooth()) {
                    buffer[y][x] = TerrainType.GROUND;
                } else if (groundNeighbors < Config.get().smooth()) {
                    buffer[y][x] = TerrainType.WATER;
                } else {
                    buffer[y][x] = cells[y][x].getTerrain();
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x].setTerrain(buffer[y][x]);
            }
        }
    }

    private int countGroundNeighbors(Cell[][] cells, int cx, int cy, int width, int height) {
        int count = 0;
        for (int yMod = -1; yMod <= 1; yMod++) {
            for (int xMod = -1; xMod <= 1; xMod++) {
                if (xMod == 0 && yMod == 0) continue;
                int nx = cx + xMod;
                int ny = cy + yMod;
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (cells[ny][nx].isGround()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
