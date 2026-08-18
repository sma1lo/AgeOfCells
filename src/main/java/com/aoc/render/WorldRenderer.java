package com.aoc.render;

import com.aoc.cell.Cell;
import com.aoc.cell.TerrainType;
import com.aoc.nation.Nation;
import com.aoc.util.Color;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class WorldRenderer {
    private final int width;
    private final int height;

    public WorldRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void render(Cell[][] cells, Screen screen) {
        TextGraphics graphics = screen.newTextGraphics();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = cells[y][x];
                String symbol = "0";
                Color color = Color.RESET;

                if (cell.isWater()) {
                    symbol = "~";
                    color = Color.BLUE;
                } else if (cell.getTerrain() == TerrainType.GOLD) {
                    symbol = "$";
                } else if (cell.getTerrain() == TerrainType.IRON) {
                    symbol = "#";
                } else if (cell.getTerrain() == TerrainType.COAL) {
                    symbol = "*";
                } else if (cell.isCamp()) {
                    symbol = "c";
                } else if(cell.isMarauder()) {
                    symbol = "m";
                }

                if (cell.isOwned()) {
                    Nation n = cell.getOwner();
                    color = n.getColor();

                    if (cell.isCapital()) {
                        symbol = n.getName().substring(0, 1).toUpperCase();
                    } else if (cell.isCastle()) {
                        symbol = "C";
                    } else if (cell.isTown()) {
                        symbol = "T";
                    } else if (cell.isVillage()) {
                        symbol = "v";
                    } else if (cell.isShip()) {
                        symbol = "^";
                    } else if (cell.isLand()) {
                        if (cell.getTerrain() == TerrainType.GOLD ||
                            cell.getTerrain() == TerrainType.IRON ||
                            cell.getTerrain() == TerrainType.COAL) {
                        } else {
                            symbol = n.getName().substring(0, 1).toLowerCase();
                        }
                    }
                }

                graphics.setForegroundColor(color.getColor());
                graphics.putString(new TerminalPosition(x, y + 1), symbol);
            }
        }
    }
}
