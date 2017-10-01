package student.gettysburg.engine.common;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.GbgUnit;

import java.util.Collection;

public class Battle implements BattleDescriptor {


    private Collection<GbgUnit> attackers;
    private Collection<GbgUnit> defenders;

    public Battle(Collection<GbgUnit> attackers, Collection<GbgUnit> defenders) {
        this.attackers = attackers;
        this.defenders = defenders;
    }

    @Override
    public Collection<GbgUnit> getAttackers() {
        return attackers;
    }

    @Override
    public Collection<GbgUnit> getDefenders() {
        return defenders;
    }
}
