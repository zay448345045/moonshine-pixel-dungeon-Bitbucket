package com.moonshinepixel.moonshinepixeldungeon.items.grimoires;

import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class ElementalGrimoire extends Grimoire {
    protected final String AC_UTILITY   = "utility";
    protected final String AC_ATTACK    = "attack";
    protected final String AC_BUFF      = "buff";
    protected float skill = 0;
    protected float[] cd = new float[]{0,0,0};
    @Override
    public void validateMobKill(Mob mob) {
        skill+=mob.exp();
        level((int)(skill/10));
    }

    public int utilityLevel(){
        return 0;
    }
    public int attackLevel(){
        return 0;
    }
    public int buffLevel(){
        return 0;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList actions =  super.actions(hero);
        actions.add(AC_UTILITY+utilityLevel());
        actions.add(AC_ATTACK+attackLevel());
        actions.add(AC_BUFF+buffLevel());
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.contains(AC_UTILITY)) {
            if (cd[0]<=0)
                utilityEffect(hero);
            else
                GLog.i(Messages.get(ElementalGrimoire.class,"delay",Math.floor(cd[0]*10)/10));
        }
        if (action.contains(AC_ATTACK)) {
            if (cd[1]<=0)
                attackEffect(hero);
            else
                GLog.i(Messages.get(ElementalGrimoire.class,"delay",Math.floor(cd[1]*10)/10));
        }
        if (action.contains(AC_BUFF)) {
            if (cd[2]<=0)
                buffEffect(hero);
            else
                GLog.i(Messages.get(ElementalGrimoire.class,"delay",Math.floor(cd[2]*10)/10));
        }
    }

    public abstract void utilityEffect(Hero caster);
    public abstract void attackEffect(Hero caster);
    public abstract void buffEffect(Hero caster);

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        for (int i = 0; i<cd.length;i++){
            bundle.put("cd"+i,cd[i]);
        }
        bundle.put("skill", skill);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        for (int i = 0; i<cd.length;i++){
            cd[i]=bundle.getFloat("cd"+i);
        }
        skill=bundle.getFloat("skill");
    }

    public abstract class GrimoireBuff extends Buff {
        protected int level;

        {
            type=buffType.POSITIVE;
            level=0;
        }

        public GrimoireBuff(){
            super();
        }

        public void set(int level){
            this.level=level;
        }

        @Override
        public boolean attachTo(Char target) {
            if (target instanceof Hero) {
                return super.attachTo(target);
            }
            return false;
        }

        @Override
        public boolean act() {
            if (((Hero)target).getSouls()>=soulUsage()) {
                ((Hero)target).spendSouls(soulUsage());
            } else {
                detach();
                return false;
            }
            spend(Actor.TICK);
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("lvl",level);
        }

        protected abstract int soulUsage();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            level=bundle.getInt("lvl");
        }
        @Override
        public String toString() {
            return Messages.get(this, "name"+level);
        }

        @Override
        public String heroMessage() {
            return Messages.get(this, "heromsg"+level);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc"+level);
        }
        @Override
        public int icon() {
            return ico();
        }
        protected abstract int ico();
    }

    @Override
    public void invAct() {
        super.invAct();
        for (int i = 0; i<cd.length;i++){
            cd[i]=Math.max(0,cd[i]-1);
        }
    }
}
