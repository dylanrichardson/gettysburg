package student.gettysburg.engine.common;

import gettysburg.common.GbgUnit;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.BattleResult.AELIM;
import static gettysburg.common.BattleResult.DELIM;
import static gettysburg.common.BattleResult.EXCHANGE;
import static gettysburg.common.Direction.EAST;
import static gettysburg.common.Direction.WEST;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static student.gettysburg.engine.common.Battle.makeBattle;
import static student.gettysburg.engine.common.Resolution.makeResolution;
import static student.gettysburg.engine.common.Unit.makeUnit;

public class ResolutionTest {

    @Test
    public void exchangeAllEliminated() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits));

        assertEquals(EXCHANGE, resolution.getBattleResult());
        assertEquals(emptyList(), resolution.getActiveUnionUnits());
        assertEquals(emptyList(), resolution.getActiveConfederateUnits());
        assertEquals(attackingUnits, resolution.getEliminatedUnionUnits());
        assertEquals(defendingUnits, resolution.getEliminatedConfederateUnits());
    }

    @Test
    public void exchangePartEliminated() {
        GbgUnit unitA = makeUnit(UNION, 2, EAST, "A", 0, null, null);
        GbgUnit unitB = makeUnit(UNION, 2, EAST, "B", 0, null, null);
        GbgUnit unitC = makeUnit(UNION, 3, EAST, "C", 0, null, null);
        Collection<GbgUnit> attackingUnits = asList(unitA, unitB, unitC);
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 4, WEST, "D", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits));

        assertEquals(EXCHANGE, resolution.getBattleResult());
        assertEquals(singletonList(unitC), resolution.getActiveUnionUnits());
        assertEquals(emptyList(), resolution.getActiveConfederateUnits());
        assertEquals(asList(unitA, unitB), resolution.getEliminatedUnionUnits());
        assertEquals(defendingUnits, resolution.getEliminatedConfederateUnits());
    }

    @Test
    public void attackersEliminated() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 2, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits));

        assertEquals(AELIM, resolution.getBattleResult());
        assertEquals(emptyList(), resolution.getActiveUnionUnits());
        assertEquals(defendingUnits, resolution.getActiveConfederateUnits());
        assertEquals(attackingUnits, resolution.getEliminatedUnionUnits());
        assertEquals(emptyList(), resolution.getEliminatedConfederateUnits());
    }

    @Test
    public void defendersEliminated() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 2, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits));

        assertEquals(DELIM, resolution.getBattleResult());
        assertEquals(attackingUnits, resolution.getActiveUnionUnits());
        assertEquals(emptyList(), resolution.getActiveConfederateUnits());
        assertEquals(emptyList(), resolution.getEliminatedUnionUnits());
        assertEquals(defendingUnits, resolution.getEliminatedConfederateUnits());
    }

}