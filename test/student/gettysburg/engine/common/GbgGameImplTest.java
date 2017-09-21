package student.gettysburg.engine.common;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import org.junit.Test;
import student.gettysburg.engine.utility.configure.BattleOrder;

import java.util.Collection;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.*;
import static gettysburg.common.GbgGameStatus.UNION_WINS;
import static gettysburg.common.GbgGameStep.*;
import static org.junit.Assert.*;
import static student.gettysburg.engine.common.CoordinateImpl.makeCoordinate;
import static student.gettysburg.engine.utility.configure.BattleOrder.getConfederateBattleOrder;
import static student.gettysburg.engine.utility.configure.BattleOrder.getUnionBattleOrder;

public class GbgGameImplTest {

    private static final GbgUnit GAMBLE = getUnionBattleOrder()[0].unit;
    private static final GbgUnit DEVIN = getUnionBattleOrder()[1].unit;
    private static final GbgUnit HETH = getConfederateBattleOrder()[0].unit;

    @Test
    public void newGameStartsOnTurnOne() {
        GbgGame game = new GbgGameImpl();
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    public void newGameStartsOnUnionMove() {
        GbgGame game = new GbgGameImpl();
        assertEquals(UMOVE, game.getCurrentStep());
    }

    @Test
    public void newGameStartsWithGambleAtCorrectSquare() {
        GbgGame game = new GbgGameImpl();
        assertEquals(makeCoordinate(11, 11), game.whereIsUnit(GAMBLE));
    }

    @Test
    public void newGameStartsWithDevinAtCorrectSquare() {
        GbgGame game = new GbgGameImpl();
        assertEquals(makeCoordinate(13, 9), game.whereIsUnit(DEVIN));
    }

    @Test
    public void newGameStartsWithHethAtCorrectSquare() {
        GbgGame game = new GbgGameImpl();
        assertEquals(makeCoordinate(8, 8), game.whereIsUnit(HETH));
    }

    @Test
    public void endBattleStepAdvancesStep() {
        GbgGame game = new GbgGameImpl();
        game.endStep();
        game.endStep();
        game.endBattleStep();
        assertEquals(CBATTLE, game.getCurrentStep());
    }

    @Test(expected = GbgInvalidActionException.class)
    public void endBattleStepDuringMoveStep() {
        GbgGame game = new GbgGameImpl();
        game.endBattleStep();
    }

    @Test
    public void endMoveStepAdvancesStep() {
        GbgGame game = new GbgGameImpl();
        game.endMoveStep();
        assertEquals(CMOVE, game.getCurrentStep());
    }

    @Test(expected = GbgInvalidActionException.class)
    public void endMoveStepDuringBattleStep() {
        GbgGame game = new GbgGameImpl();
        game.endStep();
        game.endStep();
        game.endMoveStep();
    }

    @Test
    public void endStepFourTimesEndsGame() {
        GbgGame game = new GbgGameImpl();
        assertEquals(CMOVE, game.endStep());
        assertEquals(UBATTLE, game.endStep());
        assertEquals(CBATTLE, game.endStep());
        assertEquals(UMOVE, game.endStep());
        assertEquals(UNION_WINS, game.getGameStatus());
    }

    @Test
    public void getUnitFacingWest() {
        GbgGame game = new GbgGameImpl();
        assertEquals(WEST, game.getUnitFacing(GAMBLE));
    }

    @Test
    public void getUnitFacingSouth() {
        GbgGame game = new GbgGameImpl();
        assertEquals(SOUTH, game.getUnitFacing(DEVIN));
    }

    @Test
    public void getUnitFacingEast() {
        GbgGame game = new GbgGameImpl();
        assertEquals(EAST, game.getUnitFacing(HETH));
    }

    @Test
    public void getUnitsAtCoordinateWithOneUnit() {
        GbgGame game = new GbgGameImpl();
        GbgUnit unit = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
        assertEquals(GAMBLE, unit);
    }

    @Test
    public void getUnitsAtCoordinateWithNoUnits() {
        GbgGame game = new GbgGameImpl();
        Collection<GbgUnit> units = game.getUnitsAt(makeCoordinate(11, 12));
        assertTrue(units.isEmpty());
    }

    @Test
    public void moveUnitWhenTurnToValidSquare() {
        GbgGame game = new GbgGameImpl();
        Coordinate to = makeCoordinate(11, 12);
        game.moveUnit(GAMBLE, makeCoordinate(11, 11), to);
        assertEquals(to, game.whereIsUnit(GAMBLE));
    }

    @Test(expected = GbgInvalidActionException.class)
    public void moveUnitWhenNotTurn() {
        GbgGame game = new GbgGameImpl();
        game.moveUnit(HETH, makeCoordinate(8, 8), makeCoordinate(8, 9));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnFromInvalidSquare() {
        GbgGame game = new GbgGameImpl();
        game.moveUnit(GAMBLE, makeCoordinate(11, 12), makeCoordinate(11, 13));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnToOutOfReachSquare() {
        GbgGame game = new GbgGameImpl();
        game.moveUnit(GAMBLE, makeCoordinate(11, 11), makeCoordinate(11, 16));
    }

    @Test(expected = GbgInvalidMoveException.class)
    public void moveUnitWhenTurnToOccupiedSquare() {
        GbgGame game = new GbgGameImpl();
        game.moveUnit(GAMBLE, makeCoordinate(11, 11), makeCoordinate(11, 11));
    }

    @Test
    public void setUnitFacingDuringMove() {
        GbgGame game = new GbgGameImpl();
        game.setUnitFacing(DEVIN, NORTH);
        assertEquals(NORTH, game.getUnitFacing(DEVIN));
        assertEquals(SOUTH, DEVIN.getFacing());
    }

    @Test(expected = GbgInvalidActionException.class)
    public void setUnitFacingNotDuringMove() {
        GbgGame game = new GbgGameImpl();
        game.setUnitFacing(HETH, NORTH);
    }

}