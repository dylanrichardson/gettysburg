package student.gettysburg.engine;

import gettysburg.common.*;
import org.junit.Before;
import org.junit.Test;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.Direction.SOUTH;
import static gettysburg.common.GbgGameStatus.IN_PROGRESS;
import static gettysburg.common.GbgGameStep.*;
import static org.junit.Assert.*;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.GettysburgFactory.makeTestGame;

public class GettysburgFactoryTest {

    private GbgGame game;
    private TestGbgGame testGame;
    private GbgUnit gamble, devin, heth;

    @Before
    public void setup() {
        game = testGame = makeTestGame();
        gamble = game.getUnit("Gamble", UNION);
        devin = game.getUnit("Devin", UNION);
        heth = game.getUnit("Heth", CONFEDERATE);
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
    public void stackedEntryUnitIsAtCorrectLocation() {
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
    public void hethDefeatsDevin() {
        testGame.setGameTurn(2);
        testGame.clearBoard();
        testGame.putUnitAt(heth, 5, 5, SOUTH);
        testGame.putUnitAt(devin, 5, 7, SOUTH);
        testGame.setGameStep(CMOVE);
        game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
        game.endStep();		// CBATTLE
        BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
        assertEquals(BattleResult.DELIM, game.resolveBattle(battle).getBattleResult());
    }

    class TestCoordinate implements Coordinate {
        private int x, y;

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

}
