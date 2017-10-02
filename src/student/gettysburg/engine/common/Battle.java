package student.gettysburg.engine.common;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.GbgUnit;

import java.util.Collection;

public class Battle implements BattleDescriptor {


    private Collection<GbgUnit> attackers;
    private Collection<GbgUnit> defenders;

    private Battle(Collection<GbgUnit> attackers, Collection<GbgUnit> defenders) {
        this.attackers = attackers;
        this.defenders = defenders;
    }

    static Battle makeBattle(BattleDescriptor battleDescriptor) {
        return new Battle(battleDescriptor.getAttackers(), battleDescriptor.getDefenders());
    }

    static Battle makeBattle(Collection<GbgUnit> attackers, Collection<GbgUnit> defenders) {
        return new Battle(attackers, defenders);
    }

    @Override
    public Collection<GbgUnit> getAttackers() {
        return attackers;
    }

    @Override
    public Collection<GbgUnit> getDefenders() {
        return defenders;
    }

    Double getOdds() {
        return new Double(getAttackingCombatFactor()) / getDefendingCombatFactor();
    }

    private Integer sumCombatFactor(Collection<GbgUnit> units) {
        return units.stream().mapToInt(GbgUnit::getCombatFactor).sum();
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

    Integer getAttackingCombatFactor() {
        return sumCombatFactor(attackers);
    }

    Integer getDefendingCombatFactor() {
        return sumCombatFactor(defenders);
    }
}
