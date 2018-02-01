package com.moonshinepixel.moonshinepixeldungeon.actors.mobs;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.levels.GreatYogBossLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.BigYogSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.InvisbleMobSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BossHealthBar;
import com.moonshinepixel.moonshinepixeldungeon.ui.HealthIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameArrays;
import com.watabou.utils.PathFinder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GreatYog extends Mob {
    HashSet<Integer> body;

    {
        HP=HT=1;
        spriteClass= BigYogSprite.class;
        properties.add(Property.BOSS);
        properties.add(Property.IMMOVABLE);
        properties.add(Property.DEMONIC);
        flying = true;
        defFlying = true;
    }

    public void spawn(Level level, int pos){
        body = new HashSet<>();
        this.pos=pos;
        for (int c: PathFinder.NEIGHBOURS24){
            int cell = c+pos;
            DummyPart dp = new DummyPart();
            dp.pos=cell;
            GameScene.add(dp);
            body.add(dp.id());
            dp.parentID=id();
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        for (Integer i:body){
            ((Mob)Char.findById(i)).destroy();
        }
        ((GreatYogBossLevel)Dungeon.level).win();
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    public void notice() {
        super.notice();
        BossHealthBar.assignBoss(this);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        int[] arr = (int[])GameArrays.simplify(body.toArray(new Integer[0]));
        bundle.put("body",arr);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        BossHealthBar.assignBoss(this);
        int[] arr = bundle.getIntArray("body");
        body=new HashSet<>();
        body.addAll(Arrays.asList((Integer[]) GameArrays.wrap(arr)));
    }

    @Override
    public void beckon( int cell ) {
    }

    public class DummyPart extends Mob {
        public GreatYog parent=null;
        public int parentID=-1;

        {
            HP=HT=1;
            renderHPBar=false;
            properties.add(Property.BOSS);
            properties.add(Property.IMMOVABLE);
            properties.add(Property.DEMONIC);
            flying = true;
            defFlying = true;
            spriteClass= InvisbleMobSprite.class;
            diactivate();
            EXP=0;
            hostile=false;
        }

        public DummyPart(){
            super();
            new Updater().attachTo(this);
            state=HUNTING;
        }

        public void updateParrent(){
            try {
                parent = (GreatYog) Char.findById(parentID);
                HP=parent.HP;
                HT=parent.HT;
            } catch (Exception ignored){

            }
        }

        @Override
        public void damage(int dmg, Object src) {
            updateParrent();
            parent.damage(dmg,src);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("par",parentID);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            parentID=bundle.getInt("par");
        }

        @Override
        public int defenseSkill(Char enemy) {
            updateParrent();
            return parent.defenseSkill(enemy);
        }

        @Override
        public HashSet<Class> immunities() {
            updateParrent();
            if (parent!=null)
            return parent.immunities();
            else return super.immunities();
        }

        @Override
        public HashSet<Class> resistances() {
            updateParrent();
            if (parent!=null)
            return parent.resistances();
            else return super.immunities();
        }

        @Override
        protected boolean getCloser(int target) {
            return false;
        }

        @Override
        protected boolean getFurther(int target) {
            return false;
        }

        @Override
        public boolean doAttack(Char enemy) {
            spend(Actor.TICK);
            next();
            return false;
        }

        @Override
        public void onAttackComplete() {
            next();
        }

        public class Updater extends Buff{
            @Override
            public boolean act() {
                DummyPart.this.updateParrent();
                spend(TICK);
                return true;
            }
        }
    }
}
