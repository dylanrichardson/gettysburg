package student.gettysburg.engine.common;

import gettysburg.common.*;
import gettysburg.common.Coordinate;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.*;
import static gettysburg.common.GbgGameStep.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static student.gettysburg.engine.GettysburgFactory.makeTestGame;
import static student.gettysburg.engine.common.Coordinate.makeCoordinate;
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
        assertEquals(makeCoordinate(11, 11), game.whereIsUnit(GAMBLE));
    }

    @Test
    public void newGameStartsWithDevinAtCorrectSquare() {
        assertEquals(makeCoordinate(13, 9), game.whereIsUnit(DEVIN));
    }

    @Test
    public void newGameStartsWithHethAtCorrectSquare() {
        assertEquals(makeCoordinate(8, 8), game.whereIsUnit(HETH));
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
        GbgUnit unit = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
        assertEquals(GAMBLE, unit);
    }

    @Test
    public void getUnitsAtCoordinateWithNoUnits() {
        assertNull(game.getUnitsAt(makeCoordinate(11, 12)));
    }

    // moveUnit

    @Test
    public void moveUnitWhenTurnToValidSquare() {
        Coordinate to = makeCoordinate(11, 12);
        game.moveUnit(GAMBLE, makeCoordinate(11, 11), to);
        assertEquals(to, game.whereIsUnit(GAMBLE));
    }

    @Test(expected = GbgInvalidActionException.class)
    public void moveUnitWhenNotTurn() {
        game.moveUnit(HETH, makeCoordinate(8, 8), makeCoordinate(8, 9));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnFromInvalidSquare() {
        game.moveUnit(GAMBLE, makeCoordinate(11, 12), makeCoordinate(11, 13));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnToOutOfReachSquare() {
        game.moveUnit(GAMBLE, makeCoordinate(11, 11), makeCoordinate(11, 16));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnToOccupiedSquare() {
        game.moveUnit(GAMBLE, makeCoordinate(11, 11), makeCoordinate(11, 11));
    }

    // setUnitFacing

    @Test
    public void setUnitFacingDuringMove() {
        game.setUnitFacing(DEVIN, NORTH);
        assertEquals(NORTH, game.getUnitFacing(DEVIN));
        assertEquals(SOUTH, DEVIN.getFacing());
    }

    @Test(expected = GbgInvalidActionException.class)
    public void setUnitFacingNotDuringMove() {
        game.setUnitFacing(HETH, NORTH);
    }

    // placeReinforcements

    @Test
    public void placeReinforcements() {
        testGame.setGameTurn(2);
        testGame.setGameStep(CBATTLE);
        game.endStep();
        assertEquals(makeCoordinate(14, 28), game.whereIsUnit("Howard", UNION));
        assertEquals(makeCoordinate(14, 28), game.whereIsUnit("von Steinwehr", UNION));
        assertEquals(makeCoordinate(14, 28), game.whereIsUnit("Schurz", UNION));
        assertEquals(makeCoordinate(7, 28), game.whereIsUnit("Barlow", UNION));
    }

    // getBattlesToResolve

    @Test
    public void getBattlesToResolve() {
        GbgUnit unitA = makeUnit(UNION, "A");
        GbgUnit unitB = makeUnit(CONFEDERATE, "B");
        testGame.clearBoard();
        testGame.putUnitAt(unitA, 5, 5, EAST);
        testGame.putUnitAt(unitB, 6, 5, WEST);
        Collection<BattleDescriptor> battlesToResolve = singletonList(
                new Battle(singletonList(unitA), singletonList(unitB))
        );

        assertEquals(battlesToResolve, game.getBattlesToResolve());
    }

}