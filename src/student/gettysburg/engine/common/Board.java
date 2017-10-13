package student.gettysburg.engine.common;

import gettysburg.common.ArmyID;
import gettysburg.common.GbgBoard;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static student.gettysburg.engine.common.Cell.makeCell;
import static student.gettysburg.engine.common.Unit.makeUnit;

class Board implements GbgBoard {

    private final Map<Unit, Cell> unitPositions = new HashMap<>();


    Collection<GbgUnit> getUnitsAt(Cell cell) {
        return getUnitsInCell(cell)
                .collect(Collectors.toList());
    }

    private Stream<GbgUnit> getUnitsInCell(Cell cell) {
        return unitPositions.entrySet().stream()
                .filter(entry -> isUnitInCell(entry, cell))
                .map(Map.Entry::getKey);
    }

    private Boolean isUnitInCell(Map.Entry<Unit, Cell> entry, Cell cell) {
        return entry.getValue().equals(cell);
    }

    void moveUnit(Unit unit, Cell cell) {
        unitPositions.put(unit, cell);
    }

    final Predicate<Cell> cellIsOccupied = cell -> !getUnitsAt(cell).isEmpty();

    GbgUnit getUnit(GbgUnit theUnit) {
        // find unit on board with same army and leader
        Optional<GbgUnit> unitOptional = unitPositions.keySet().stream()
                .filter(unit -> unit.equals(theUnit))
                .map(Unit::toOriginal)
                .findFirst();
        if (unitOptional.isPresent())
            return unitOptional.get();
        throw new RuntimeException("Could not find unit: " + theUnit);
    }

    Cell getUnitPosition(Unit unit) {
        return unitPositions.get(unit);
    }

    void placeUnit(UnitInitializer unitInitializer) {
        moveUnit(makeUnit(unitInitializer.getUnit()), makeCell(unitInitializer.getWhere()));
    }

    void clear() {
        unitPositions.clear();
    }

    void removeStackedUnits() {
        getOccupiedCells().forEach(coordinate -> {
            if (cellHasStackedUnits(coordinate))
                removeUnitsAt(coordinate);
        });
    }



    Stream<GbgUnit> getUnitsInBattlePositions() {
        // for each attacker get corresponding defenders, remove duplicates
        return unitPositions.keySet().stream()
                .filter(this::isUnitAttacking)
                .flatMap(this::getEngagedUnits)
                .distinct();
    }

    private Boolean isUnitAttacking(Unit unit) {
        return isUnitAttackingFrom(unit, getUnitPosition(unit));
    }

    private Boolean isUnitAttackingFrom(Unit unit, Cell cell) {
        // check if enemy in the unit's ZOC
        Stream<Cell> z = getZoneOfControlAt(unit, cell);
        return z.anyMatch(cellIsOccupiedBy(unit.getEnemy()));
    }

    private Stream<GbgUnit> getEngagedUnits(Unit unit) {
        // combine the attacker with it's defenders
        return Stream.concat(Stream.of(unit), getDefenders(unit));
    }

    private Stream<GbgUnit> getDefenders(Unit unit) {
        // get the defenders of the attacking unit
        return getZoneOfControl(unit)
                .flatMap(this::getUnitsInCell)
                .filter(isOnArmy(unit.getEnemy()));
    }



    void removeUnits(Collection<GbgUnit> units) {
        units.forEach(unit -> removeUnit(makeUnit(unit)));
    }

    Boolean hasPath(Unit unit, Cell destination, Integer length) {
        return PathFinder.find(getUnitPosition(unit), destination, length, getAllowedNeighborsAt(unit));
    }



    Stream<Cell> getRetreatableSquares(Unit unit) {
        if (unit.getMovementFactor() == 0)
            return Stream.of();
        // get
        return getNeighborsAt.apply(getUnitPosition(unit))
                .filter(isControlledByArmy(unit.getEnemy())
                        .or(cellIsOccupied)
                        .negate());
    }

    Function<Cell, Collection<Cell>> getAllowedNeighborsAt(Unit unit) {
        return cell -> {
            // check if unable to move
            if (isUnableToMoveFrom(unit, cell))
                return emptyList();
            // get open neighbors not occupied by enemy
            return getNeighborsAt.apply(cell)
                    .filter(cellIsOccupiedBy(unit.getEnemy()).negate())
                    .collect(Collectors.toList());
        };
    }

    private Function<Cell, Stream<Cell>> getNeighborsAt = cell -> Direction.all().map(cell::getAdjacent);

    // private

    private Collection<Cell> getOccupiedCells() {
        return unitPositions.values().stream().distinct().collect(Collectors.toList());
    }

    private void removeUnitsAt(Cell cell) {
        getUnitsAt(cell).forEach(unit -> removeUnit((Unit) unit));
    }

    void removeUnit(Unit unit) {
        unitPositions.remove(unit);
    }

    private Boolean cellHasStackedUnits(Cell cell) {
        return getUnitsAt(cell).size() > 1;
    }

    private Predicate<GbgUnit> isOnArmy(ArmyID army) {
        return unit -> unit.getArmy() == army;
    }

    private Predicate<Cell> cellIsOccupiedBy(ArmyID armyID) {
        return cell -> {
            Optional<GbgUnit> unit = getUnitsAt(cell).stream().findFirst();
            return unit.isPresent() && unit.get().getArmy() == armyID && cellIsOccupied.test(cell);
        };
    }

    private Stream<Cell> getZoneOfControl(Unit unit) {
        return getZoneOfControlAt(unit, getUnitPosition(unit));
    }

    private Stream<Cell> getZoneOfControlAt(Unit unit, Cell cell) {
        Direction direction = unit.getDirection();
        // 3 cells where unit is facing
        return IntStream.range(-1, 2)
                .mapToObj(direction::rotate)
                .map(cell::getAdjacent);
    }

    private Boolean isUnableToMoveFrom(Unit unit, Cell cell) {
        // unit in enemy ZOC or enemy in unit's ZOC
        return isControlledByArmy(unit.getEnemy()).test(cell)
                || isUnitAttackingFrom(unit, cell);
    }

    // within any of army's units' zone of control
    private Predicate<Cell> isControlledByArmy(ArmyID armyID) {
        // check each direction
        return cell -> Direction.allAndNone().anyMatch(direction ->
                // check neighboring friendly units if in control
                getUnitsAt(cell.getAdjacent(direction))
                        .stream()
                        .filter(unit -> unit.getArmy() == armyID)
                        .map(Unit::makeUnit)
                        .anyMatch(isControlledByUnit(cell)));
    }

    private Predicate<Unit> isControlledByUnit(Cell theCell) {
        // check if in cell or cell is in zone of control
        return unit -> getUnitPosition(unit).equals(theCell)
                || getZoneOfControl(unit).filter(cell -> cell.equals(theCell)).count() > 0;
    }
}
