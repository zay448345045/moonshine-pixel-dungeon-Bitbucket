package com.moonshinepixel.moonshinepixeldungeon.actors.blobs.triggers;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;

public class DummyTrigger extends Trigger {
    @Override
    public boolean trigger(Char targ) {
        //System.out.println("triggered");
        return false;
    }
}
