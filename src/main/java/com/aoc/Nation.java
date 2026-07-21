package com.aoc;

public class Nation {
    private String name;
    private NationType nationType;
    private long power;
    private long gold;
    private long totalCells;
    private Color color;
    protected SituationState state;
    private int shipCount = 0;

    public Nation(String name, NationType nationType, long startingPower) {
        this.name = name;
        this.nationType = nationType;
        this.power = startingPower;
        this.gold = startingPower;
        this.totalCells = 0;
        this.state = SituationState.PEACE;

        this.color = switch (nationType) {
            case ROME -> Color.RED;
            case BAVARIA -> Color.CYAN;
            case ENGLAND -> Color.GREEN;
            case FRANCE -> Color.PINK;
            case SCOTLAND -> Color.YELLOW;
            case AUSTRIA -> Color.PURPLE;
            case RUSSIA -> Color.BROWN;
            default -> Color.WHITE;
        };
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

    public void resetTotalCells() {
        this.totalCells = 0;
    }

    public void incrementTotalCells() {
        this.totalCells++;
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

    public long getTotalCells() {
        return totalCells;
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