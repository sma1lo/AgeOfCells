package com.aoc.cell;

import com.aoc.nation.Nation;

public class Cell {
    private final TerrainType terrain;
    private Nation owner;
    private CellType type;

    public Cell(TerrainType terrain) {
        this.terrain = terrain;
        this.owner = null;
        this.type = CellType.NONE;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public Nation getOwner() {
        return owner;
    }

    public void setOwner(Nation newOwner) {
        if (this.owner != null) {
            this.owner.removeCell(this);
        }
        this.owner = newOwner;

        if (newOwner != null) {
            newOwner.addCell(this);
        }

    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public boolean isWater() {
        return terrain == TerrainType.WATER;
    }

    public boolean isGround() {
        return terrain == TerrainType.GROUND
            || terrain == TerrainType.GOLD
            || terrain == TerrainType.IRON
            || terrain == TerrainType.COAL;
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
        return type == CellType.LAND
            || type == CellType.VILLAGE
            || type == CellType.TOWN
            || type == CellType.CASTLE
            || type == CellType.CAMP
            || type == CellType.PORT;
    }

    public boolean isVillage() {
        return type == CellType.VILLAGE;
    }

    public boolean isTown() {
        return type == CellType.TOWN;
    }

    public boolean isCastle() {
        return type == CellType.CASTLE;
    }

    public boolean isCamp() {
        return type == CellType.CAMP;
    }

    public boolean isPort() {
        return type == CellType.PORT;
    }

    public boolean isMarauder() {
        return type == CellType.MARAUDER;
    }
}
