package com.moonshinepixel.moonshinepixeldungeon.actors.buffs;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;

public class Countdown extends FlavourBuff implements Hero.Doom {

    {
        type = Buff.buffType.NEUTRAL;
    }

    @Override
    public boolean act() {
        target.damage(Dungeon.fakedepth[Dungeon.depth]/5+1, this);
        BuffIndicator.refreshHero();
        spend( TICK );
        return true;
    }

    @Override
    public int icon() {
        if (cooldown()>0) {
            return BuffIndicator.COUNTDOWN;
        } else {
            return BuffIndicator.COUNTDOWND;
        }
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        if (cooldown()>0) {
            return Messages.get(this, "desc", dispTurns());
        } else {
            return Messages.get(this, "descdeadly");
        }
    }

    @Override
    public void detach() {
        //This buff can't be detached
    }

    @Override
    public void onDeath() {
        Dungeon.fail( getClass() );
        GLog.n( Messages.get(this, "ondeath") );
    }
}
