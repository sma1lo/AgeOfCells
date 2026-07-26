package com.aoc;

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
}