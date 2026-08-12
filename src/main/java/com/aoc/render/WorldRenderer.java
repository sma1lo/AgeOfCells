package com.aoc.render;

import com.aoc.cell.Cell;
import com.aoc.cell.TerrainType;
import com.aoc.nation.Nation;
import com.aoc.util.Color;

public class WorldRenderer {
    private final StringBuilder gridBuilder;

    public WorldRenderer(int width, int height) {
        this.gridBuilder = new StringBuilder(width * height * 15);
    }

    public void render(Cell[][] cells, int width, int height) {
        gridBuilder.setLength(0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = cells[y][x];
                String symbol = "0";

                if (cell.isWater()) {
                    symbol = "~";
                } else if (cell.getTerrain() == TerrainType.GOLD) {
                    symbol = "$";
                } else if (cell.getTerrain() == TerrainType.IRON) {
                    symbol = "#";
                } else if (cell.getTerrain() == TerrainType.COAL) {
                    symbol = "*";
                }

                if (cell.isOwned()) {
                    Nation n = cell.getOwner();

                    if (cell.isCapital()) {
                        gridBuilder.append(n.getColor()).append(n.getName().substring(0, 1).toUpperCase()).append(Color.RESET);
                    } else if (cell.isLand()) {
                        if (cell.getTerrain() == TerrainType.GOLD ||
                            cell.getTerrain() == TerrainType.IRON ||
                            cell.getTerrain() == TerrainType.COAL) {
                            gridBuilder.append(n.getColor()).append(symbol).append(Color.RESET);
                        } else {
                            gridBuilder.append(n.getColor()).append(n.getName().substring(0, 1).toLowerCase()).append(Color.RESET);
                        }
                    } else if (cell.isShip()) {
                        gridBuilder.append(n.getColor()).append("^").append(Color.RESET);
                    }
                } else {
                    if (cell.isWater()) {
                        gridBuilder.append(Color.BLUE).append(symbol).append(Color.RESET);
                    } else {
                        gridBuilder.append(symbol);
                    }
                }
            }
            gridBuilder.append("\n");
        }
        System.out.println(gridBuilder.toString());
    }
}
