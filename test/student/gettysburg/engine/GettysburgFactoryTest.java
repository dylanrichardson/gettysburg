package student.gettysburg.engine;

import gettysburg.common.GbgGame;
import gettysburg.common.GbgUnit;
import org.junit.Before;
import org.junit.Test;

import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.GbgGameStep.UMOVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.GettysburgFactory.makeGame;

public class GettysburgFactoryTest {

    private GbgGame game;
    @Before
    public void setup()
    {
        game = makeGame();
    }

    @Test
    public void factoryMakesGame()
    {
        assertNotNull(game);
    }

    @Test
    public void gameTurnIsOneOnInitializedGame()
    {
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    public void gameStepOnInitializedGameIsUMOVE()
    {
        assertEquals(UMOVE, game.getCurrentStep());
    }

    @Test
    public void correctSquareForGambleUsingWhereIsUnit()
    {
        assertEquals(makeCoordinate(11, 11), game.whereIsUnit("Gamble", UNION));
    }

    @Test
    public void correctSquareForGambleUsingGetUnitsAt()
    {
        GbgUnit unit = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
        assertNotNull(unit);
        assertEquals("Gamble", unit.getLeader());
    }

    @Test(expected=Exception.class)
    public void queryInvalidSquare()
    {
        game.getUnitsAt(makeCoordinate(30, 30));
    }

}
