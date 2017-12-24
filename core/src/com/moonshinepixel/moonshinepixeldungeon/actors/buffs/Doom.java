package com.moonshinepixel.moonshinepixeldungeon.actors.buffs;


import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;

public class Doom extends Buff {

    {
        type = buffType.NEGATIVE;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add( CharSprite.State.DARKENED );
        else if (target.invisible == 0) target.sprite.remove( CharSprite.State.DARKENED );
    }

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
}

