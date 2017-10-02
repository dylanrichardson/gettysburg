package student.gettysburg.engine.common;

import gettysburg.common.GbgUnit;
import org.junit.Before;
import org.junit.Test;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.EAST;
import static gettysburg.common.Direction.WEST;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.common.Cell.makeCell;
import static student.gettysburg.engine.common.Unit.makeUnit;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void getUnitsAtEmptyCell() {
        Cell cell = makeCell(5, 5);

        assertTrue(board.getUnitsAt(cell).isEmpty());
    }

    @Test
    public void getUnitsAtOccupiedCell() {
        Cell cell = makeCell(5, 5);
        Collection<Unit> units = asList(
                makeUnit(UNION, "A"),
                makeUnit(CONFEDERATE, "B")
        );
        units.forEach(unit -> board.moveUnit(unit, cell));

        assertEquals(new HashSet<>(units), new HashSet<>(board.getUnitsAt(cell)));
    }

    @Test
    public void moveUnit() {
        Cell cellA = makeCell(5, 5);
        Cell cellB = makeCell(6, 6);
        Unit unit = makeUnit(UNION, "unit");
        board.moveUnit(unit, cellA);
        board.moveUnit(unit, cellB);

        assertEquals(cellB, board.getUnitPosition(unit));
    }

    @Test
    public void cellIsOccupied() {
        Cell cell = makeCell(5, 5);
        board.moveUnit(makeUnit(UNION, "test"), cell);

        assertTrue(board.cellIsOccupied.test(cell));
    }

    @Test(expected = RuntimeException.class)
    public void getUnitNotOnBoardThrowsException() {
        assertNull(board.getUnit(makeUnit(UNION, "test")));
    }

    @Test
    public void getUnitOnBoard() {
        Unit unit = makeUnit(UNION, "test");
        board.moveUnit(unit, makeCell(5, 5));

        assertEquals(unit, board.getUnit(unit));
    }

    @Test
    public void getUnitPosition() {
        Cell cell = makeCell(5, 5);
        Unit unit = makeUnit(UNION, "test");
        board.moveUnit(unit, cell);

        assertEquals(cell, board.getUnitPosition(unit));
    }

    @Test
    public void placeUnit() {
        Cell cell = makeCell(5, 5);
        Unit unit = makeUnit(UNION, "test");
        UnitInitializer unitInitializer = new UnitInitializer(0, cell, unit);
        board.placeUnit(unitInitializer);

        assertEquals(cell, board.getUnitPosition(unit));
    }

    @Test
    public void clear() {
        Cell cell = makeCell(5, 5);
        board.moveUnit(makeUnit(UNION, ""), cell);
        board.clear();

        assertTrue(board.getUnitsAt(cell).isEmpty());
    }

    @Test
    public void removeStackedUnits() {
        Cell cell = makeCell(5, 5);
        board.moveUnit(makeUnit(UNION, "A"), cell);
        board.moveUnit(makeUnit(UNION, "B"), cell);

        board.removeStackedUnits();

        assertTrue(board.getUnitsAt(cell).isEmpty());
    }

    @Test
    public void getUnitsInBattle1v1() {
        Unit unitA = makeUnit(UNION, "A");
        unitA.setFacing(EAST);
        Unit unitB = makeUnit(CONFEDERATE, "B");
        unitB.setFacing(EAST);
        Collection<Unit> units = asList(unitA, unitB);

        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(6, 5));

        assertEquals(new HashSet<>(units), new HashSet<>(board.getUnitsInBattlePositions().collect(Collectors.toList())));
    }

    @Test
    public void getUnitsInBattleFacingAway() {
        Unit unitA = makeUnit(UNION, "A");
        unitA.setFacing(WEST);
        Unit unitB = makeUnit(CONFEDERATE, "B");
        unitB.setFacing(EAST);

        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(6, 5));

        assertEquals(0, board.getUnitsInBattlePositions().count());
    }

    @Test
    public void getUnitsInBattle1v2() {
        Unit unitA = makeUnit(UNION, "A");
        unitA.setFacing(EAST);
        Unit unitB = makeUnit(CONFEDERATE, "B");
        Unit unitC = makeUnit(CONFEDERATE, "C");
        Collection<Unit> units = asList(unitA, unitB, unitC);

        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(6, 5));
        board.moveUnit(unitC, makeCell(6, 6));

        assertEquals(new HashSet<>(units), new HashSet<>(board.getUnitsInBattlePositions().collect(Collectors.toList())));
    }

    @Test
    public void getUnitsInBattle1v3() {
        Unit unitA = makeUnit(UNION, "A");
        unitA.setFacing(EAST);
        Unit unitB = makeUnit(CONFEDERATE, "B");
        Unit unitC = makeUnit(CONFEDERATE, "C");
        Unit unitD = makeUnit(CONFEDERATE, "D");
        Collection<Unit> units = asList(unitA, unitB, unitC, unitD);

        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(6, 5));
        board.moveUnit(unitC, makeCell(6, 6));
        board.moveUnit(unitD, makeCell(6, 4));

        assertEquals(new HashSet<>(units), new HashSet<>(board.getUnitsInBattlePositions().collect(Collectors.toList())));
    }

    @Test
    public void getUnitsIn2BattlesNoOverlap() {
        Unit unitA = makeUnit(UNION, "A");
        Unit unitB = makeUnit(UNION, "B");
        unitA.setFacing(EAST);
        unitB.setFacing(EAST);
        Unit unitC = makeUnit(CONFEDERATE, "C");
        Unit unitD = makeUnit(CONFEDERATE, "D");
        Collection<Unit> units = asList(unitA, unitB, unitC, unitD);

        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(10, 10));
        board.moveUnit(unitC, makeCell(6, 5));
        board.moveUnit(unitD, makeCell(11, 10));

        assertEquals(new HashSet<>(units), new HashSet<>(board.getUnitsInBattlePositions().collect(Collectors.toList())));
    }

    @Test
    public void getUnitsIn2BattlesOverlapping() {
        Unit unitA = makeUnit(UNION, "A");
        Unit unitB = makeUnit(UNION, "B");
        unitA.setFacing(EAST);
        unitB.setFacing(EAST);
        Unit unitC = makeUnit(CONFEDERATE, "C");
        Unit unitD = makeUnit(CONFEDERATE, "D");
        Collection<Unit> units = asList(unitA, unitB, unitC, unitD);

        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(5, 6));
        board.moveUnit(unitC, makeCell(6, 5));
        board.moveUnit(unitD, makeCell(6, 6));

        assertEquals(new HashSet<>(units), new HashSet<>(board.getUnitsInBattlePositions().collect(Collectors.toList())));
    }

    // hasPath

    @Test
    public void hasPathUnblocked() {
        Integer movementFactor = 5;
        Unit unit = makeUnit(UNION, 0, EAST,"A", movementFactor, null, null);
        Cell to = makeCell(5, 10);
        Cell from = makeCell(5, 5);
        board.moveUnit(unit, from);

        assertTrue(board.hasPath(unit, to, movementFactor));
    }

    @Test
    public void hasPathBlocked() {
        Integer movementFactor = 5;
        Unit unit = makeUnit(UNION, 0, EAST,"A", movementFactor, null, null);
        Cell to = makeCell(5, 10);
        Cell from = makeCell(5, 5);
        board.moveUnit(unit, from);
        board.moveUnit(makeUnit(CONFEDERATE, "B"), makeCell(7, 4));
        board.moveUnit(makeUnit(CONFEDERATE, "C"), makeCell(7, 7));
        board.moveUnit(unit, from);

        assertFalse(board.hasPath(unit, to, movementFactor));
    }

    @Test
    public void hasIndirectPath() {
        Integer movementFactor = 6;
        Unit unitA = makeUnit(UNION, 0, EAST,"A", movementFactor, null, null);
        Cell to = makeCell(8, 5);
        Cell from = makeCell(5, 5);
        board.moveUnit(unitA, from);
        Unit unitB = makeUnit(CONFEDERATE, 0, WEST, "B", 0, null, null);
        Unit unitC = makeUnit(CONFEDERATE, 0, WEST, "C", 0, null, null);
        board.moveUnit(unitB, makeCell(7, 4));
        board.moveUnit(unitC, makeCell(7, 7));
        board.moveUnit(unitA, from);

        assertTrue(board.hasPath(unitA, to, movementFactor));
    }

    // getOpenNeighbors

    @Test
    public void getNei() {
        Unit unitA = makeUnit(UNION, "A");
        Unit unitB = makeUnit(CONFEDERATE, 0, WEST, "C", 0, null, null);
        Cell cell = makeCell(5, 5);
        board.moveUnit(unitA, cell);
        board.moveUnit(unitB, makeCell(7, 5));

        assertTrue(board.isControlledByUnit(makeCell(6, 4)).test(unitB));
        assertTrue(board.isControlledByUnit(makeCell(6, 5)).test(unitB));
        assertTrue(board.isControlledByUnit(makeCell(6, 6)).test(unitB));
        assertTrue(board.isControlledByUnit(makeCell(7, 5)).test(unitB));

        assertTrue(board.isControlledByArmy(CONFEDERATE).test(makeCell(6, 5)));
        assertTrue(board.isControlledByArmy(CONFEDERATE).test(makeCell(6, 6)));
        assertTrue(board.isControlledByArmy(CONFEDERATE).test(makeCell(6, 4)));
        assertTrue(board.isControlledByArmy(CONFEDERATE).test(makeCell(7, 5)));

        assertEquals(5, board.getOpenNeighbors(unitA).apply(cell).size());

    }

}