package com.moonshinepixel.moonshinepixeldungeon.messages.traps;

import com.moonshinepixel.moonshinepixeldungeon.levels.traps.Trap;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.WornTrap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class TrapObject implements Bundlable {

    public int pos;

    public Trap trap = new WornTrap();

    public TrapObject(Trap trap){
        this.trap=trap;
        this.trap.friendly=true;
        trapName=this.trap.name;
    }
    public TrapObject(){
        this.trap=new WornTrap();
        this.trap.friendly=true;
        trapName=this.trap.name;
    }

    public TrapObject(Class<? extends Trap> trap){
        try {
            this.trap = trap.newInstance();
        } catch (Exception e){
            this.trap = new WornTrap();
        }
        this.trap.friendly=true;
        trapName=this.trap.name;
    }

    private static final String POS	= "pos";
    private static final String TRAP= "trap";
    @Override
    public void restoreFromBundle( Bundle bundle ) {
        pos = bundle.getInt( POS );
        trap = (Trap)bundle.get(TRAP);
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
        bundle.put( POS, pos );
        bundle.put(TRAP,trap);
    }

    public void trigger(){
        if (Dungeon.visible[pos]) {
            Sample.INSTANCE.play(Assets.SND_TRAP);
        }
        trap.pos=pos;
        trap.trigger();
        if (!trap.active)
            Dungeon.level.untrap(pos);
    }

    public String trapName = trap.name;

    public String desc() {
        return trap.desc();
    }
}
