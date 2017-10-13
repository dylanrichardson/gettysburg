package student.gettysburg.engine.common;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleDescriptor;
import gettysburg.common.GbgUnit;

import java.util.Collection;

import static gettysburg.common.ArmyID.UNION;
import static java.util.Collections.emptyList;

class Battle implements BattleDescriptor {


    private final Collection<GbgUnit> attackers;
    private final Collection<GbgUnit> defenders;
    private final Double defendingCombatFactor;
    private final Double attackingCombatFactor;

    private Battle(Collection<GbgUnit> attackers, Collection<GbgUnit> defenders) {
        this.attackers = attackers;
        this.defenders = defenders;
        defendingCombatFactor = sumCombatFactor(defenders);
        attackingCombatFactor = sumCombatFactor(attackers);
    }

    static Battle makeBattle(BattleDescriptor battleDescriptor) {
        return new Battle(battleDescriptor.getAttackers(), battleDescriptor.getDefenders());
    }

    static Battle makeBattle(Collection<GbgUnit> attackers, Collection<GbgUnit> defenders) {
        return new Battle(attackers != null ? attackers : emptyList(), defenders != null ? defenders : emptyList());
    }

    @Override
    public Collection<GbgUnit> getAttackers() {
        return attackers;
    }

    @Override
    public Collection<GbgUnit> getDefenders() {
        return defenders;
    }

    Double getBattleRatio() {
        if (defendingCombatFactor == 0) {
            if (attackingCombatFactor == 0)
                return 1.0;
            return 6.0;
        }
        return attackingCombatFactor / defendingCombatFactor;
    }

    private Double sumCombatFactor(Collection<GbgUnit> units) {
        return units.stream().mapToDouble(GbgUnit::getCombatFactor).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Battle battle = (Battle) o;

        return (attackers != null ? attackers.equals(battle.attackers) : battle.attackers == null)
                && (defenders != null ? defenders.equals(battle.defenders) : battle.defenders == null);
    }

    @Override
    public int hashCode() {
        int result = attackers != null ? attackers.hashCode() : 0;
        result = 31 * result + (defenders != null ? defenders.hashCode() : 0);
        return result;
    }

    Double getAttackingCombatFactor() {
        return attackingCombatFactor;
    }

    Double getDefendingCombatFactor() {
        return defendingCombatFactor;
    }

    ArmyID getAttackingArmy() {
        return attackers.stream().findFirst().map(GbgUnit::getArmy).orElse(UNION);
    }
}
