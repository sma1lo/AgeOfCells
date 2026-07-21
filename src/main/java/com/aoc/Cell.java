package com.aoc;

public class Cell {
    private TerrainType terrain;
    private Nation owner;
    private CellType type;

    public Cell(TerrainType terrain) {
        this.terrain = terrain;
        this.owner = null;
        this.type = CellType.NONE;
    }

    public TerrainType getTerrain() { return terrain; }
    public void setTerrain(TerrainType terrain) { this.terrain = terrain; }

    public Nation getOwner() { return owner; }
    public void setOwner(Nation owner) { this.owner = owner; }

    public CellType getType() { return type; }
    public void setType(CellType type) { this.type = type; }

    public boolean isWater() {
        return terrain == TerrainType.WATER;
    }

    public boolean isGround() {
        return terrain == TerrainType.GROUND;
    }

    public boolean isOwned() {
        return owner != null;
    }

    public boolean isCapital() {
        return type == CellType.CAPITAL;
    }

    public boolean isShip() {
        return type == CellType.SHIP;
    }

    public boolean isLand() {
        return type == CellType.LAND;
    }
}