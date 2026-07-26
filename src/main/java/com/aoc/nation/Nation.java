package com.aoc.nation;

import com.aoc.cell.Cell;
import com.aoc.util.Color;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Nation {
    private String name;
    private NationType nationType;
    private long power;
    private long gold;
    private Color color;
    protected SituationState state;
    private int shipCount = 0;
    private final Set<Cell> ownedCells = new HashSet<>();

    public Nation(String name, NationType nationType, long startingPower) {
        this.name = name;
        this.nationType = nationType;
        this.power = startingPower;
        this.gold = startingPower;
        this.state = SituationState.PEACE;
        this.color = switch (nationType) {
            case ROME -> Color.RED;
            case BAVARIA -> Color.CYAN;
            case ENGLAND -> Color.GREEN;
            case FRANCE -> Color.PINK;
            case SCOTLAND -> Color.YELLOW;
            case AUSTRIA -> Color.PURPLE;
            case RUSSIA -> Color.BROWN;
            case PRUSSIA -> Color.BLUE;
            case OTTOMANS -> Color.ORANGE;
            case SPAIN -> Color.WHITE;
            case SWEDEN -> Color.GRAY;
            case POLAND -> Color.BRIGHT_RED;
            case VENICE -> Color.GOLD;
            case PORTUGAL -> Color.LIME;
            case DENMARK -> Color.MAGENTA;
            case NETHERLANDS -> Color.BRIGHT_YELLOW;
            case PERSIA -> Color.TEAL;
            default -> Color.WHITE;
        };
    }

    public void addCell(Cell cell) {
        ownedCells.add(cell);
        cell.setOwner(this);
    }

    public void removeCell(Cell cell) {
        ownedCells.remove(cell);
        if (cell.getOwner() == this) {
            cell.setOwner(null);
        }
    }

    public Set<Cell> getOwnedCells() {
        return Collections.unmodifiableSet(ownedCells);
    }

    public long getTotalCells() {
        return ownedCells.size();
    }

    public void addGold(long amount) {
        this.gold += amount;
    }

    public boolean spendGold(long amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public void addPower(long amount) {
        this.power += amount;
    }

    public void strengthenFromGold() {
        if (gold > 15) {
            long converted = gold / 12;
            addPower(converted);
            gold -= converted;
        }
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public SituationState getState() {
        return state;
    }

    public long getPower() {
        return power;
    }

    public long getGold() {
        return gold;
    }

    public void setState(SituationState state) {
        this.state = state;
    }

    public int getShipCount() {
        return shipCount;
    }

    public void incrementShipCount() {
        this.shipCount++;
    }

    public void decrementShipCount() {
        if (shipCount > 0) this.shipCount--;
    }
}