package com.moonshinepixel.moonshinepixeldungeon.levels.traps;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public abstract class ActingTrap extends Trap {

    public TrapActor actor = new TrapActor();

    {
        actor.trap=this;
        Actor.add(actor);
    }

    protected boolean act(){
        actor.spend(Actor.TICK);
        return true;
    }
    private static final String FUSE = "actor";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( FUSE, actor );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        actor=(TrapActor)bundle.get(FUSE);
        actor.trap=this;
        Actor.add(actor);
    }
    public static class TrapActor extends Actor {
        {
            actPriority = -1;
        }

        private ActingTrap trap;

        @Override
        protected boolean act() {


            if (trap.actor != this){
                Actor.remove( this );
                return true;
            }
            if (!Dungeon.level.traps.containsValue(trap, true)) {
                Actor.remove(this);
                return true;
            }
            return trap.act();
        }
        public void spend(float time){
            super.spend(time);
        }
    }
}
