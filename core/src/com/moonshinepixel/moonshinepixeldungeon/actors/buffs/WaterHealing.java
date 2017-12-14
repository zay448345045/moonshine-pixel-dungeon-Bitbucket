package com.moonshinepixel.moonshinepixeldungeon.actors.buffs;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class WaterHealing extends Buff {

    private float duration;

    {
        //unlike other buffs, this one acts after the hero and takes priority against enemies
        //healing is much more useful if you get some of it off before enemies attack
        actPriority = 1;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)){
            target.updateHT(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean act(){

        if (duration<=0){
            detach();
            return true;
        }

        int healingThisTick = (int)GameMath.gate(1,target.HT/20f,Integer.MAX_VALUE);

        float spend = TICK;

        if (Level.water[target.pos]) {
            if (target.HP<target.HT) {
                spend *= 2;
                if (target.HTPENALTY > 0) target.HTPENALTY--;
                target.HP = Math.min(target.HT, target.HP + healingThisTick);

                target.sprite.showStatus(0x4286f4, "+"+healingThisTick);
                target.updateHT();
            }
        }
        duration-=spend;
        spend( TICK );

        return true;
    }

    public void set(float duration){
        this.duration=duration;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add( CharSprite.State.WET );
        else    target.sprite.remove( CharSprite.State.WET );
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("dur",duration);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        duration=bundle.getFloat("dur");
    }
}
