package com.aoc.nation;

import com.aoc.cell.Cell;
import com.aoc.util.Color;

import java.util.*;

public class Nation {
    private String name;
    private long power;
    private long gold;
    protected SituationState state;
    private int stability = 100;
    private int libertyDesire = 0;
    private Color color;

    private Nation master = null;
    private final Set<Cell> ownedCells = new HashSet<>();
    private final List<Nation> vassals = new ArrayList<>();

    private static final Color[] AVAILABLE_COLORS = {
        Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.PURPLE,
        Color.CYAN, Color.WHITE, Color.BROWN, Color.PINK,
        Color.ORANGE, Color.GRAY, Color.BRIGHT_RED, Color.BRIGHT_GREEN,
        Color.BRIGHT_YELLOW, Color.BRIGHT_BLUE, Color.BRIGHT_CYAN,
        Color.LIME, Color.GOLD, Color.TEAL, Color.MAGENTA, Color.DARK_BLUE
    };

    public Nation(String name, long startingPower) {
        this.name = name;
        this.power = startingPower;
        this.gold = startingPower;
        this.state = SituationState.PEACE;
        this.stability = 100;
        this.color = AVAILABLE_COLORS[new Random().nextInt(AVAILABLE_COLORS.length)];
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

    public boolean isVassal() {
        return master != null;
    }

    public void addVassal(Nation vassal) {
        if (vassal != null && vassal != this && !vassals.contains(vassal)) {
            vassals.add(vassal);
            vassal.setMaster(this);
        }
    }

    public void removeVassal(Nation vassal) {
        if (vassals.remove(vassal)) {
            vassal.setMaster(null);
        }
    }

    public Nation getMaster() {
        return master;
    }

    public void setMaster(Nation master) {
        this.master = master;
    }

    public List<Nation> getVassals() {
        return Collections.unmodifiableList(vassals);
    }

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = Math.max(0, Math.min(100, stability));
    }

    public int getLibertyDesire() {
        return libertyDesire;
    }

    public void setLibertyDesire(int libertyDesire) {
        this.libertyDesire = Math.max(0, Math.min(100, libertyDesire));
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
            long converted = gold / 20;
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

    public void setState(SituationState state) {
        this.state = state;
    }

    public long getPower() {
        return power;
    }

    public long getGold() {
        return gold;
    }
}
