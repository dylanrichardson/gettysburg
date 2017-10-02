package student.gettysburg.engine.common;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.BattleResolution;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.BattleResult.*;
import static student.gettysburg.engine.common.Battle.makeBattle;

public class Resolution implements BattleResolution {
    private BattleResult battleResult;
    private Collection<GbgUnit> activeUnionUnits = new ArrayList<>();
    private Collection<GbgUnit> eliminatedUnionUnits = new ArrayList<>();
    private Collection<GbgUnit> activeConfederateUnits = new ArrayList<>();
    private Collection<GbgUnit> eliminatedConfederateUnits = new ArrayList<>();

    private Resolution(BattleResult battleResult, Map<GbgUnit, Boolean> unitsMap) {
        this.battleResult = battleResult;
        // TODO refactor
        unitsMap.forEach((unit, isActive) -> {
            if (unit.getArmy() == UNION) {
                if (isActive)
                    activeUnionUnits.add(unit);
                else
                    eliminatedUnionUnits.add(unit);
            } else {
                if (isActive)
                    activeConfederateUnits.add(unit);
                else
                    eliminatedConfederateUnits.add(unit);
            }
        });
    }

    static Resolution makeResolution(BattleDescriptor battleDescriptor) {
        Battle battle = makeBattle(battleDescriptor);
        BattleResult result = getResult(battle.getOdds());

        return new Resolution(result, eliminateUnits(battle, result));
    }

    private static Map<GbgUnit, Boolean> eliminateUnits(Battle battle, BattleResult result) {
        Map<GbgUnit, Boolean> unitMap = new HashMap<>();

        if (result == AELIM) {
            battle.getAttackers().forEach(unit -> unitMap.put(unit, false));
            battle.getDefenders().forEach(unit -> unitMap.put(unit, true));
        } else if (result == DELIM) {
            battle.getAttackers().forEach(unit -> unitMap.put(unit, true));
            battle.getDefenders().forEach(unit -> unitMap.put(unit, false));
        } else {
            if (battle.getOdds() > 1) {
                battle.getDefenders().forEach(unit -> unitMap.put(unit, false));
                // add enough attackers
                addEnoughUnits(unitMap, battle.getAttackers(), battle.getDefendingCombatFactor());
            } else if (battle.getOdds() < 1) {
                battle.getAttackers().forEach(unit -> unitMap.put(unit, false));
                // add enough defenders
                addEnoughUnits(unitMap, battle.getDefenders(), battle.getAttackingCombatFactor());
            } else {
                battle.getAttackers().forEach(unit -> unitMap.put(unit, false));
                battle.getDefenders().forEach(unit -> unitMap.put(unit, false));
            }
        }
        return unitMap;
    }

    private static void addEnoughUnits(Map<GbgUnit, Boolean> unitMap, Collection<GbgUnit> units, Integer minCombatFactor) {
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

    private static BattleResult getResult(Double odds) {
        if (odds >= 2.0)
            return DELIM;
        if (odds > 0.5)
            return EXCHANGE;
        return AELIM;
    }

    @Override
    public BattleResult getBattleResult() {
        return battleResult;
    }

    @Override
    public Collection<GbgUnit> getEliminatedUnionUnits() {
        return eliminatedUnionUnits;
    }

    @Override
    public Collection<GbgUnit> getEliminatedConfederateUnits() {
        return eliminatedConfederateUnits;
    }

    @Override
    public Collection<GbgUnit> getActiveUnionUnits() {
        return activeUnionUnits;
    }

    @Override
    public Collection<GbgUnit> getActiveConfederateUnits() {
        return activeConfederateUnits;
    }
}
