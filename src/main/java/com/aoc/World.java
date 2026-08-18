package com.aoc;

import com.aoc.cell.*;
import com.aoc.config.Config;
import com.aoc.diplomacy.DiplomacyManager;
import com.aoc.map.MapGenerator;
import com.aoc.nation.Nation;
import com.aoc.nation.NationGenerator;
import com.aoc.nation.SituationState;
import com.aoc.render.WorldRenderer;
import com.googlecode.lanterna.screen.Screen;
import com.aoc.util.Element;
import com.aoc.util.Rng;
import com.aoc.util.Time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World {

    private final Cell[][] cells;
    private final List<Nation> nations;
    private final Time time;
    private final MapGenerator generator;
    private final WorldRenderer renderer;
    private final int height;
    private final int width;

    public World(Time time) {
        this.time = time;
        this.width = Config.get().width();
        this.height = Config.get().height();
        this.cells = new Cell[height][width];
        this.nations = new ArrayList<>();
        this.generator = new MapGenerator();
        this.renderer = new WorldRenderer(width, height);
    }

    public void init() {
        generator.generateTerrain(this, cells, this.width, this.height);

        fillNations();
        generator.spawnMarauderCamp(this, cells, width, height);
        generator.spawnCapitals(this, cells, nations, this.width, this.height);
    }


    public void generateGrid(Screen screen) {
        renderer.render(cells, screen);
    }

    private void fillNations() {
        nations.clear();
        NationGenerator.generate(nations);
    }

    public void update() {
        int currentTick = this.time.getCurrentTick();
        collectEconomy();

        if (currentTick % 4 == 0) {
            for (Nation nation : nations) {
                nation.strengthenFromGold();
            }
        }

        DiplomacyManager.update(nations);
        rotateState(currentTick);
        check(currentTick);
        checkCapitals();
        updateMarauders();

        for (Nation nation : nations) {
            int ships = countShips(nation);
            if (ships > 7) {
                int toRemove = ships - 7;
                for (Cell cell : new ArrayList<>(nation.getOwnedCells())) {
                    if (toRemove <= 0) break;
                    if (cell.isShip()) {
                        cell.setType(CellType.LAND);
                        toRemove--;
                    }
                }
            }
        }
    }

    private int countShips(Nation nation) {
        int count = 0;
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.isShip()) count++;
        }
        return count;
    }

    public void collectEconomy() {
        for (Nation nation : nations) {
            for (Cell cell : nation.getOwnedCells()) {
                long income = 0;

                income += switch (cell.getType()) {
                    case CAPITAL -> 4;
                    case LAND -> 1;
                    case SHIP -> 1;
                    case CASTLE -> 2;
                    case TOWN -> 4;
                    case VILLAGE -> 2;
                    default -> 0;
                };

                income += switch (cell.getTerrain()) {
                    case GOLD -> 8;
                    case IRON -> 5;
                    case COAL -> 3;
                    default -> 0;
                };

                if (income > 0) {
                    nation.addGold(income);
                }
            }
        }
    }

    public void check(int tick) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Cell cell = cells[y][x];
                Nation owner = cell.getOwner();

                if (owner != null && (cell.isCapital() || cell.isLand())) {
                    if (Rng.nextInt(100) < 15) {
                        tryExpand(x, y, owner);
                    }
                }

                if (owner != null && cell.isShip()) {
                    if (Rng.nextInt(100) < 20) {
                        trySail(x, y, owner);
                    }
                }
            }
        }

        if (tick % 10 == 0) {
            for (Nation nation : nations) {
                tryBuilding(nation);
            }
        }
    }

    private boolean hasLand(Nation nation) {
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.isLand() || cell.isCapital()) {
                return true;
            }
        }
        return false;
    }

    public void checkCapitals() {
        Iterator<Nation> iterator = nations.iterator();
        while (iterator.hasNext()) {
            Nation nation = iterator.next();

            boolean noLand = !hasLand(nation);
            boolean noCapitalAndNotVassal = !hasCapital(nation) && !nation.isVassal();

            if (noLand || noCapitalAndNotVassal) {
                eliminateNation(nation);
                iterator.remove();
            }
        }
    }

    private boolean hasCapital(Nation nation) {
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.isCapital()) {
                return true;
            }
        }
        return false;
    }

    private void eliminateNation(Nation nation) {
        if (nation.isVassal()) {
            nation.getMaster().removeVassal(nation);
        }
        for (Nation v : new ArrayList<>(nation.getVassals())) {
            nation.removeVassal(v);
        }

        List<Cell> cellsToClear = new ArrayList<>(nation.getOwnedCells());
        for (Cell cell : cellsToClear) {
            cell.setType(CellType.NONE);
            claimCell(cell, null);
        }
    }

    private void tryExpand(int x, int y, Nation attacker) {
        if (attacker.getPower() < 10) return;

        int nx = x + (Rng.nextInt(3) - 1);
        int ny = y + (Rng.nextInt(3) - 1);
        if (nx < 0 || nx >= this.width || ny < 0 || ny >= this.height) return;

        Cell target = cells[ny][nx];
        long cost = 12;
        int chance = (int) (8 + attacker.getPower() / 15);

        if (attacker.getState() == SituationState.WAR) {
            chance += 15;
            cost *= 2;
        }

        if (attacker.getGold() < cost * 2) {
            return;
        }

        if (target.isGround() && !target.isOwned()) {
            if (attacker.spendGold(cost)) {
                claimCell(target, attacker);
                if (target.getType() != CellType.CAPITAL) {
                    target.setType(CellType.LAND);
                }
                attacker.addPower(1);
            }

        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (attacker.getState() == SituationState.UNION && target.getOwner().getState() == SituationState.UNION) {
                return;
            }
            if (isFriendly(attacker, target.getOwner())) {
                return;
            }

            if (attacker.getState() == SituationState.WAR) {
                if (Rng.nextInt(100) < chance) {
                    if (attacker.spendGold(cost)) {
                        handleCapitalCaptureIfNeeded(target, attacker);
                        claimCell(target, attacker);
                        if (!target.isCapital()) {
                            target.setType(CellType.LAND);
                        }
                        attacker.addPower(1);
                    }
                }
            } else {
                if (Rng.nextInt(100) < 2) {
                    attacker.setState(SituationState.WAR);
                    target.getOwner().setState(SituationState.WAR);
                }
            }

        } else if (target.isWater() && !target.isOwned()) {
            if (Rng.nextInt(100) < 4 && attacker.spendGold(8)) {
                if (countShips(attacker) < 7) {
                    claimCell(target, attacker);
                    target.setType(CellType.SHIP);
                }
            }
        }
    }

    private void trySail(int x, int y, Nation attacker) {
        int nx = x + (Rng.nextInt(3) - 1);
        int ny = y + (Rng.nextInt(3) - 1);
        if (nx < 0 || nx >= this.width || ny < 0 || ny >= this.height) return;

        Cell target = cells[ny][nx];
        Cell current = cells[y][x];

        if ((target.isWater() || target.isGround()) && !target.isOwned()) {
            if (attacker.spendGold(5)) {
                claimCell(current, null);
                current.setType(CellType.LAND);

                claimCell(target, attacker);

                if (target.isWater()) {
                    if (countShips(attacker) < 7) {
                        target.setType(CellType.SHIP);
                    } else {
                        target.setType(CellType.LAND);
                    }
                } else {
                    target.setType(CellType.LAND);
                }
            }
        } else if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (isFriendly(attacker, target.getOwner())) {
                return;
            }
            if (attacker.getState() == SituationState.WAR && Rng.nextInt(100) < 25) {
                if (attacker.spendGold(10)) {
                    claimCell(current, null);
                    current.setType(CellType.LAND);

                    claimCell(target, attacker);
                    if (!target.isCapital()) {
                        target.setType(CellType.LAND);
                    }
                    attacker.addPower(2);
                }
            }
        }
    }

    private static void handleCapitalCaptureIfNeeded(Cell target, Nation attacker) {
        if (target.isCapital()) {
            Nation defender = target.getOwner();
            if (defender != null && defender != attacker) {
                if (Rng.nextInt(100) < 70) {
                    DiplomacyManager.makeVassal(attacker, defender);
                }
            }
        }
    }

    private static int countBuildings(Nation nation, CellType type) {
        int count = 0;
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.getType() == type) count++;
        }
        return count;
    }

    private void tryBuilding(Nation builder) {
        List<Cell> lands = new ArrayList<>();
        for (Cell cell : builder.getOwnedCells()) {
            if (cell.getType() == CellType.LAND
                || cell.getType() == CellType.VILLAGE
                || cell.getType() == CellType.TOWN) {
                lands.add(cell);
            }
        }
        if (lands.isEmpty()) return;
        Cell cell = Element.getRandomElement(lands);
        int cost;
        CellType next;
        int limit;
        switch (cell.getType()) {
            case LAND -> {
                cost = 300;
                next = CellType.VILLAGE;
                limit = 24;
            }
            case VILLAGE -> {
                cost = 500;
                next = CellType.TOWN;
                limit = 12;
            }

            case TOWN -> {
                cost = 1000;
                next = CellType.CASTLE;
                limit = 5;
            }

            default -> {
                return;
            }
        }
        if (builder.getGold() < cost) return;
        if (countBuildings(builder, next) >= limit) return;
        builder.spendGold(cost);
        cell.setType(next);
        if (next == CellType.CASTLE) {
            builder.addPower(25);
        }
    }

    public void updateMarauders() {
        boolean[][] movedThisTick = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = cells[y][x];

                if (cell.getType() == CellType.CAMP) {
                    if (Rng.nextInt(100) < 20) {
                        Object[] spawnData = getRandomNeighboringLand(x, y);
                        if (spawnData != null) {
                            Cell spawnPoint = (Cell) spawnData[0];
                            if (spawnPoint.getType() == CellType.NONE && !spawnPoint.isOwned()) {
                                spawnPoint.setType(CellType.MARAUDER);
                            }
                        }
                    }
                }

                if (cell.getType() == CellType.MARAUDER && !movedThisTick[y][x]) {
                    Object[] moveData = getRandomNeighboringLand(x, y);

                    if (moveData != null) {
                        Cell nextCell = (Cell) moveData[0];
                        int nx = (Integer) moveData[1];
                        int ny = (Integer) moveData[2];

                        if (Rng.nextInt(100) < 1) {
                            cell.setType(CellType.NONE);
                            continue;
                        }

                        if (nextCell.isOwned()) {
                            Nation victim = nextCell.getOwner();
                            if (victim != null) {
                                if (nextCell.getType() == CellType.VILLAGE) {
                                    nextCell.setType(CellType.NONE);
                                    victim.spendGold(100);
                                } else {
                                    victim.spendGold(10);
                                }
                            }
                            nextCell.setOwner(null);
                        }

                        nextCell.setType(CellType.MARAUDER);
                        cell.setType(CellType.NONE);

                        movedThisTick[ny][nx] = true;
                    }
                }
            }
        }
    }

    private Object[] getRandomNeighboringLand(int cx, int cy) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        if (Rng.nextInt(100) < 40) {
            for (int i = 0; i < directions.length; i++) {
                int index = Rng.nextInt(directions.length);
                int[] temp = directions[i];
                directions[i] = directions[index];
                directions[index] = temp;
            }

            for (int[] dir : directions) {
                int nx = cx + dir[0];
                int ny = cy + dir[1];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    Cell neighbor = cells[ny][nx];
                    CellType type = neighbor.getType();

                    if (neighbor.isGround()
                        && type != CellType.CAMP
                        && type != CellType.CAPITAL
                        && type != CellType.CASTLE
                        && type != CellType.MARAUDER) {

                        return new Object[]{neighbor, nx, ny};
                    }
                }
            }
        }
        return null;
    }

    private void rotateState(int tick) {
        if (tick % 35 == 0) {
            for (Nation nation : nations) {
                if (nation.isVassal()) continue;
                if (nation.getState() == SituationState.UNION) continue;
                int chance = Rng.nextInt(100);

                if (chance < 55) {
                    nation.setState(SituationState.WAR);
                } else if (chance < 80 && nations.size() > 1) {
                    Nation partner = Element.getRandomElement(nations);
                    if (partner != null && partner != nation && !partner.isVassal()) {
                        nation.setState(SituationState.UNION);
                        partner.setState(SituationState.UNION);
                    }
                } else {
                    nation.setState(SituationState.PEACE);
                }
            }
        }
    }

    private boolean isFriendly(Nation a, Nation b) {
        if (a == null || b == null || a == b) return true;
        if (a.getVassals().contains(b) || b.getVassals().contains(a)) {
            return true;
        }
        if (a.getState() == SituationState.UNION && b.getState() == SituationState.UNION) {
            return true;
        }

        return false;
    }

    public void claimCell(Cell cell, Nation newOwner) {
        if (cell == null) return;

        cell.setOwner(newOwner);

        if (newOwner == null) {
            cell.setType(CellType.NONE);
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public List<Nation> getNations() {
        return nations;
    }
}
