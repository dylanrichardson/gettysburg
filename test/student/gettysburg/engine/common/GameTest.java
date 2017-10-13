package student.gettysburg.engine.common;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.BattleResult.*;
import static gettysburg.common.Direction.*;
import static gettysburg.common.GbgGameStep.*;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.GettysburgFactory.makeTestGame;
import static student.gettysburg.engine.common.Battle.makeBattle;
import static student.gettysburg.engine.common.Cell.makeCell;
import static student.gettysburg.engine.common.Unit.makeUnit;
import static student.gettysburg.engine.utility.configure.BattleOrder.getConfederateBattleOrder;
import static student.gettysburg.engine.utility.configure.BattleOrder.getUnionBattleOrder;

public class GameTest {

    private static final GbgUnit GAMBLE = getUnionBattleOrder()[0].getUnit();
    private static final GbgUnit DEVIN = getUnionBattleOrder()[1].getUnit();
    private static final GbgUnit HETH = getConfederateBattleOrder()[0].getUnit();

    private GbgGame game;
    private TestGbgGame testGame;

    @Before
    public void setup() {
        game = testGame = makeTestGame();
    }

    @Test
    public void newGameStartsOnTurnOne() {
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    public void newGameStartsOnUnionMove() {
        assertEquals(UMOVE, game.getCurrentStep());
    }

    // whereIsUnit

    @Test
    public void newGameStartsWithGambleAtCorrectSquare() {
        assertEquals(makeCell(11, 11), game.whereIsUnit(GAMBLE));
    }

    @Test
    public void newGameStartsWithDevinAtCorrectSquare() {
        assertEquals(makeCell(13, 9), game.whereIsUnit(DEVIN));
    }

    @Test
    public void newGameStartsWithHethAtCorrectSquare() {
        assertEquals(makeCell(8, 8), game.whereIsUnit(HETH));
    }

    // getUnitFacing

    @Test
    public void getUnitFacingWest() {
        assertEquals(WEST, game.getUnitFacing(GAMBLE));
    }

    @Test
    public void getUnitFacingSouth() {
        assertEquals(SOUTH, game.getUnitFacing(DEVIN));
    }

    @Test
    public void getUnitFacingEast() {
        assertEquals(EAST, game.getUnitFacing(HETH));
    }

    // getUnitsAt

    @Test
    public void getUnitsAtCoordinateWithOneUnit() {
        GbgUnit unit = game.getUnitsAt(makeCell(11, 11)).iterator().next();
        assertEquals(GAMBLE, unit);
    }

    @Test
    public void getUnitsAtCoordinateWithNoUnits() {
        assertNull(game.getUnitsAt(makeCell(11, 12)));
    }

    // moveUnit

    @Test
    public void moveUnitWhenTurnToValidSquare() {
        Coordinate to = makeCell(11, 12);
        game.moveUnit(GAMBLE, makeCell(11, 11), to);
        assertEquals(to, game.whereIsUnit(GAMBLE));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenNotTurn() {
        game.moveUnit(HETH, makeCell(8, 8), makeCell(8, 9));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnFromInvalidSquare() {
        game.moveUnit(GAMBLE, makeCell(11, 12), makeCell(11, 13));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnToOutOfReachSquare() {
        game.moveUnit(GAMBLE, makeCell(11, 11), makeCell(11, 16));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnToOccupiedSquare() {
        game.moveUnit(GAMBLE, makeCell(11, 11), makeCell(11, 11));
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void attemptToMoveUnitTwiceInOneTurn() {
        game.moveUnit(GAMBLE, makeCoordinate(11, 11), makeCoordinate(12, 10));
        game.moveUnit(GAMBLE, makeCoordinate(12, 10), makeCoordinate(12, 11));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitBlocked() {
        GbgUnit unitA = makeUnit(UNION, 0, EAST, "A", 3, null, null);
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(makeUnit(CONFEDERATE, "B"), 7, 4, WEST);
        testGame.putUnitAt(makeUnit(CONFEDERATE, "C"), 7, 7, WEST);
        game.moveUnit(unitA, makeCoordinate(5, 5), makeCoordinate(8, 5));
    }

    @Test
    public void moveUnitIndirectPath() {
        GbgUnit unitA = makeUnit(UNION, 0, EAST, "A", 6, null, null);
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(makeUnit(CONFEDERATE, "B"), 7, 4, WEST);
        testGame.putUnitAt(makeUnit(CONFEDERATE, "C"), 7, 7, WEST);
        Coordinate to = makeCoordinate(5, 8);
        game.moveUnit(unitA, makeCoordinate(5, 5), to);
        assertEquals(to, game.whereIsUnit("A", UNION));
    }

    // setUnitFacing

    @Test
    public void setUnitFacingDuringMove() {
        game.setUnitFacing(DEVIN, NORTH);
        assertEquals(NORTH, game.getUnitFacing(DEVIN));
        assertEquals(SOUTH, DEVIN.getFacing());
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void setUnitFacingNotDuringMove() {
        game.setUnitFacing(HETH, NORTH);
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void devinTriesToSetFacingTwice() {
        game.setUnitFacing(DEVIN, NORTHWEST);
        game.moveUnit(DEVIN, makeCoordinate(13, 9), makeCoordinate(13, 10));
        game.setUnitFacing(DEVIN, SOUTHEAST);
    }

    // placeReinforcements

    @Test
    public void placeReinforcements() {
        testGame.setGameTurn(2);
        testGame.setGameStep(CBATTLE);
        game.endStep();
        assertEquals(makeCell(14, 28), game.whereIsUnit("Howard", UNION));
        assertEquals(makeCell(14, 28), game.whereIsUnit("von Steinwehr", UNION));
        assertEquals(makeCell(14, 28), game.whereIsUnit("Schurz", UNION));
        assertEquals(makeCell(7, 28), game.whereIsUnit("Barlow", UNION));
    }

    // getBattlesToResolve

    @Test
    public void getBattlesToResolve() {
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.clearBoard();
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        testGame.setGameStep(UBATTLE);
        Collection<BattleDescriptor> battlesToResolve = singletonList(
                makeBattle(singletonList(unitA), singletonList(unitB))
        );

        assertEquals(battlesToResolve, game.getBattlesToResolve());
    }

    @Test
    public void getBattlesToResolveAfterBattling() {
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(UNION, "B");
        GbgUnit unitC = makeUnit(CONFEDERATE, "C");
        GbgUnit unitD = makeUnit(CONFEDERATE, "D");
        testGame.clearBoard();
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 10, 10, EAST);
        testGame.putUnitAt(unitC, 6, 5, WEST);
        testGame.putUnitAt(unitD, 11, 10, WEST);
        testGame.setGameStep(UBATTLE);
        game.resolveBattle(makeBattle(singletonList(unitA), singletonList(unitC)));


        Collection<BattleDescriptor> battlesToResolve = singletonList(
                makeBattle(singletonList(unitB), singletonList(unitD))
        );

        assertEquals(battlesToResolve, game.getBattlesToResolve());
    }

    // endStep

    @Test(expected = GbgInvalidActionException.class)
    public void endStepWithBattlesToResolve() {
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.clearBoard();
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        testGame.setGameStep(UMOVE);
        game.endStep();
        game.endStep();
    }

    // resolveBattle

    @Test(expected = GbgInvalidActionException.class)
    public void resolveBattleNoBattles() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        BattleDescriptor battle = makeBattle(singletonList(unitA), singletonList(unitB));

        game.resolveBattle(battle);
    }

    @Test(expected = GbgInvalidActionException.class)
    public void resolveBattleInvalidAttacker() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> units = singletonList(unitB);
        BattleDescriptor battle = makeBattle(units, units);

        game.resolveBattle(battle);
    }

    @Test(expected = GbgInvalidActionException.class)
    public void resolveBattleInvalidDefender() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> units = singletonList(unitA);
        BattleDescriptor battle = makeBattle(units, units);

        game.resolveBattle(battle);
    }

    @Test
    public void resolveBattleAELIM() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> attackingUnits = singletonList(unitA);
        Collection<GbgUnit> defendingUnits = singletonList(unitB);
        BattleDescriptor battle = makeBattle(attackingUnits, defendingUnits);
        testGame.setBattleResults(singletonList(AELIM));
        BattleResolution resolution = game.resolveBattle(battle);

        assertEquals(AELIM, resolution.getBattleResult());
        assertNull(game.whereIsUnit(unitA));
        assertEquals(makeCell(6, 5), game.whereIsUnit(unitB));
    }

    @Test
    public void resolveBattleDELIM() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> attackingUnits = singletonList(unitA);
        Collection<GbgUnit> defendingUnits = singletonList(unitB);
        BattleDescriptor battle = makeBattle(attackingUnits, defendingUnits);
        testGame.setBattleResults(singletonList(DELIM));
        BattleResolution resolution = game.resolveBattle(battle);

        assertEquals(DELIM, resolution.getBattleResult());
        assertEquals(makeCell(5, 5), game.whereIsUnit(unitA));
        assertNull(game.whereIsUnit(unitB));
    }

    @Test
    public void resolveBattleEXCHANGEHQ() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> attackingUnits = singletonList(unitA);
        Collection<GbgUnit> defendingUnits = singletonList(unitB);
        BattleDescriptor battle = makeBattle(attackingUnits, defendingUnits);
        testGame.setBattleResults(singletonList(EXCHANGE));
        BattleResolution resolution = game.resolveBattle(battle);

        assertEquals(EXCHANGE, resolution.getBattleResult());
        assertEquals(makeCell(5, 5), game.whereIsUnit(unitA));
        assertEquals(makeCell(6, 5), game.whereIsUnit(unitB));
    }

    @Test
    public void resolveBattleEXCHANGE() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, 1, EAST, "A", 0, null, null);
        GbgUnit unitB = makeUnit(CONFEDERATE, 1, EAST, "B", 0, null, null);
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> attackingUnits = singletonList(unitA);
        Collection<GbgUnit> defendingUnits = singletonList(unitB);
        BattleDescriptor battle = makeBattle(attackingUnits, defendingUnits);
        testGame.setBattleResults(singletonList(EXCHANGE));
        BattleResolution resolution = game.resolveBattle(battle);

        assertEquals(EXCHANGE, resolution.getBattleResult());
        assertNull(game.whereIsUnit(unitA));
        assertNull(game.whereIsUnit(unitB));
    }

    @Test
    public void resolveBattleABACK() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, 1, EAST, "A", 1, null, null);
        GbgUnit unitB = makeUnit(CONFEDERATE, 1, EAST, "B", 1, null, null);
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> attackingUnits = singletonList(unitA);
        Collection<GbgUnit> defendingUnits = singletonList(unitB);
        BattleDescriptor battle = makeBattle(attackingUnits, defendingUnits);
        testGame.setBattleResults(singletonList(ABACK));
        BattleResolution resolution = game.resolveBattle(battle);

        assertEquals(ABACK, resolution.getBattleResult());
        assertNotEquals(makeCell(5, 5), game.whereIsUnit(unitA));
        assertNotNull(game.whereIsUnit(unitA));
        assertEquals(makeCell(6, 5), game.whereIsUnit(unitB));
    }

    @Test
    public void resolveBattleDBACK() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, 1, EAST, "A", 1, null, null);
        GbgUnit unitB = makeUnit(CONFEDERATE, 1, EAST, "B", 1, null, null);
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<GbgUnit> attackingUnits = singletonList(unitA);
        Collection<GbgUnit> defendingUnits = singletonList(unitB);
        BattleDescriptor battle = makeBattle(attackingUnits, defendingUnits);
        testGame.setBattleResults(singletonList(DBACK));
        BattleResolution resolution = game.resolveBattle(battle);

        assertEquals(DBACK, resolution.getBattleResult());
        assertEquals(makeCell(5, 5), game.whereIsUnit(unitA));
        assertNotEquals(makeCell(6, 5), game.whereIsUnit(unitB));
        assertNotNull(game.whereIsUnit(unitB));
    }

    @Test
    public void resolveBattleRetreatNoSquares() {
        testGame.clearBoard();
        testGame.setGameStep(UBATTLE);
        GbgUnit unitA = makeUnit(UNION, 0, EAST, "A", 1, null, null);
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        GbgUnit unitC = makeUnit(CONFEDERATE, "C");
        GbgUnit unitD = makeUnit(CONFEDERATE, "D");
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 4, 4, SOUTHEAST);
        testGame.putUnitAt(unitC, 6, 4, SOUTHWEST);
        testGame.putUnitAt(unitD, 5, 7, NORTH);
        Collection<GbgUnit> attackingUnits = singletonList(unitA);
        Collection<GbgUnit> defendingUnits = singletonList(unitB);
        BattleDescriptor battle = makeBattle(attackingUnits, defendingUnits);
        testGame.setBattleResults(singletonList(ABACK));
        BattleResolution resolution = game.resolveBattle(battle);

        assertEquals(ABACK, resolution.getBattleResult());
        assertNull(game.whereIsUnit(unitA));
        assertEquals(makeCell(4, 4), game.whereIsUnit(unitB));
        assertEquals(makeCell(6, 4), game.whereIsUnit(unitC));
        assertEquals(makeCell(5, 7), game.whereIsUnit(unitD));
    }

}