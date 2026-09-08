package com.aoc;

import com.aoc.cell.Cell;
import com.aoc.cell.CellType;
import com.aoc.config.Config;
import com.aoc.diplomacy.DiplomacyManager;
import com.aoc.economy.EconomyManager;
import com.aoc.map.MapGenerator;
import com.aoc.nation.Nation;
import com.aoc.nation.NationGenerator;
import com.aoc.nation.SituationState;
import com.aoc.render.WorldRenderer;
import com.aoc.util.Element;
import com.aoc.util.Rng;
import com.aoc.util.Time;
import com.googlecode.lanterna.screen.Screen;

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
        this.cells = new Cell[this.height][this.width];
        this.nations = new ArrayList<>();
        this.generator = new MapGenerator();
        this.renderer = new WorldRenderer(this.width, this.height);
    }

    public void init() {
        this.generator.generateTerrain(this, this.cells, this.width, this.height);
        fillNations();
        this.generator.spawnMarauderCamp(this, this.cells, this.width, this.height);
        this.generator.spawnCapitals(this, this.cells, this.nations, this.width, this.height);
    }

    public void generateGrid(Screen screen) {
        this.renderer.render(this.cells, screen);
    }

    private void fillNations() {
        this.nations.clear();
        NationGenerator.generate(this.nations);
    }

    public void update() {
        int tick = this.time.getCurrentTick();

        EconomyManager.collectEconomy(nations);

        if (tick % 4 == 0) {
            for (Nation nation : this.nations) {
                nation.strengthenFromGold();
            }
        }

        DiplomacyManager.update(this.nations);
        DiplomacyManager.rotateState(tick, this.nations);
        processExpansionAndSailing(tick);
        checkCapitals();
        updateMarauders();
        limitShips();
    }

    private void processExpansionAndSailing(int tick) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Cell cell = this.cells[y][x];
                Nation owner = cell.getOwner();
                if (owner == null) continue;

                if ((cell.isCapital() || cell.isLand()) && Rng.nextInt(100) < 15) {
                    tryExpand(x, y, owner);
                }

                if (cell.isShip() && Rng.nextInt(100) < 20) {
                    trySail(x, y, owner);
                }
            }
        }

        if (tick % 10 == 0) {
            for (Nation nation : this.nations) {
                tryBuilding(nation);
            }
        }
    }

    private void tryExpand(int x, int y, Nation attacker) {
        if (attacker.getPower() < 10) return;

        int nx = x + Rng.nextInt(3) - 1;
        int ny = y + Rng.nextInt(3) - 1;
        if (!inBounds(nx, ny)) return;

        Cell target = this.cells[ny][nx];
        long cost = 12;
        int chance = (int) (8 + attacker.getPower() / 15);

        if (attacker.getState() == SituationState.WAR) {
            chance += 15;
            cost *= 2;
        }

        if (attacker.getGold() < cost * 2) return;

        if (target.isGround() && !target.isOwned()) {
            if (attacker.spendGold(cost)) {
                claim(target, attacker, CellType.LAND);
                attacker.addPower(1);
            }
            return;
        }

        if (target.isOwned() && target.getOwner() != attacker && !target.isShip()) {
            if (isFriendly(attacker, target.getOwner())) return;

            if (attacker.getState() == SituationState.WAR) {
                if (Rng.nextInt(100) < chance && attacker.spendGold(cost)) {
                    handleCapitalCapture(target, attacker);
                    claim(target, attacker, target.isCapital() ? CellType.CAPITAL : CellType.LAND);
                    attacker.addPower(1);
                }
            } else if (Rng.nextInt(100) < 2) {
                attacker.setState(SituationState.WAR);
                target.getOwner().setState(SituationState.WAR);
            }
            return;
        }

        if (target.isWater() && !target.isOwned()) {
            Cell current = this.cells[y][x];
            if (current.getType() == CellType.PORT
                && Rng.nextInt(100) < 4
                && attacker.spendGold(8)
                && countShips(attacker) < 7) {
                claim(target, attacker, CellType.SHIP);
            }
        }
    }

    private void trySail(int x, int y, Nation attacker) {
        int nx = x + Rng.nextInt(3) - 1;
        int ny = y + Rng.nextInt(3) - 1;
        if (!inBounds(nx, ny)) return;

        Cell target = this.cells[ny][nx];
        Cell current = this.cells[y][x];

        if ((target.isWater() || target.isGround()) && !target.isOwned()) {
            if (attacker.spendGold(5)) {
                clearCell(current);
                CellType newType = target.isWater() && countShips(attacker) < 7
                    ? CellType.SHIP
                    : CellType.LAND;
                claim(target, attacker, newType);
            }
            return;
        }

        if (target.isOwned()
            && target.getOwner() != attacker
            && !target.isShip()
            && !isFriendly(attacker, target.getOwner())
            && attacker.getState() == SituationState.WAR
            && Rng.nextInt(100) < 25
            && attacker.spendGold(30)) {

            clearCell(current);
            claim(target, attacker, target.isCapital() ? CellType.CAPITAL : CellType.LAND);
            attacker.addPower(2);
        }
    }

    private void tryBuilding(Nation builder) {
        List<Cell> candidates = new ArrayList<>();
        for (Cell cell : builder.getOwnedCells()) {
            if (cell.getType() == CellType.LAND
                || cell.getType() == CellType.VILLAGE
                || cell.getType() == CellType.TOWN) {
                candidates.add(cell);
            }
        }
        if (candidates.isEmpty()) return;

        Cell cell = Element.getRandomElement(candidates);

        if (cell.getType() == CellType.LAND) {
            int[] pos = findCellPosition(cell);
            if (pos != null && isCoastal(pos[0], pos[1])) {
                if (builder.getGold() >= 450
                    && countBuildings(builder, CellType.PORT) < 8
                    && Rng.nextInt(100) < 35) {
                    builder.spendGold(450);
                    cell.setType(CellType.PORT);
                    return;
                }
            }
        }

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

        if (builder.getGold() < cost || countBuildings(builder, next) >= limit) return;

        builder.spendGold(cost);
        cell.setType(next);

        if (next == CellType.CASTLE) {
            builder.addPower(25);
        }
    }

    private void checkCapitals() {
        Iterator<Nation> it = this.nations.iterator();
        while (it.hasNext()) {
            Nation nation = it.next();

            boolean hasLand = false;
            boolean hasCapital = false;

            for (Cell cell : nation.getOwnedCells()) {
                if (cell.isLand() || cell.isCapital()) hasLand = true;
                if (cell.isCapital()) hasCapital = true;
            }

            if (!hasLand || (!hasCapital && !nation.isVassal())) {
                eliminateNation(nation);
                it.remove();
            }
        }
    }

    private void eliminateNation(Nation nation) {
        if (nation.isVassal()) {
            nation.getMaster().removeVassal(nation);
        }
        for (Nation vassal : new ArrayList<>(nation.getVassals())) {
            nation.removeVassal(vassal);
        }

        List<Cell> owned = new ArrayList<>(nation.getOwnedCells());
        for (Cell cell : owned) {
            clearCell(cell);
        }

        if (Rng.nextInt(100) < 75 && !owned.isEmpty()) {
            int camps = Math.min(Rng.nextInt(7) + 1, owned.size());
            for (int i = 0; i < camps; i++) {
                int idx = Rng.nextInt(owned.size());
                owned.get(idx).setType(CellType.CAMP);
                owned.remove(idx);
            }
        }
    }

    private void updateMarauders() {
        boolean[][] moved = new boolean[this.height][this.width];

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                Cell cell = this.cells[y][x];

                if (cell.getType() == CellType.CAMP && Rng.nextInt(100) < 20) {
                    Neighbor neighbor = getRandomNeighborLand(x, y);
                    if (neighbor != null
                        && neighbor.cell.getType() == CellType.NONE
                        && !neighbor.cell.isOwned()) {
                        neighbor.cell.setType(CellType.MARAUDER);
                    }
                }

                if (cell.getType() == CellType.MARAUDER && !moved[y][x]) {
                    Neighbor neighbor = getRandomNeighborLand(x, y);
                    if (neighbor == null) continue;
                    if (Rng.nextInt(100) < 1) {
                        cell.setType(CellType.NONE);
                        continue;
                    }

                    Cell next = neighbor.cell;

                    if (next.isOwned()) {
                        Nation victim = next.getOwner();
                        if (victim != null) {
                            if (next.getType() == CellType.VILLAGE) {
                                next.setType(CellType.NONE);
                                victim.spendGold(100);
                            } else {
                                victim.spendGold(10);
                            }
                        }
                        next.setOwner(null);
                    }

                    next.setType(CellType.MARAUDER);
                    cell.setType(CellType.NONE);
                    moved[neighbor.y][neighbor.x] = true;
                }
            }
        }
    }

    private boolean isFriendly(Nation a, Nation b) {
        if (a == null || b == null || a == b) return true;
        if (a.getVassals().contains(b) || b.getVassals().contains(a)) return true;
        return a.getState() == SituationState.UNION && b.getState() == SituationState.UNION;
    }

    private void handleCapitalCapture(Cell target, Nation attacker) {
        if (!target.isCapital()) return;
        Nation defender = target.getOwner();
        if (defender != null && defender != attacker && Rng.nextInt(100) < 70) {
            DiplomacyManager.makeVassal(attacker, defender);
        }
    }

    private void limitShips() {
        for (Nation nation : this.nations) {
            int ships = countShips(nation);
            if (ships <= 7) continue;

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

    public void claimCell(Cell cell, Nation newOwner) {
        if (cell == null) return;
        cell.setOwner(newOwner);
        if (newOwner == null) {
            cell.setType(CellType.NONE);
        }
    }

    private void claim(Cell cell, Nation owner, CellType type) {
        if (cell == null) return;
        cell.setOwner(owner);
        if (type != null) {
            cell.setType(type);
        }
    }

    private void clearCell(Cell cell) {
        if (cell == null) return;
        cell.setOwner(null);
        cell.setType(CellType.NONE);
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < this.width && y >= 0 && y < this.height;
    }

    private boolean isCoastal(int x, int y) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (inBounds(nx, ny) && this.cells[ny][nx].isWater()) {
                return true;
            }
        }
        return false;
    }

    private int countShips(Nation nation) {
        int count = 0;
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.isShip()) count++;
        }
        return count;
    }

    private int countBuildings(Nation nation, CellType type) {
        int count = 0;
        for (Cell cell : nation.getOwnedCells()) {
            if (cell.getType() == type) count++;
        }
        return count;
    }

    private int[] findCellPosition(Cell cell) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.cells[y][x] == cell) {
                    return new int[]{x, y};
                }
            }
        }
        return null;
    }

    private Neighbor getRandomNeighborLand(int cx, int cy) {
        if (Rng.nextInt(100) >= 40) return null;

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int i = 0; i < directions.length; i++) {
            int j = Rng.nextInt(directions.length);
            int[] tmp = directions[i];
            directions[i] = directions[j];
            directions[j] = tmp;
        }

        for (int[] dir : directions) {
            int nx = cx + dir[0];
            int ny = cy + dir[1];
            if (!inBounds(nx, ny)) continue;

            Cell neighbor = this.cells[ny][nx];
            CellType type = neighbor.getType();

            if (neighbor.isGround()
                && type != CellType.CAMP
                && type != CellType.CAPITAL
                && type != CellType.CASTLE
                && type != CellType.MARAUDER) {
                return new Neighbor(neighbor, nx, ny);
            }
        }
        return null;
    }

    private record Neighbor(Cell cell, int x, int y) {
    }

    public Cell[][] getCells() {
        return this.cells;
    }

    public List<Nation> getNations() {
        return this.nations;
    }
}
