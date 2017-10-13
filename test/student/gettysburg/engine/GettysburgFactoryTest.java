package student.gettysburg.engine;

import gettysburg.common.*;
import gettysburg.common.exceptions.GbgInvalidActionException;
import gettysburg.common.exceptions.GbgInvalidMoveException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.*;
import static gettysburg.common.GbgGameStatus.IN_PROGRESS;
import static gettysburg.common.GbgGameStep.*;
import static gettysburg.common.UnitSize.BRIGADE;
import static gettysburg.common.UnitSize.DIVISION;
import static gettysburg.common.UnitType.CAVALRY;
import static gettysburg.common.UnitType.INFANTRY;
import static org.junit.Assert.*;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.GettysburgFactory.makeTestGame;
import static student.gettysburg.engine.common.Unit.makeUnit;

public class GettysburgFactoryTest {

    private GbgGame game;
    private TestGbgGame testGame;
    private GbgUnit gamble, schurz, devin, heth, rodes, hampton;

    @Before
    public void setup() {
        game = testGame = makeTestGame();
        gamble = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
        devin = game.getUnitsAt(makeCoordinate(13, 9)).iterator().next();
        heth = game.getUnitsAt(makeCoordinate(8, 8)).iterator().next();
        schurz = makeUnit(UNION, 2, NORTH, "Schurz", 2, DIVISION, INFANTRY);
        rodes = makeUnit(CONFEDERATE, 4, SOUTH, "Rodes", 2, DIVISION, INFANTRY);
        hampton = makeUnit(CONFEDERATE, 1, SOUTH, "Hampton", 4, BRIGADE, CAVALRY);
    }

    // Initial setup tests taken as is from Version 1 tests
    @Test
    public void gameTurnIsOneOnInitializedGame() {
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    public void initialGameStatusIsInProgress() {
        assertEquals(IN_PROGRESS, game.getGameStatus());
    }

    @Test
    public void gameStepOnInitializedGameIsUMOVE() {
        assertEquals(UMOVE, game.getCurrentStep());
    }

    @Test
    public void correctSquareForGambleUsingWhereIsUnit() {
        assertEquals(makeCoordinate(11, 11), game.whereIsUnit("Gamble", UNION));
    }

    @Test
    public void correctSquareForGambleUsingGetUnitsAt() {
        GbgUnit unit = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
        assertNotNull(unit);
        assertEquals("Gamble", unit.getLeader());
    }

    @Test
    public void devinFacesSouth() {
        assertEquals(SOUTH, game.getUnitFacing(devin));
    }

    // Game step and turn tests
    @Test
    public void unionBattleFollowsUnionMove() {
        game.endStep();
        assertEquals(UBATTLE, game.getCurrentStep());
    }

    @Test
    public void confederateMoveFollowsUnionBattle() {
        game.endStep();
        game.endStep();
        assertEquals(CMOVE, game.getCurrentStep());
    }

    @Test
    public void confederateBattleFollowsConfederateMove() {
        game.endStep();
        game.endStep();
        assertEquals(CBATTLE, game.endStep());
    }

    @Test
    public void turnOneDuringConfederateBattle() {
        game.endStep();
        game.endStep();
        game.endStep();
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    public void goToTurn2() {
        game.endStep();
        game.endStep();
        game.endStep();
        game.endStep();
        assertEquals(2, game.getTurnNumber());
    }

    @Test
    public void startOfTurn2IsUMOVEStep() {
        game.endStep();
        game.endStep();
        game.endStep();
        game.endStep();
        assertEquals(UMOVE, game.getCurrentStep());
    }

    // Movement tests
    @Test
    public void gambleMovesNorth() {
        game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(11, 10));
        assertEquals(makeCoordinate(11, 10), game.whereIsUnit(gamble));
        assertNull(">> Documentation says this should be null, not an empty array",
                game.getUnitsAt(makeCoordinate(11, 11)));
    }

    @Test
    public void devinMovesSouth() {
        game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(13, 11));
        assertEquals(makeCoordinate(13, 11), game.whereIsUnit(devin));
    }

    @Test
    public void hethMovesEast() {
        game.endStep();
        game.endStep();
        game.moveUnit(heth, makeCoordinate(8, 8), makeCoordinate(10, 8));
        assertEquals(heth, game.getUnitsAt(makeCoordinate(10, 8)).iterator().next());
    }

    @Test
    public void devinMovesSouthUsingANonStandardCoordinate() {
        game.moveUnit(devin, new TestCoordinate(13, 9), makeCoordinate(13, 11));
        assertEquals(makeCoordinate(13, 11), game.whereIsUnit(devin));
    }

    // Tests requiring the test double
    @Test
    public void stackedEntryUnitIsAsCorrectLocation() {
        testGame.setGameTurn(8);
        testGame.setGameStep(CBATTLE);
        game.endStep();  // step -> UMOVE, turn -> 9
        assertEquals(makeCoordinate(22, 22), game.whereIsUnit("Geary", UNION));
    }

    @Test
    public void stackedEntryUnitsAreNotMoved() {
        testGame.setGameTurn(8);
        testGame.setGameStep(CBATTLE);
        game.endStep();  // step -> UMOVE, turn -> 9
        game.endStep();  // step -> UBATTLE
        assertNull(game.getUnitsAt(makeCoordinate(22, 22)));
    }

    @Test
    public void unitsStackedProperlyAtStartOfGame() {
        // Move units at (7, 28) during UMOVE, turn 1
        Collection<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28));
        assertEquals(4, units.size());
    }

    @Test
    public void allStackedUnitsAtStartOfGameMove() {
        Iterator<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28)).iterator();
        game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(5, 28));
        game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(6, 28));
        game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(8, 28));
        game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(9, 28));
        Collection<GbgUnit> remaining = game.getUnitsAt(makeCoordinate(7, 28));
        assertTrue(remaining == null || remaining.isEmpty());
    }

    @Test
    public void someEntryUnitsRemainAndAreRemoved() {
        Iterator<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28)).iterator();
        game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(5, 28));
        game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(6, 28));
        Collection<GbgUnit> remaining = game.getUnitsAt(makeCoordinate(7, 28));
        assertEquals(2, remaining.size());
        game.endStep();
        remaining = game.getUnitsAt(makeCoordinate(7, 28));
        assertTrue(remaining == null || remaining.isEmpty());
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void tryToMoveThroughEnemyZOC() {
        testGame.clearBoard();
        testGame.setGameTurn(2);
        testGame.putUnitAt(heth, 10, 10, SOUTH);		// ZOC = [(9, 11), (10, 11), (11, 11)]
        testGame.putUnitAt(hampton, 13, 10, SOUTH);	// ZOC = [(12, 11), (13, 11), (14, 11)]
        testGame.putUnitAt(devin, 11, 12, SOUTH);
        testGame.setGameStep(UMOVE);
        game.moveUnit(devin, makeCoordinate(11, 12), makeCoordinate(11, 9));
    }

    // Battle tests

    @Test
    public void twoBattles() {
        testGame.clearBoard();
        testGame.setGameTurn(2);
        testGame.putUnitAt(heth, 5, 5, SOUTH);
        testGame.putUnitAt(devin, 5, 7, SOUTH);
        testGame.putUnitAt(rodes, 20, 20, NORTH);
        testGame.putUnitAt(schurz, 20, 18, SOUTHWEST);
        testGame.setGameStep(CMOVE);
        game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
        game.moveUnit(rodes, makeCoordinate(20, 20), makeCoordinate(20, 19));
        game.endStep();		// CBATTLE
        Collection<BattleDescriptor> battles = game.getBattlesToResolve();
        BattleDescriptor battle = battles.iterator().next();
        if (battles.size() == 1) {
            assertTrue(battle.getAttackers().contains(heth));
        } else {
            assertTrue(battle.getAttackers().contains(heth) || battle.getAttackers().contains(rodes));
        }
    }

    @Test(expected=GbgInvalidActionException.class)
    public void unitTriesToFightTwoBattlesInSameTurn() {
        testGame.clearBoard();
        testGame.setGameTurn(2);
        testGame.setGameStep(UMOVE);
        testGame.putUnitAt(heth, 5, 5, SOUTH);
        testGame.putUnitAt(hampton, 7, 5, SOUTH);
        testGame.putUnitAt(schurz, 6, 7, NORTH);
        game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
        game.endStep();	// UBATTLE
        TestBattleDescriptor bd = new TestBattleDescriptor();
        bd.addAttacker(schurz);
        bd.addDefender(heth);
        TestBattleDescriptor bd1 = new TestBattleDescriptor();
        bd1.addAttacker(schurz);
        bd1.addDefender(hampton);
        game.resolveBattle(bd);
        game.resolveBattle(bd1);
    }

    @Test(expected=Exception.class)
    public void notAllUnitsHaveFoughtAtEndOfBattleStep() {
        testGame.clearBoard();
        testGame.setGameTurn(2);
        testGame.setGameStep(UMOVE);
        testGame.putUnitAt(heth, 5, 5, SOUTH);
        testGame.putUnitAt(hampton, 7, 5, SOUTH);
        testGame.putUnitAt(schurz, 6, 7, NORTH);
        game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
        game.endStep();	// UBATTLE
        TestBattleDescriptor bd = new TestBattleDescriptor();
        bd.addAttacker(schurz);
        bd.addDefender(heth);
        game.resolveBattle(bd);
        game.endStep(); 	// CMOVE: hampton did not engage
    }

    @Test(expected=Exception.class)
    public void improperBattleDescriptor() {
        testGame.clearBoard();
        testGame.setGameTurn(2);
        testGame.setGameStep(UMOVE);
        testGame.putUnitAt(heth, 22, 5, SOUTH);
        testGame.putUnitAt(hampton, 7, 5, SOUTH);
        testGame.putUnitAt(schurz, 6, 7, NORTH);
        game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
        game.endStep();	// UBATTLE
        TestBattleDescriptor bd = new TestBattleDescriptor();
        bd.addAttacker(schurz);
        bd.addDefender(heth);
        game.resolveBattle(bd);		// heth is not in schurz' ZOC
    }

    // Tests requiring the test double
    @Test
    public void stackedEntryUnitIsAtCorrectLocation() {
        testGame.setGameTurn(8);
        testGame.setGameStep(CBATTLE);
        game.endStep();  // step -> UMOVE, turn -> 9
        assertEquals(makeCoordinate(22, 22), game.whereIsUnit("Geary", UNION));
    }

    @Test
    public void hethMovesWest() {
        game.endStep();
        game.endStep();
        game.moveUnit(heth,  makeCoordinate(8, 8), makeCoordinate(9, 8));
        assertEquals(heth, game.getUnitsAt(makeCoordinate(9, 8)).iterator().next());
    }

    @Test
    public void devinMovesNorthEast() {
        game.moveUnit(devin,  makeCoordinate(13, 9), makeCoordinate(16, 6));
        assertEquals(makeCoordinate(16, 6), game.whereIsUnit(devin));
    }

    @Test
    public void devinMovesSouthWest() {
        game.moveUnit(devin,  makeCoordinate(13, 9), makeCoordinate(9, 13));
        assertEquals(makeCoordinate(9, 13), game.whereIsUnit(devin));
    }

    @Test
    public void gambleMovesSouthEast() {
        game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(13, 12));
        assertEquals(makeCoordinate(13, 12), game.whereIsUnit(gamble));
    }

    @Test
    public void gambleMovesNorthWest() {
        game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(12, 10));
        assertEquals(makeCoordinate(12, 10), game.whereIsUnit(gamble));
    }

    @Test
    public void hethMovesAnL() {
        game.endStep();
        game.endStep();
        game.moveUnit(heth,  makeCoordinate(8, 8), makeCoordinate(9, 6));
        assertEquals(heth, game.getUnitsAt(makeCoordinate(9, 6)).iterator().next());
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void attemptToMoveTooFar()
    {
        game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(18, 9));
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void attemptToMoveOntoAnotherUnit()
    {
        game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(13, 9));
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void attemptToMoveFromEmptySquare() {
        game.moveUnit(gamble,  makeCoordinate(10, 10), makeCoordinate(10, 9));
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void attemptToMoveWrongArmyUnit() {
        game.moveUnit(heth, makeCoordinate(8, 8), makeCoordinate(9, 6));
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void attemptToMoveUnitTwiceInOneTurn() {
        game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(12, 10));
        game.moveUnit(gamble, makeCoordinate(12, 10), makeCoordinate(12, 11));
    }

    // Facing tests
    @Test
    public void gambleFacesNorth() {
        game.setUnitFacing(gamble, NORTH);
        assertEquals(NORTH, game.getUnitFacing(gamble));
    }

    @Test
    public void devinFacesSoutheastAfterMoving() {
        game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(13, 10));
        game.setUnitFacing(devin, SOUTHEAST);
        assertEquals(SOUTHEAST, game.getUnitFacing(devin));
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void hethAttemptsFacingChangeAtWrongTime() {
        game.setUnitFacing(heth, WEST);
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void devinTriesToSetFacingTwice() {
        game.setUnitFacing(devin, NORTHWEST);
        game.moveUnit(devin, makeCoordinate(13, 9), makeCoordinate(13, 10));
        game.setUnitFacing(devin, SOUTHEAST);
    }

    // Other tests
    @Test(expected=Exception.class)
    public void queryInvalidSquare()
    {
        game.getUnitsAt(makeCoordinate(30, 30));
    }

    @Test(expected=GbgInvalidMoveException.class)
    public void moveToStartingSquare() {
        game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(11, 11));
    }


    class TestCoordinate implements Coordinate {
        private final int x;
        private final int y;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        TestCoordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class TestBattleDescriptor implements BattleDescriptor {
        private final Collection<GbgUnit> attackers;
        private final Collection<GbgUnit> defenders;

        TestBattleDescriptor() {
            attackers = new ArrayList<>();
            defenders = new ArrayList<>();
        }

        void addAttacker(GbgUnit unit) {
            attackers.add(unit);
        }

        void addDefender(GbgUnit unit) {
            defenders.add(unit);
        }

        @Override
        public Collection<GbgUnit> getAttackers() {
            return attackers;
        }

        @Override
        public Collection<GbgUnit> getDefenders() {
            return defenders;
        }
    }
}
