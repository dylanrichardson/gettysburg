package student.gettysburg.engine.common;

import gettysburg.common.ArmyID;
import gettysburg.common.GbgBoard;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static student.gettysburg.engine.common.Cell.makeCell;
import static student.gettysburg.engine.common.Direction.NONE;
import static student.gettysburg.engine.common.Unit.makeUnit;

class Board implements GbgBoard {

    private final Map<Unit, Cell> unitPositions = new HashMap<>();


    Collection<GbgUnit> getUnitsAt(Cell cell) {
        return getUnitsInCell(cell)
                .collect(Collectors.toList());
    }

    void moveUnit(Unit unit, Cell cell) {
        unitPositions.put(unit, cell);
    }

    Predicate<Cell> cellIsOccupied = (cell) -> !getUnitsAt(cell).isEmpty();

    GbgUnit getUnit(GbgUnit theUnit) {
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
            if (cellHasStackedUnits(coordinate)) {
                removeUnitsAt(coordinate);
            }
        });
    }

    Stream<GbgUnit> getUnitsInBattlePositions() {
        return unitPositions.keySet().stream()
                .filter(this::isUnitIsAttacking)
                .flatMap(this::getEngagedUnits)
                .distinct();
    }

    void removeUnits(Collection<GbgUnit> units) {
        units.forEach(unit -> removeUnit(makeUnit(unit)));
    }

    Boolean hasPath(Unit unit, Cell destination, Integer length) {
        return PathFinder.find(getUnitPosition(unit), destination, length, getOpenNeighbors(unit));
    }

    // private

    private Stream<GbgUnit> getUnitsInCell(Cell cell) {
        return unitPositions.entrySet().stream()
                .filter(entry -> isUnitInCell(entry, cell))
                .map(Map.Entry::getKey);
    }

    private Boolean isUnitInCell(Map.Entry<Unit, Cell> entry, Cell cell) {
        return entry.getValue().equals(cell);
    }

    private Collection<Cell> getOccupiedCells() {
        return unitPositions.values().stream().distinct().collect(Collectors.toList());
    }

    private void removeUnitsAt(Cell cell) {
        getUnitsAt(cell).forEach(unit -> removeUnit((Unit) unit));
    }

    private void removeUnit(Unit unit) {
        unitPositions.remove(unit);
    }

    private Boolean cellHasStackedUnits(Cell cell) {
        return getUnitsAt(cell).size() > 1;
    }

    private Stream<GbgUnit> getEngagedUnits(Unit unit) {
        return Stream.concat(
                Stream.of(unit),
                getZoneOfControl(unit).flatMap(this::getUnitsInCell));
    }

    private Boolean isUnitIsAttacking(Unit unit) {
        return getZoneOfControl(unit).anyMatch(cell -> cellIsOccupiedBy(cell, unit.getEnemy()));
    }

    private Boolean cellIsOccupiedBy(Cell cell, ArmyID armyID) {
        Optional<GbgUnit> unit = getUnitsAt(cell).stream().findFirst();
        return unit.isPresent() && unit.get().getArmy() == armyID && cellIsOccupied.test(cell);
    }

    Stream<Cell> getZoneOfControl(Unit unit) {
        Direction direction = unit.getDirection();
        Cell cell = getUnitPosition(unit);
        return IntStream.range(-1, 2)
                .mapToObj(direction::rotate)
                .map(cell::getAdjacent);
    }

    Function<Cell, Collection<Cell>> getOpenNeighbors(Unit unit) {
        return (cell) -> Direction.all()
                .map(cell::getAdjacent)
                .filter(isControlledByArmy(unit.getEnemy()).negate())
                .collect(Collectors.toList());
    }

    // within any of army's units' zone of control
    Predicate<Cell> isControlledByArmy(ArmyID armyID) {
        // check each direction
        return (cell) -> Stream.concat(Direction.all(), Stream.of(NONE)).anyMatch(direction ->
                // check neighboring friendly units if in control
                getUnitsAt(cell.getAdjacent(direction))
                        .stream()
                        .filter(unit -> unit.getArmy() == armyID)
                        .map(Unit::makeUnit)
                        .anyMatch(isControlledByUnit(cell)));
    }

    Predicate<Unit> isControlledByUnit(Cell theCell) {
        // check if in cell or cell is in zone of control
        return unit -> getUnitPosition(unit).equals(theCell)
                || getZoneOfControl(unit).filter(cell -> cell.equals(theCell)).count() > 0;
    }
}
