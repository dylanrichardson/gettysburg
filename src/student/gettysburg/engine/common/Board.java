package student.gettysburg.engine.common;

import gettysburg.common.Coordinate;
import gettysburg.common.GbgBoard;
import gettysburg.common.GbgUnit;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class Board implements GbgBoard {

    private final Map<GbgUnit, Coordinate> unitPositions = new HashMap<>();


    Collection<GbgUnit> getUnitsAt(Coordinate coordinate) {
        return unitPositions
                .entrySet()
                .stream()
                .filter(entry -> isUnitAtCoordinate(entry, coordinate))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Boolean isUnitAtCoordinate(Map.Entry<GbgUnit, Coordinate> entry, Coordinate where) {
        return entry.getValue().getX() == where.getX() && entry.getValue().getY() == where.getY();
    }

    void moveUnit(GbgUnit unit, Coordinate coordinate) {
        unitPositions.put(unit, coordinate);
    }

    Boolean cellIsOccupied(Coordinate coordinate) {
        return !getUnitsAt(coordinate).isEmpty();
    }

    GbgUnit getUnit(GbgUnit unit) {
        Optional<GbgUnit> unitOptional = unitPositions
                .keySet()
                .stream()
                .filter(u -> u.equals(unit))
                .findFirst();
        if (unitOptional.isPresent())
            return unitOptional.get();
        throw new RuntimeException("Could not find unit: " + unit);
    }

    Coordinate getUnitPosition(GbgUnit unit) {
        return unitPositions.get(unit);
    }

    void placeUnit(GbgUnit unit, Coordinate coordinate) {
        unitPositions.put(new Unit(unit), coordinate);
    }

    void placeUnit(UnitInitializer unitInitializer) {
        placeUnit(unitInitializer.getUnit(), unitInitializer.getWhere());
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

    private Collection<Coordinate> getOccupiedCells() {
        return unitPositions.values().stream().distinct()
                .collect(Collectors.toList());
    }

    private void removeUnitsAt(Coordinate coordinate) {
        getUnitsAt(coordinate).forEach(this::removeUnit);
    }

    private void removeUnit(GbgUnit unit) {
        unitPositions.remove(unit);
    }

    private Boolean cellHasStackedUnits(Coordinate coordinate) {
        return getUnitsAt(coordinate).size() > 1;
    }
}
