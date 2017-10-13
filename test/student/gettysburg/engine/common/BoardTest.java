package student.gettysburg.engine.common;

import org.junit.Before;
import org.junit.Test;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static student.gettysburg.engine.common.Cell.makeCell;
import static student.gettysburg.engine.common.Unit.makeUnit;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    // getUnitsAt

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

    // moveUnit

    @Test
    public void moveUnit() {
        Cell cellA = makeCell(5, 5);
        Cell cellB = makeCell(6, 6);
        Unit unit = makeUnit(UNION, "unit");
        board.moveUnit(unit, cellA);
        board.moveUnit(unit, cellB);

        assertEquals(cellB, board.getUnitPosition(unit));
    }

    // cellIsOccupied

    @Test
    public void cellIsOccupied() {
        Cell cell = makeCell(5, 5);
        board.moveUnit(makeUnit(UNION, "test"), cell);

        assertTrue(board.cellIsOccupied.test(cell));
    }

    // getUnit

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

    // getUnitPosition

    @Test
    public void getUnitPosition() {
        Cell cell = makeCell(5, 5);
        Unit unit = makeUnit(UNION, "test");
        board.moveUnit(unit, cell);

        assertEquals(cell, board.getUnitPosition(unit));
    }

    // placeUnit

    @Test
    public void placeUnit() {
        Cell cell = makeCell(5, 5);
        Unit unit = makeUnit(UNION, "test");
        UnitInitializer unitInitializer = new UnitInitializer(0, cell, unit);
        board.placeUnit(unitInitializer);

        assertEquals(cell, board.getUnitPosition(unit));
    }

    // clear

    @Test
    public void clear() {
        Cell cell = makeCell(5, 5);
        board.moveUnit(makeUnit(UNION, ""), cell);
        board.clear();

        assertTrue(board.getUnitsAt(cell).isEmpty());
    }

    // removeStackedUnits

    @Test
    public void removeStackedUnits() {
        Cell cell = makeCell(5, 5);
        board.moveUnit(makeUnit(UNION, "A"), cell);
        board.moveUnit(makeUnit(UNION, "B"), cell);

        board.removeStackedUnits();

        assertTrue(board.getUnitsAt(cell).isEmpty());
    }

    // getUnitsInBattlePositions

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

    // getAllowedNeighborsAt

    @Test
    public void getOpenNeighborsAll() {
        Unit unitA = makeUnit(UNION, 0, WEST, "A", 0, null, null);
        Unit unitB = makeUnit(CONFEDERATE, 0, WEST, "B", 0, null, null);
        Cell cell = makeCell(5, 5);
        board.moveUnit(unitA, cell);
        board.moveUnit(unitB, makeCell(7, 5));
        Set<Cell> openNeighbors = new HashSet<>(asList(
                makeCell(4, 4),
                makeCell(4, 5),
                makeCell(4, 6),
                makeCell(5, 4),
                makeCell(5, 6),
                makeCell(6, 4),
                makeCell(6, 5),
                makeCell(6, 6)
        ));

        assertEquals(openNeighbors, new HashSet<>(board.getAllowedNeighborsAt(unitA).apply(cell)));
    }

    @Test
    public void getOpenNeighborsSome() {
        Unit unitA = makeUnit(UNION, 0, WEST, "A", 0, null, null);
        Unit unitB = makeUnit(CONFEDERATE, 0, EAST, "B", 0, null, null);
        Cell cell = makeCell(5, 5);
        board.moveUnit(unitA, cell);
        board.moveUnit(unitB, makeCell(6, 5));
        Set<Cell> openNeighbors = new HashSet<>(asList(
                makeCell(4, 4),
                makeCell(4, 5),
                makeCell(4, 6),
                makeCell(5, 4),
                makeCell(5, 6),
                makeCell(6, 4),
                makeCell(6, 6)
        ));

        assertEquals(openNeighbors, new HashSet<>(board.getAllowedNeighborsAt(unitA).apply(cell)));
    }

    @Test
    public void getOpenNeighborsNone() {
        Unit unitA = makeUnit(UNION, 0, WEST, "A", 0, null, null);
        Unit unitB = makeUnit(CONFEDERATE, 0, WEST, "B", 0, null, null);
        Cell cell = makeCell(5, 5);
        board.moveUnit(unitA, cell);
        board.moveUnit(unitB, makeCell(6, 5));

        assertTrue(board.getAllowedNeighborsAt(unitA).apply(cell).isEmpty());
    }

    // hasPath

    @Test
    public void hasPathNotBlocked() {
        Integer movementFactor = 5;
        Unit unit = makeUnit(UNION, 0, EAST,"A", movementFactor, null, null);
        Cell to = makeCell(5, 10);
        Cell from = makeCell(5, 5);
        board.moveUnit(unit, from);

        assertTrue(board.hasPath(unit, to, movementFactor));
    }

    @Test
    public void hasPathNotBlocked2() {
        Integer movementFactor = 5;
        Unit unitA = makeUnit(UNION, 0, SOUTH,"A", movementFactor, null, null);
        Unit unitB = makeUnit(CONFEDERATE, 0, SOUTH,"B", movementFactor, null, null);
        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(5, 7));

        assertTrue(board.hasPath(unitA, makeCell(5, 6), movementFactor));
    }

    @Test
    public void hasPathBlockedByEnemyZOC() {
        Integer movementFactor = 4;
        Unit unitA = makeUnit(UNION, "A");
        unitA.setFacing(EAST);
        Unit unitB = makeUnit(CONFEDERATE, "B");
        unitB.setFacing(EAST);
        Unit unitC = makeUnit(CONFEDERATE, "C");
        unitC.setFacing(EAST);
        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(7, 4));
        board.moveUnit(unitC, makeCell(7, 7));

        assertFalse(board.hasPath(unitA, makeCell(10, 5), movementFactor));
    }

    @Test
    public void hasPathBlockedByFriendlyZOC() {
        Integer movementFactor = 2;
        Unit unitA = makeUnit(UNION, "A");
        unitA.setFacing(EAST);
        Unit unitB = makeUnit(CONFEDERATE, "B");
        unitB.setFacing(NORTH);
        Unit unitC = makeUnit(CONFEDERATE, "C");
        unitC.setFacing(SOUTH);
        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(7, 4));
        board.moveUnit(unitC, makeCell(7, 6));

        assertFalse(board.hasPath(unitA, makeCell(7, 5), movementFactor));
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

    // getRetreatableSquares

    @Test
    public void getRetreatableSquaresSome() {
        Unit unitA = makeUnit(UNION, 1, EAST, "A", 1, null, null);
        Unit unitB = makeUnit(CONFEDERATE, 1, WEST, "B", 1, null, null);
        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(6, 5));

        Set<Cell> cells = new HashSet<>(asList(
                makeCell(4, 4),
                makeCell(4, 5),
                makeCell(4, 6),
                makeCell(6, 4),
                makeCell(6, 6)
        ));
        assertEquals(cells, new HashSet<>(board.getRetreatableSquares(unitA).collect(Collectors.toList())));
    }

    @Test
    public void getRetreatableSquaresNone() {
        Unit unitA = makeUnit(UNION, 1, EAST, "A", 1, null, null);
        Unit unitB = makeUnit(CONFEDERATE, 1, SOUTHEAST, "B", 1, null, null);
        Unit unitC = makeUnit(CONFEDERATE, 1, SOUTHWEST, "C", 1, null, null);
        Unit unitD = makeUnit(CONFEDERATE, 1, NORTH, "D", 1, null, null);
        board.moveUnit(unitA, makeCell(5, 5));
        board.moveUnit(unitB, makeCell(4, 4));
        board.moveUnit(unitC, makeCell(6, 4));
        board.moveUnit(unitD, makeCell(5, 7));

        assertTrue(board.getRetreatableSquares(unitA).count() == 0);
    }

    @Test
    public void getRetreatableSquaresNoMovementFactor() {
        Unit unitA = makeUnit(UNION, 1, EAST, "A", 0, null, null);
        board.moveUnit(unitA, makeCell(5, 5));

        assertTrue(board.getRetreatableSquares(unitA).count() == 0);
    }

}