package student.gettysburg.engine.common;

import gettysburg.common.ArmyID;
import student.gettysburg.engine.utility.configure.UnitInitializer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;


class Reinforcements {


    private Map<ArmyID, Map<Integer, Stream<UnitInitializer>>> armyTurnMap = new HashMap<>();

    private Reinforcements(Stream<UnitInitializer> unitInitializers) {
        armyTurnMap =
                unitInitializers.collect(
                        groupingBy(UnitInitializer::getArmyID,
                                toMap(UnitInitializer::getTurn, Stream::of, Stream::concat)));
    }

    static Reinforcements makeReinforcements(Stream<UnitInitializer> unitInitializers) {
        return new Reinforcements(unitInitializers);
    }

    Stream<UnitInitializer> get(ArmyID armyID, Integer turn) {
        Stream<UnitInitializer> unitInitializers = armyTurnMap.get(armyID).get(turn);
        if (unitInitializers == null)
            return Stream.empty();
        return unitInitializers;
    }
}
