package student.gettysburg.engine.common;

import gettysburg.common.ArmyID;
import gettysburg.common.Coordinate;
import gettysburg.common.GbgUnit;
import org.junit.Test;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gettysburg.common.ArmyID.UNION;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static student.gettysburg.engine.GettysburgFactory.makeCoordinate;
import static student.gettysburg.engine.common.Reinforcements.makeReinforcements;
import static student.gettysburg.engine.common.Unit.makeUnit;

public class ReinforcementsTest {
    @Test
    public void get() {
        int turn = 5;
        Coordinate coordinate = makeCoordinate(5, 5);
        ArmyID armyID = UNION;
        GbgUnit unit = makeUnit(armyID, "");
        UnitInitializer unitInit = new UnitInitializer(turn, coordinate, unit);
        Reinforcements reinforcements = makeReinforcements(Stream.of(unitInit));

        Stream<UnitInitializer> units = reinforcements.get(armyID, turn);

        assertEquals(new HashSet<>(singletonList(unitInit)), new HashSet<>(units.collect(Collectors.toList())));
    }

}