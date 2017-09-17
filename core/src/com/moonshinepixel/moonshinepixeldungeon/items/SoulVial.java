/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.moonshinepixel.moonshinepixeldungeon.items;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.ShadowRage;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Skeleton;
import com.moonshinepixel.moonshinepixeldungeon.effects.Flare;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.*;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndOptions;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.Statistics;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Wraith;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.NPC;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.Armor;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.sprites.*;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndBag;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.VenomGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Burning;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.ShadowRage_visual;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ShadowParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.sprites.*;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class SoulVial extends Item {
    protected String inventoryTitle = Messages.get(this, "inv_title");

    private static final int VOLUME_CAP=666;

	private int max_volume = 0;

	private static final String AC_DRINK	= "DRINK";

	private static final String AC_UNCURSE	= "UNCURSE";

    private static final String AC_SHADOW	= "SHADOW";

	private static final String AC_SHADOW_END	= "SHADOW_END";

    private static final String AC_DIE	= "DIE";

    private static final String AC_SUMMON	= "SUMMON";
    private static final String AC_SUMMON_GREAT	= "SUMMON_GREAT";
    private static final String AC_SUMMON_GREATEST	= "SUMMON_GREATEST";

	private static final float TIME_TO_DRINK = 1f;

	private static final String TXT_STATUS	= "%d/%d";

	private WndBag.Mode mode;

	private int charge;

	public static int volumeCap(){
	    return Math.min((int)(Statistics.deepestFloor*33.3f),666);
//	    return 666;
    }
	{
		image = ItemSpriteSheet.CRYSTAL;

		defaultAction = AC_DRINK;

		unique = true;
	}

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = Math.min(Math.max(volume,0),volumeCap());
    }

    private int volume = 0;

	private int blessType = 0;

	private static final String VOLUME	= "volume";
	private static final String MAXVOLUME= "maxvolume";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( VOLUME, volume );
		bundle.put( MAXVOLUME, max_volume);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		volume	= bundle.getInt( VOLUME );
		max_volume =bundle.getInt( MAXVOLUME );
	}

	/*@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (volume > 0) {
			actions.add( AC_DRINK );
		}
        if (volume>=66){
            actions.add( AC_UNCURSE );
        }
        if (volume>111 && hero.buff(ShadowRage.class)==null){
            actions.add( AC_SHADOW );
        } else if (hero.buff(ShadowRage.class)!=null){
            actions.add(AC_SHADOW_END);
        }
        if (volume>=222){
            actions.add(AC_SUMMON);
        }
        if (volume>=333){
            actions.add(AC_SUMMON_GREAT);
        }
        if (volume>=666){
            actions.add(AC_SUMMON_GREATEST);
        }
//        actions.add(AC_DIE);
		return actions;
	}*/

	@Override
	public void execute( final Hero hero, String action ) {

        super.execute(hero, action);

        if (action.equals(AC_DRINK)) {

            if (volume > 0) {

                int value = 1;
                value *= Math.min(volume, 66);
                int effect = Math.min(hero.HT - hero.HP, value);
                if (effect > 0) {
                    hero.HP += effect;
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), volume > 5 ? 2 : 1);
                    hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "value", effect));
                    volume = volume < effect ? 0 : volume - effect;
                }


                hero.spend(TIME_TO_DRINK);
                hero.busy();

                Sample.INSTANCE.play(Assets.SND_DRINK);
                hero.sprite.operate(hero.pos);

                updateQuickslot();


            } else {
                GLog.w(Messages.get(this, "empty"));
            }

        }
        if (action.equals(AC_UNCURSE)) {
            int lvl = 0;
            if (volume >= 66) {
                lvl++;
		        /*if (volume>=111) {
		            lvl++;
		            if (volume>222){
		                lvl++;
                    }
                }*/
            }
            if (lvl > 0) uncurse(lvl);
        }
        if (action.equals(AC_SHADOW)) {
            if (volume > 111) {
                Buff.affect(hero, ShadowRage.class);
                curUser.spend(3);
                curUser.busy();
                Sample.INSTANCE.play(Assets.SND_READ);
                curUser.sprite.operate(curUser.pos);
            }
        }
        if (action.equals(AC_SHADOW_END)) {
            curUser.spend(1);
            curUser.busy();
            Sample.INSTANCE.play(Assets.SND_READ);
            curUser.sprite.operate(curUser.pos);
            ShadowRage buff = hero.buff(ShadowRage.class);
            if (buff != null) {
                buff.detach();
            }
        }
        if (action.equals(AC_DIE)) {
            hero.damage(hero.HP, hero);
        }
        if (action.equals(AC_SUMMON)) {
            if (volume >= 222) {
                ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Level.getPassable(p) || Level.getAvoid(p))) {
                        spawnPoints.add(p);
                    }
                }

                if (spawnPoints.size() > 0) {
                    Wraith wraith = new Wraith() {

                        @Override
                        protected boolean getCloser(int target) {
                            if (state == WANDERING || Dungeon.level.distance(target, Dungeon.hero.pos) > 6 || Dungeon.level.distance(target, pos) > 10)
                                this.target = target = Dungeon.hero.pos;
                            return super.getCloser(target);
                        }

                        @Override
                        protected Char chooseEnemy() {
                            if (enemy == null || !enemy.isAlive() || !Dungeon.level.mobs.contains(enemy) || state == WANDERING) {

                                HashSet<Mob> enemies = new HashSet<Mob>();
                                for (Mob mob : Dungeon.level.mobs) {
                                    if (mob.hostile && Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE) {
                                        enemies.add(mob);
                                    }
                                }
                                enemy = enemies.size() > 0 ? Random.element(enemies) : null;
                            }
                            return enemy;
                        }
                    };
                    wraith.hostile = false;
                    wraith.ally = true;
                    wraith.pos = Random.element(spawnPoints);
                    Wraith w = wraith;
                    w.adjustStats(Dungeon.fakedepth[Dungeon.depth]);
                    w.state = w.HUNTING;
                    GameScene.add(w, 1);

                    w.sprite.alpha(0);
                    w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

                    w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

                    hero.spend(1f);
                    hero.busy();
                    hero.sprite.operate(hero.pos);
                    Sample.INSTANCE.play(Assets.SND_GHOST);
                    volume -= 222;
                    updateQuickslot();
                } else
                    GLog.i(Messages.get(this, "no_space"));
            }
        }
        if (action.equals(AC_SUMMON_GREAT)) {
            if (volume >= 333) {
                ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Level.getPassable(p) || Level.getAvoid(p))) {
                        spawnPoints.add(p);
                    }
                }

                if (spawnPoints.size() > 0) {
                    Undead wraith = new Undead();
                    wraith.hostile = false;
                    wraith.ally = true;
                    wraith.pos = Random.element(spawnPoints);
                    Undead w = wraith;
                    w.adjustStats(Dungeon.fakedepth[Dungeon.depth]);
                    w.state = w.HUNTING;
                    GameScene.add(w, 1);

                    w.sprite.alpha(0);
                    w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

                    w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

                    hero.spend(1f);
                    hero.busy();
                    hero.sprite.operate(hero.pos);
                    Sample.INSTANCE.play(Assets.SND_GHOST);
                    volume -= 333;
                    updateQuickslot();
                } else
                    GLog.i(Messages.get(this, "no_space"));
            }
        }
        if (action.equals(AC_SUMMON_GREATEST)) {
            if (volume >= 666) {
                ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Level.getPassable(p) || Level.getAvoid(p))) {
                        spawnPoints.add(p);
                    }
                }

                if (spawnPoints.size() > 0) {
                    ShadowHero ghost = new ShadowHero( level() );
                    ghost.pos = Random.element(spawnPoints);

                    GameScene.add(ghost, 1f);
                    hero.spend(1f);
                    hero.busy();
                    hero.sprite.operate(hero.pos);
                    ShadowHero w = ghost;
                    w.sprite.alpha(0);
                    w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

                    w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

                    hero.spend(1f);
                    hero.busy();
                    hero.sprite.operate(hero.pos);
                    Sample.INSTANCE.play(Assets.SND_GHOST);
                    volume -= 666;
                    updateQuickslot();
                } else
                    GLog.i(Messages.get(this, "no_space"));
            }
        }
    }

	public void uncurse(int lvl){
	    charge = 0;
	    switch (lvl){
            case 1:
                mode = WndBag.Mode.UNIDED_OR_CURSED;
                GameScene.selectItem( itemSelector, mode, inventoryTitle );
                charge=66;
                blessType = 1;
                break;
            case 2:
                mode = WndBag.Mode.UPGRADEABLELIMIT;
                GameScene.selectItem( itemSelector, mode, inventoryTitle );
                charge=111;
                blessType = 2;
                break;
            case 3:
                mode = WndBag.Mode.UPGRADEABLELIMIT;
                GameScene.selectItem( itemSelector, mode, inventoryTitle );
                charge=222;
                blessType = 3;
                break;
            default:
        }
    }

    private void applyBless(Item item){
	    switch (blessType){
            case 1:
                ScrollOfRemoveCurse.uncurse(curUser,item);
                break;
            case 2:
                ScrollOfRemoveCurse.uncurse(curUser,item);
                if (item instanceof Weapon){
                    Weapon w = (Weapon)item;
                    boolean hadGoodEnchant = w.hasGoodEnchant();
                    w.upgrade();
                    if (hadGoodEnchant && !w.hasGoodEnchant()){
                        GLog.w( Messages.get(Weapon.class, "incompatible") );
                    }
                } else if (item instanceof Armor){
                    Armor a = (Armor)item;
                    boolean hadGoodGlyph = a.hasGoodGlyph();
                    a.upgrade();
                    if (hadGoodGlyph && !a.hasGoodGlyph()){
                        GLog.w( Messages.get(Armor.class, "incompatible") );
                    }
                } else {
                    item.upgrade();
                }
                Badges.validateItemLevelAquired(item);
                break;
            case 3:
                ScrollOfRemoveCurse.uncurse(curUser,item);
                if (item instanceof Weapon) {
                    if (!(item instanceof Gun)) {
                        if (((Weapon) item).hasGoodEnchant()) {
                            ((Weapon) item).upgrade(true);
                            volume += 111;
                        } else {
                            ((Weapon) item).upgrade(true);
                        }
                    } else {
                        item.upgrade();
                    }
                }
                else if (item instanceof Armor) {
                    if (((Armor) item).hasGoodGlyph()) {
                        ((Armor) item).upgrade(true);
                        charge = 111;
                    } else {
                        ((Armor) item).upgrade(true);
                    }
                }
                else {
                    item.upgrade();
                    charge = 111;
                }
                Badges.validateItemLevelAquired(item);
                new Flare( 6, 32 ).show( curUser.sprite, 2f ) ;
                GLog.p( Messages.get(this, "blesssucces") );
                curUser.spend( 3 );
                curUser.busy();
                Sample.INSTANCE.play( Assets.SND_READ );
                curUser.sprite.operate( curUser.pos );
                break;
        }
        volume=volume>charge?volume-charge:0;
    }

	public void empty() {volume = 0; updateQuickslot();}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	public boolean isFull() {
		return volume >= max_volume;
	}

	public void collectSoul( Char ch ){
		GLog.i( Messages.get(this, "collected", ch.name) );
		volume+=ch.HT;
        if (volume > max_volume) {
            int delta = volume-max_volume;
            max_volume+=delta;
            if (max_volume>=volumeCap()){
                max_volume=volumeCap();
                GLog.p( Messages.get(this, "full") );
            }
            volume = max_volume;
        }
	}

	public void fill() {
		volume = max_volume;
		updateQuickslot();
	}

	@Override
	public String status() {
		return Messages.format( TXT_STATUS, volume, max_volume);
	}

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing( 0x000000 );
    }

    private void confirmCancelation() {
        GameScene.show( new WndOptions( name(), Messages.get(this, "warning"),
                Messages.get(this, "yes"), Messages.get(this, "no") ) {
            @Override
            protected void onSelect( int index ) {
                switch (index) {
                    case 0:
                        break;
                    case 1:
                        GameScene.selectItem( itemSelector, mode, inventoryTitle );
                        break;
                }
            }
            public void onBackPressed() {};
        } );
    }

    protected static WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( Item item ) {
            if (item != null) {
                ((SoulVial)curItem).applyBless(item);
            } else {
//                ((SoulVial)curItem).confirmCancelation();
            }
        }
    };




    public static class ShadowHero extends NPC {

        {
            spriteClass = SoulShadowSprite.class;

            flying = false;

            state = WANDERING;
            enemy = null;

            ally = true;
        }

        public ShadowHero() {
            super();
            Buff.affect(this, ShadowRage_visual.class);

            //double heroes defence skill
            defenseSkill = (Dungeon.hero.lvl+4)*2;
        }

        public ShadowHero(int vialCharge){
            this();
            HP = HT = Dungeon.hero.HT;
        }

        public void saySpawned(){
            Sample.INSTANCE.play( Assets.SND_GHOST );
        }

        public void sayAnhk(){
            Sample.INSTANCE.play( Assets.SND_GHOST );
        }

        public void sayDefeated(){
            Sample.INSTANCE.play( Assets.SND_GHOST );
        }

        public void sayHeroKilled(){
            Sample.INSTANCE.play( Assets.SND_GHOST );
        }

        public void sayBossBeaten(){
            Sample.INSTANCE.play( Assets.SND_GHOST );
        }

        @Override
        protected boolean act() {
            if (Random.Int(10) == 0) damage(HT/20 , this);
            if (!isAlive())
                return true;
            if (!Dungeon.hero.isAlive()){
                sayHeroKilled();
                sprite.die();
                destroy();
                return true;
            }
            return super.act();
        }

        @Override
        protected boolean getCloser( int target ) {
            if (state == WANDERING || Dungeon.level.distance(target, Dungeon.hero.pos) > 6)
                this.target = target = Dungeon.hero.pos;
            return super.getCloser( target );
        }

        @Override
        protected Char chooseEnemy() {
            if (enemy == null || !enemy.isAlive() || !Dungeon.level.mobs.contains(enemy) || state == WANDERING) {

                HashSet<Mob> enemies = new HashSet<Mob>();
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.hostile && Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE) {
                        enemies.add(mob);
                    }
                }
                enemy = enemies.size() > 0 ? Random.element( enemies ) : null;
            }
            return enemy;
        }

        @Override
        public int attackSkill(Char target) {
            //same accuracy as the hero.
            return (defenseSkill/2)+5;
        }

        @Override
        public int damageRoll() {
            int lvl = 10;
            return Dungeon.hero.damageRoll();
        }

        @Override
        public int drRoll() {
            int lvl = (HT-10)/4;
            return Dungeon.hero.drRoll();
        }

        @Override
        public void add( Buff buff ) {
            //in other words, can't be directly affected by buffs/debuffs.
        }

        @Override
        public boolean interact() {
            int curPos = pos;

            moveSprite( pos, Dungeon.hero.pos );
            move( Dungeon.hero.pos );

            Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
            Dungeon.hero.move( curPos );

            Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
            Dungeon.hero.busy();
            return true;
        }

        @Override
        public void die(Object cause) {
            sayDefeated();
            super.die(cause);
        }

        @Override
        public void destroy() {
            super.destroy();
        }

        private static final HashSet<Class<?>> IMMUNITIES = new HashSet<Class<?>>();
        static {
            IMMUNITIES.add( ToxicGas.class );
            IMMUNITIES.add( VenomGas.class );
            IMMUNITIES.add( Burning.class );
            IMMUNITIES.add( ScrollOfPsionicBlast.class );
        }

        @Override
        public HashSet<Class<?>> immunities() {
            return IMMUNITIES;
        }
    }
    public class Undead extends Skeleton {
        {
        spriteClass = SkeletonSprite.class;

		properties.add(Property.UNDEAD);
    }
        public int level;
        public void adjustStats( int level ) {
            this.level = level;

            HP = HT = (1 + level) * 6;
            EXP = 2 + 2 * (level - 1) / 5;
            defenseSkill = attackSkill( null ) / 2;

            enemySeen = true;
        }
        private static final String LEVEL	= "level";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( LEVEL, level );
        }
        @Override
        public void restoreFromBundle( Bundle bundle ) {
            adjustStats( bundle.getInt( LEVEL ) );
            super.restoreFromBundle(bundle);
        }
        @Override
        protected boolean getCloser(int target) {
            if (state == WANDERING || Dungeon.level.distance(target, Dungeon.hero.pos) > 6 || Dungeon.level.distance(target, pos) > 10)
                this.target = target = Dungeon.hero.pos;
            return super.getCloser(target);
        }

        @Override
        protected Char chooseEnemy() {
            if (enemy == null || !enemy.isAlive() || !Dungeon.level.mobs.contains(enemy) || state == WANDERING) {

                HashSet<Mob> enemies = new HashSet<Mob>();
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.hostile && Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE) {
                        enemies.add(mob);
                    }
                }
                enemy = enemies.size() > 0 ? Random.element(enemies) : null;
            }
            return enemy;
        }
    }
}
