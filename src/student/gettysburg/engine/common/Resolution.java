package student.gettysburg.engine.common;

import gettysburg.common.*;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.BattleResult.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static student.gettysburg.engine.common.Battle.makeBattle;

public class Resolution implements BattleResolution {

    private final Map<ArmyID, Map<Boolean, Collection<GbgUnit>>> armyUnitStatusMap = new HashMap<>();
    private final ArmyID attackers;
    private final ArmyID defenders;
    private final BattleResult battleResult;

    static final Map<Integer, Function<Double, BattleResult>> dieFaceBattleResults;
    static {
        Map<Integer, Function<Double, BattleResult>> dieFaceResults = new HashMap<>();
        dieFaceResults.put(1, makeDieFace(asList(
                new Pair<>(0.0, AELIM),
                new Pair<>(0.2, ABACK),
                new Pair<>(0.333, DBACK),
                new Pair<>(0.5, DELIM)
        )));
        dieFaceResults.put(2, makeDieFace(asList(
                new Pair<>(0.0, AELIM),
                new Pair<>(0.25, ABACK),
                new Pair<>(0.333, EXCHANGE),
                new Pair<>(1.0, ABACK),
                new Pair<>(2.0, EXCHANGE),
                new Pair<>(5.0, DBACK)
        )));
        dieFaceResults.put(3, makeDieFace(asList(
                new Pair<>(0.0, AELIM),
                new Pair<>(0.167, ABACK),
                new Pair<>(0.5, DBACK),
                new Pair<>(4.0, DELIM)
        )));
        dieFaceResults.put(4, makeDieFace(asList(
                new Pair<>(0.0, AELIM),
                new Pair<>(0.2, ABACK),
                new Pair<>(0.3, DBACK),
                new Pair<>(6.0, DELIM)
        )));
        dieFaceResults.put(5, makeDieFace(asList(
                new Pair<>(0.0, AELIM),
                new Pair<>(1.0, EXCHANGE),
                new Pair<>(4.0, DBACK),
                new Pair<>(5.0, DELIM)
        )));
        dieFaceResults.put(6, makeDieFace(asList(
                new Pair<>(0.0, AELIM),
                new Pair<>(3.0, DELIM)
        )));
        dieFaceBattleResults = Collections.unmodifiableMap(dieFaceResults);
    }

    private Resolution(BattleResult battleResult, Map<GbgUnit, Boolean> unitsMap, ArmyID attackers) {
        this.battleResult = battleResult;
        this.attackers = attackers;
        this.defenders = attackers == UNION ? CONFEDERATE : UNION;
        armyUnitStatusMap.put(UNION, getEmptyActiveUnitsMap());
        armyUnitStatusMap.put(CONFEDERATE, getEmptyActiveUnitsMap());
        // convert unit -> bool to army -> bool -> units
        unitsMap.forEach((unit, isActive) -> armyUnitStatusMap.get(unit.getArmy()).get(isActive).add(unit));
    }

    static Resolution makeResolution(BattleDescriptor battleDescriptor) {
        Battle battle = makeBattle(battleDescriptor);
        BattleResult result = makeResult(battle.getBattleRatio());
        return makeResolution(battle, result);
    }

    static Resolution makeResolution(BattleDescriptor battleDescriptor, BattleResult result) {
        Battle battle = makeBattle(battleDescriptor);
        return new Resolution(result, eliminateUnits(battle, result), battle.getAttackingArmy());
    }

    private static BattleResult makeResult(Double battleRatio) {
        return dieFaceBattleResults.get(rollDie()).apply(battleRatio);
    }

    private static Integer rollDie() {
        return ThreadLocalRandom.current().nextInt(1, 7);
    }

    private static Map<GbgUnit, Boolean> eliminateUnits(Battle battle, BattleResult result) {
        Map<GbgUnit, Boolean> unitActiveMap = new HashMap<>();
        // TODO refactor
        switch (result) {
            case AELIM:
                battle.getAttackers().forEach(unit -> unitActiveMap.put(unit, false));
                battle.getDefenders().forEach(unit -> unitActiveMap.put(unit, true));
                break;
            case DELIM:
                battle.getAttackers().forEach(unit -> unitActiveMap.put(unit, true));
                battle.getDefenders().forEach(unit -> unitActiveMap.put(unit, false));
                break;
            case ABACK:
            case DBACK:
                battle.getAttackers().forEach(unit -> unitActiveMap.put(unit, true));
                battle.getDefenders().forEach(unit -> unitActiveMap.put(unit, true));
                break;
            default: // EXCHANGE
                if (battle.getBattleRatio() > 1) {
                    battle.getDefenders().forEach(unit -> unitActiveMap.put(unit, false));
                    // add enough attackers
                    balanceUnits(unitActiveMap, battle.getAttackers(), battle.getDefendingCombatFactor());
                } else if (battle.getBattleRatio() < 1) {
                    battle.getAttackers().forEach(unit -> unitActiveMap.put(unit, false));
                    // add enough defenders
                    balanceUnits(unitActiveMap, battle.getDefenders(), battle.getAttackingCombatFactor());
                } else {
                    Boolean isActive = battle.getAttackingCombatFactor() == 0;
                    battle.getAttackers().forEach(unit -> unitActiveMap.put(unit, isActive));
                    battle.getDefenders().forEach(unit -> unitActiveMap.put(unit, isActive));
                }
        }
        return unitActiveMap;
    }

    private static void balanceUnits(Map<GbgUnit, Boolean> unitMap, Collection<GbgUnit> units, Double minCombatFactor) {
        List<GbgUnit> sortedUnits = new ArrayList<>(units);
        sortedUnits.sort(Comparator.comparingInt(GbgUnit::getCombatFactor));
        for(GbgUnit unit : sortedUnits) {
            if (minCombatFactor > 0) {
                unitMap.put(unit, false);
                minCombatFactor -= unit.getCombatFactor();
            } else {
                unitMap.put(unit, true);
            }
        }
    }

    private static Function<Double, BattleResult> makeDieFace(
            List<Pair<Double, BattleResult>> ratioResults) {
        // sort the pairs by battle ratio, lowest first
        ratioResults.sort(Comparator.<Pair<Double, BattleResult>>
                comparingDouble(Pair::getKey).reversed());
        return battleRatio -> {
            // use first battle result whose ratio
            for (Pair<Double, BattleResult> ratioResult : ratioResults) {
                Double maxBattleRatio = ratioResult.getKey();
                BattleResult result = ratioResult.getValue();
                if (battleRatio >= maxBattleRatio)
                    return result;
            }
            return ratioResults.get(0).getValue();
        };
    }

    @Override
    public BattleResult getBattleResult() {
        return battleResult;
    }

    @Override
    public Collection<GbgUnit> getEliminatedUnionUnits() {
        return armyUnitStatusMap.get(UNION).get(false);
    }

    @Override
    public Collection<GbgUnit> getEliminatedConfederateUnits() {
        return armyUnitStatusMap.get(CONFEDERATE).get(false);
    }

    @Override
    public Collection<GbgUnit> getActiveUnionUnits() {
        return armyUnitStatusMap.get(UNION).get(true);
    }

    @Override
    public Collection<GbgUnit> getActiveConfederateUnits() {
        return armyUnitStatusMap.get(CONFEDERATE).get(true);
    }

    private Map<Boolean, Collection<GbgUnit>> getEmptyActiveUnitsMap() {
        HashMap<Boolean, Collection<GbgUnit>> activeUnitsMap = new HashMap<>();
        activeUnitsMap.put(true, new ArrayList<>());
        activeUnitsMap.put(false, new ArrayList<>());
        return activeUnitsMap;
    }

    Boolean isRetreat() {
        return battleResult == ABACK || battleResult == DBACK;
    }

    Collection<Unit> getRetreatingUnits() {
        ArmyID armyID;
        if (battleResult == ABACK)
            armyID = attackers;
        else if (battleResult == DBACK)
            armyID = defenders;
        else
            return emptyList();
        return armyUnitStatusMap.get(armyID).get(true)
                .stream().map(Unit::makeUnit).collect(Collectors.toList());
    }
}
