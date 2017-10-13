package student.gettysburg.engine.common;

import gettysburg.common.GbgUnit;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static gettysburg.common.ArmyID.CONFEDERATE;
import static gettysburg.common.ArmyID.UNION;
import static gettysburg.common.BattleResult.*;
import static gettysburg.common.Direction.EAST;
import static gettysburg.common.Direction.WEST;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static student.gettysburg.engine.common.Battle.makeBattle;
import static student.gettysburg.engine.common.Resolution.dieFaceBattleResults;
import static student.gettysburg.engine.common.Resolution.makeResolution;
import static student.gettysburg.engine.common.Unit.makeUnit;

public class ResolutionTest {

    // makeResolution

    @Test
    public void exchangeAllEliminated() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), EXCHANGE);

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
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), EXCHANGE);

        assertEquals(EXCHANGE, resolution.getBattleResult());
        assertEquals(singletonList(unitC), resolution.getActiveUnionUnits());
        assertEquals(emptyList(), resolution.getActiveConfederateUnits());
        assertEquals(new HashSet<>(asList(unitA, unitB)), new HashSet<>(resolution.getEliminatedUnionUnits()));
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
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), AELIM);

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
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), DELIM);

        assertEquals(DELIM, resolution.getBattleResult());
        assertEquals(attackingUnits, resolution.getActiveUnionUnits());
        assertEquals(emptyList(), resolution.getActiveConfederateUnits());
        assertEquals(emptyList(), resolution.getEliminatedUnionUnits());
        assertEquals(defendingUnits, resolution.getEliminatedConfederateUnits());
    }

    @Test
    public void attackersRetreated() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 2, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), ABACK);

        assertEquals(ABACK, resolution.getBattleResult());
        assertEquals(attackingUnits, resolution.getActiveUnionUnits());
        assertEquals(defendingUnits, resolution.getActiveConfederateUnits());
        assertEquals(emptyList(), resolution.getEliminatedUnionUnits());
        assertEquals(emptyList(), resolution.getEliminatedConfederateUnits());
    }

    @Test
    public void defendersRetreated() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 2, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), DBACK);

        assertEquals(DBACK, resolution.getBattleResult());
        assertEquals(attackingUnits, resolution.getActiveUnionUnits());
        assertEquals(defendingUnits, resolution.getActiveConfederateUnits());
        assertEquals(emptyList(), resolution.getEliminatedUnionUnits());
        assertEquals(emptyList(), resolution.getEliminatedConfederateUnits());
    }

    // dieFaceBattleResults

    @Test
    public void dieFaceBattleResults1AELIM() {
        assertEquals(AELIM, dieFaceBattleResults.get(1).apply(0.0));
    }

    @Test
    public void dieFaceBattleResults1ABACK() {
        assertEquals(ABACK, dieFaceBattleResults.get(1).apply(0.2));
    }

    @Test
    public void dieFaceBattleResults1DBACK() {
        assertEquals(DBACK, dieFaceBattleResults.get(1).apply(0.333));
    }

    @Test
    public void dieFaceBattleResults1DELIM() {
        assertEquals(DELIM, dieFaceBattleResults.get(1).apply(0.5));
    }

    @Test
    public void dieFaceBattleResults2AELIM() {
        assertEquals(AELIM, dieFaceBattleResults.get(1).apply(0.0));
    }

    @Test
    public void dieFaceBattleResults2ABACK() {
        assertEquals(ABACK, dieFaceBattleResults.get(1).apply(0.25));
    }

    @Test
    public void dieFaceBattleResults2EXCHANGE() {
        assertEquals(EXCHANGE, dieFaceBattleResults.get(2).apply(0.333));
    }

    @Test
    public void dieFaceBattleResults2ABACK2() {
        assertEquals(ABACK, dieFaceBattleResults.get(2).apply(1.0));
    }

    @Test
    public void dieFaceBattleResults2EXCHANGE2() {
        assertEquals(EXCHANGE, dieFaceBattleResults.get(2).apply(2.0));
    }

    @Test
    public void dieFaceBattleResults2DBACK() {
        assertEquals(DBACK, dieFaceBattleResults.get(2).apply(5.0));
    }

    @Test
    public void dieFaceBattleResults3AELIM() {
        assertEquals(AELIM, dieFaceBattleResults.get(3).apply(0.0));
    }

    @Test
    public void dieFaceBattleResults3ABACK() {
        assertEquals(ABACK, dieFaceBattleResults.get(3).apply(0.167));
    }

    @Test
    public void dieFaceBattleResults3DBACK() {
        assertEquals(DBACK, dieFaceBattleResults.get(3).apply(0.5));
    }

    @Test
    public void dieFaceBattleResults3DELIM() {
        assertEquals(DELIM, dieFaceBattleResults.get(3).apply(4.0));
    }

    @Test
    public void dieFaceBattleResults4AELIM() {
        assertEquals(AELIM, dieFaceBattleResults.get(4).apply(0.0));
    }

    @Test
    public void dieFaceBattleResults4ABACK() {
        assertEquals(ABACK, dieFaceBattleResults.get(4).apply(0.2));
    }

    @Test
    public void dieFaceBattleResults4DBACK() {
        assertEquals(DBACK, dieFaceBattleResults.get(4).apply(3.0));
    }

    @Test
    public void dieFaceBattleResults4DELIM() {
        assertEquals(DELIM, dieFaceBattleResults.get(4).apply(6.0));
    }

    @Test
    public void dieFaceBattleResults5AELIM() {
        assertEquals(AELIM, dieFaceBattleResults.get(5).apply(0.0));
    }

    @Test
    public void dieFaceBattleResults5EXCHANGE() {
        assertEquals(EXCHANGE, dieFaceBattleResults.get(5).apply(1.0));
    }

    @Test
    public void dieFaceBattleResults5DBACK() {
        assertEquals(DBACK, dieFaceBattleResults.get(5).apply(4.0));
    }

    @Test
    public void dieFaceBattleResults5DELIM() {
        assertEquals(DELIM, dieFaceBattleResults.get(5).apply(5.0));
    }

    @Test
    public void dieFaceBattleResults6AELIM() {
        assertEquals(AELIM, dieFaceBattleResults.get(6).apply(0.0));
    }

    @Test
    public void dieFaceBattleResults6DELIM() {
        assertEquals(DELIM, dieFaceBattleResults.get(6).apply(3.0));
    }

    // isRetreat

    @Test
    public void isRetreatAttackers() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), ABACK);

        assertTrue(resolution.isRetreat());
    }

    @Test
    public void isRetreatDefenders() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), DBACK);

        assertTrue(resolution.isRetreat());
    }

    @Test
    public void isRetreatFalse() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), AELIM);

        assertFalse(resolution.isRetreat());
    }

    // getRetreatingUnits

    @Test
    public void getRetreatingUnitsAttackers() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), ABACK);

        assertEquals(new HashSet<>(attackingUnits), new HashSet<>(resolution.getRetreatingUnits()));
    }

    @Test
    public void getRetreatingUnitsDefenders() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), DBACK);

        assertEquals(new HashSet<>(defendingUnits), new HashSet<>(resolution.getRetreatingUnits()));
    }

    @Test
    public void getRetreatingUnitsNone() {
        Collection<GbgUnit> attackingUnits = singletonList(
                makeUnit(UNION, 1, EAST, "A", 0, null, null)
        );
        Collection<GbgUnit> defendingUnits = singletonList(
                makeUnit(CONFEDERATE, 1, WEST, "B", 0, null, null)
        );
        Resolution resolution = makeResolution(makeBattle(attackingUnits, defendingUnits), AELIM);

        assertTrue(resolution.getRetreatingUnits().isEmpty());
    }

}