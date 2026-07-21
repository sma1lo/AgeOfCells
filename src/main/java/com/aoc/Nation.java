package com.aoc;

import java.math.BigInteger;

public class Nation {
    private String name;
    private NationType nationType;
    private BigInteger power;
    private BigInteger gold;
    private BigInteger totalCells;
    private Color color;
    protected SituationState state;

    public Nation(String name, NationType nationType, BigInteger startingPower) {
        this.name = name;
        this.nationType = nationType;
        this.power = startingPower;
        this.gold = startingPower;
        this.totalCells = BigInteger.ZERO;
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

    public void addGold(BigInteger amount) {
        this.gold = this.gold.add(amount);
    }

    public boolean spendGold(BigInteger amount) {
        if (gold.compareTo(amount) >= 0) {
            gold = gold.subtract(amount);
            return true;
        }
        return false;
    }

    public void addPower(BigInteger amount) {
        this.power = this.power.add(amount);
    }

    public void strengthenFromGold() {
        if (gold.compareTo(BigInteger.valueOf(15)) > 0) {
            BigInteger converted = gold.divide(BigInteger.valueOf(12));
            addPower(converted);
            gold = gold.subtract(converted);
        }
    }

    public void incrementTotalCells() {
        this.totalCells = this.totalCells.add(BigInteger.ONE);
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

    public BigInteger getPower() {
        return power;
    }

    public BigInteger getGold() {
        return gold;
    }

    public BigInteger getTotalCells() {
        return totalCells;
    }

    public void setState(SituationState state) {
        this.state = state;
    }
}