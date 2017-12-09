package com.moonshinepixel.moonshinepixeldungeon.actors.buffs;

import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Healing extends Buff {

    private int healingLeft;

    private float percentHealPerTick;
    private int flatHealPerTick;

    {
        //unlike other buffs, this one acts after the hero and takes priority against enemies
        //healing is much more useful if you get some of it off before enemies attack
        actPriority = 1;
    }

    @Override
    public boolean act(){

        int healingThisTick = Math.round(healingLeft * percentHealPerTick) + flatHealPerTick;

        healingThisTick = (int) GameMath.gate(1, healingThisTick, healingLeft);

        target.HP = Math.min(target.HT, target.HP + healingThisTick);

        target.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "value", healingThisTick));

        healingLeft -= healingThisTick;

        if (healingLeft <= 0){
            detach();
        }

        spend( TICK );

        return true;
    }

    public void setHeal(int amount, float percentPerTick, int flatPerTick){
        healingLeft = amount;
        percentHealPerTick = percentPerTick;
        flatHealPerTick = flatPerTick;
    }

    public void increaseHeal( int amount ){
        healingLeft += amount;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add( CharSprite.State.HEALING );
        else    target.sprite.remove( CharSprite.State.HEALING );
    }

    private static final String LEFT = "left";
    private static final String PERCENT = "percent";
    private static final String FLAT = "flat";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, healingLeft);
        bundle.put(PERCENT, percentHealPerTick);
        bundle.put(FLAT, flatHealPerTick);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        healingLeft = bundle.getInt(LEFT);
        percentHealPerTick = bundle.getFloat(PERCENT);
        flatHealPerTick = bundle.getInt(FLAT);
    }
}
