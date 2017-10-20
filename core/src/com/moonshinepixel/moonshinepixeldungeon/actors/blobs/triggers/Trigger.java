package com.moonshinepixel.moonshinepixeldungeon.actors.blobs.triggers;

import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;

public abstract class Trigger {
    public boolean triggerBy(Char ch){
        return ch instanceof Hero;
    }
    public abstract boolean trigger(Char targ);
}
