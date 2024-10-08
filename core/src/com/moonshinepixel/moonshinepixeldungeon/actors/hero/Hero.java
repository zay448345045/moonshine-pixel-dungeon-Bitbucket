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
package com.moonshinepixel.moonshinepixeldungeon.actors.hero;

import com.moonshinepixel.moonshinepixeldungeon.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.*;
import com.moonshinepixel.moonshinepixeldungeon.effects.Flare;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.items.*;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.Armor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Viscosity;
import com.moonshinepixel.moonshinepixeldungeon.items.grimoires.GrimoireOfWind;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.Key;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfElements;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfFuror;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfMight;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.LightningTrap;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.plants.Sungrass;
import com.moonshinepixel.moonshinepixeldungeon.scenes.*;
import com.moonshinepixel.moonshinepixeldungeon.sprites.HeroSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.NPC;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Obfuscation;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Stone;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Swiftness;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.CapeOfThorns;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.DriedRose;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.HornOfPlenty;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.TimekeepersHourglass;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.Potion;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfMight;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfStrength;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.Scroll;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.Flail;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.AlchemyPot;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.Chasm;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.Door;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.Sign;
import com.moonshinepixel.moonshinepixeldungeon.plants.Earthroot;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndMessage;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndTradeItem;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.effects.CheckedCell;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.AntiMagic;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Flow;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.EtherealChains;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.TalismanOfForesight;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfEvasion;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfForce;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfHaste;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfTenacity;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.MissileWeapon;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndResurrect;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Hero extends Char {

	{
		actPriority = 0; //acts at priority 0, baseline for the rest of behaviour.
	}


	public enum Gender {
		FEMALE,
		MALE
	}

	public static final int MAX_LEVEL = 30;

	public Gender gender = Gender.MALE;

	public static final int STARTING_STR = 10;
	
	private static final float TIME_TO_REST		= 1f;
	private static final float TIME_TO_SEARCH	= 2f;
	
	public HeroClass heroClass = HeroClass.ROGUE;
	public HeroSubClass subClass = HeroSubClass.NONE;
	
	private int attackSkill = 10;
	private int defenseSkill = 5;

	public boolean ready = false;
	private boolean damageInterrupt = true;
	public HeroAction curAction = null;
	public HeroAction lastAction = null;

	private Char enemy;
	
	private Item theKey;
	
	public boolean resting = false;

	public MissileWeapon rangedWeapon = null;
	public Belongings belongings;
	
	public int STR;
	public boolean weakened = false;
	
	public float awareness;
	
	public int lvl = 1;
	public int exp = 0;

	private ArrayList<Mob> visibleEnemies;

	public int potionOfMightBonus;

	//This list is maintained so that some logic checks can be skipped
	// for enemies we know we aren't seeing normally, resultign in better performance
	public ArrayList<Mob> mindVisionEnemies = new ArrayList<>();


	public Class<? extends CharSprite> spriteClass = HeroSprite.class;

	public Hero() {
		super();
		name = MoonshinePixelDungeon.heroName();
		
		HP = HT = 20;
		potionOfMightBonus = 0;
		STR = STARTING_STR;
		awareness = 0.1f;
		
		belongings = new Belongings( this );
		
		visibleEnemies = new ArrayList<Mob>();
	}

	public int STR() {
		int STR = this.STR;

		STR += RingOfMight.getBonus(this, RingOfMight.Might.class);

		return (buff(Weakness.class) != null) ? STR - 2 : STR;
	}
	@Override
	public void updateHT(boolean changeHP){
		int lastHT=HT;
		int ht = 20;
		ht+=5*(lvl-1);

		ht+=potionOfMightBonus;


		if (belongings.misc1 instanceof RingOfMight){
			ht+=belongings.misc1.level()*5;
		}
		if (belongings.misc2 instanceof RingOfMight){
			ht+=belongings.misc2.level()*5;
		}

		Transformation trans = buff(Transformation.class);
		if (trans!=null && trans.mob!=null){
			ht=trans.mob.HT;
		}

		ht-=HTPENALTY;

//		if (buff(WaterHealing.class)!=null)ht*=1.1f;

		ht=Math.max(1,ht);
		HT=ht;
		if ( ht>lastHT&&changeHP){
			HP+=ht-lastHT;
		}
		HP=Math.min(HT,HP);
	}

	public void updateStats(){
		attackSkill=10+lvl;
		defenseSkill=5+lvl;
		Transformation trans = buff(Transformation.class);
		if (trans!=null && trans.mob!=null){
			attackSkill=trans.mob.attackSkill(null);
			defenseSkill=trans.mob.defenseSkill(null);
		}
	}

	private static final String ATTACK		= "attackSkill";
	private static final String DEFENSE		= "defenseSkill";
	private static final String STRENGTH	= "STR";
	private static final String LEVEL		= "lvl";
	private static final String EXPERIENCE	= "exp";
    private static final String GENDER		= "gender";
    private static final String SPRITE		= "spriteclass";
    private static final String POMMOD		= "potionofmightbonus";

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );
		
		heroClass.storeInBundle( bundle );
		subClass.storeInBundle( bundle );
		
		bundle.put( ATTACK, attackSkill );
		bundle.put( DEFENSE, defenseSkill );
		
		bundle.put( STRENGTH, STR );
		
		bundle.put( LEVEL, lvl );
		bundle.put( EXPERIENCE, exp );
        bundle.put( GENDER, gender);
        bundle.put( SPRITE, spriteClass );
        bundle.put( POMMOD, potionOfMightBonus );

		belongings.storeInBundle( bundle );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		
		heroClass = HeroClass.restoreInBundle( bundle );
		subClass = HeroSubClass.restoreInBundle( bundle );
		
		attackSkill = bundle.getInt( ATTACK );
		defenseSkill = bundle.getInt( DEFENSE );
		
		STR = bundle.getInt( STRENGTH );
		updateAwareness();
		
		lvl = bundle.getInt( LEVEL );
		exp = bundle.getInt( EXPERIENCE );
        gender = bundle.getEnum(GENDER, Gender.class);
        spriteClass=bundle.getClass(SPRITE);
        potionOfMightBonus = bundle.getInt(POMMOD);
		
		belongings.restoreFromBundle( bundle );
	}
	
	public static void preview(GamesInProgress.Info info, Bundle bundle ) {
		info.level = bundle.getInt( LEVEL );
		info.heroClass = HeroClass.restoreInBundle( bundle );
	}
	
	public String className() {
		return subClass == null || subClass == HeroSubClass.NONE ? heroClass.title(gender==Gender.MALE?0:1) : subClass.title();
	}

	public String givenName(){
		return name.equals(Messages.get(this, "name")) ? className() : name;
	}
	
	public void live() {
		Buff.affect( this, Regeneration.class );
		Buff.affect( this, Hunger.class );
		Buff.affect( this, ItemActor.class );
	}
	
	public int tier() {
		return belongings.armor == null ? 0 : belongings.armor.spriteId;
	}

	public boolean shoot( Char enemy, MissileWeapon wep ) {

		rangedWeapon = wep;
		boolean result = attack( enemy );
		Invisibility.dispel();
		rangedWeapon = null;

		return result;
	}

	@Override
	public int timeToReloadMod() {
		int mod = 1;
		if (heroClass==HeroClass.GUNSLINGER) if (subClass==HeroSubClass.GANGSTER){ mod*=2/3;} else mod*=1;
		ShadowRage rage = buff(ShadowRage.class);
		if (rage!=null){
		    mod/=Math.max(1, rage.LEVEL/2f);
		    rage.spendCharge((int)rage.LEVEL*2);
        }
		return mod;
	}

    @Override
    public int timeToShootMod() {
        int mod = 1;
        if (heroClass == HeroClass.GUNSLINGER) if (subClass == HeroSubClass.GANGSTER) {
            mod *= 0.5;
        } else mod *= 0.75;
        ShadowRage rage = buff(ShadowRage.class);
        if (rage!=null){
            mod/=Math.max(1, rage.LEVEL/2f);
            rage.spendCharge((int)rage.LEVEL*2);
        }
        return mod;
    }

    @Override
	public int attackSkill( Char target ) {
		float accuracy = 1;
		if (rangedWeapon != null && Dungeon.level.distance( pos, target.pos ) == 1) {
			accuracy *= 0.5f;
		}

        ShadowRage rage = buff(ShadowRage.class);
        if (rage!=null){
            accuracy/=Math.max(1, rage.LEVEL/2f);
        }

		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;
		if (wep != null) {
			return (int)(attackSkill * accuracy * wep.accuracyFactor( this ));
		} else {
			return (int)(attackSkill * accuracy);
		}
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		
		int bonus = RingOfEvasion.getBonus(this, RingOfEvasion.Evasion.class);
		if (buff(WaterHealing.class)!=null)bonus++;

		float evasion = (float)Math.pow( 1.125, bonus );
		if (paralysed > 0) {
			evasion /= 2;
		}
		
		int aEnc = belongings.armor != null ? belongings.armor.STRReq() - STR() : 10 - STR();
		
		if (aEnc > 0) {
			return (int)(defenseSkill * evasion / Math.pow( 1.5, aEnc ));
		} else {

			bonus = 0;
			if (heroClass == HeroClass.ROGUE) bonus += -aEnc;

			if (belongings.armor != null && belongings.armor.hasGlyph(Swiftness.class))
				bonus += 5 + belongings.armor.level()*1.5f;

			return Math.round((defenseSkill + bonus) * evasion);
		}
	}
	
	@Override
	public int drRoll() {
		int dr = 0;
		Barkskin bark = buff(Barkskin.class);

		if (belongings.armor != null) {
			dr += Random.NormalIntRange( belongings.armor.DRMin(), belongings.armor.DRMax());
			if (STR() < belongings.armor.STRReq()){
				dr -= 2*(belongings.armor.STRReq() - STR());
				dr = Math.max(dr, 0);
			}
		}
		if (belongings.weapon != null)  dr += Random.NormalIntRange( 0 , belongings.weapon.defenseFactor( this ) );
		if (bark != null)               dr += Random.NormalIntRange( 0 , bark.level() );

		return dr;
	}
	
	@Override
	public int damageRoll() {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;
		int dmg;
		int bonus = RingOfForce.getBonus(this, RingOfForce.Force.class);

		if (wep != null) {
			dmg = wep.damageRoll( this ) + bonus;
		} else {
			if (bonus != 0){
				dmg = RingOfForce.damageRoll(this);
			} else {
				dmg = Random.NormalIntRange(1, Math.max(STR()-8, 1));
			}
		}
		if (dmg < 0) dmg = 0;
		if (subClass == HeroSubClass.BERSERKER){
			berserk = Buff.affect(this, Berserk.class);
			dmg = berserk.damageFactor(dmg);
		}
		dmg = buff( Fury.class ) != null ? (int)(dmg * 1.5f) : dmg;
		Drunk dr = buff(Drunk.class);
		if (dr!=null){
			dmg+=dmg*dr.dmgMod();
		}
        ShadowRage sr = buff(ShadowRage.class);
        if (sr!=null) {
            dmg *= Math.max(0.5f * sr.LEVEL, 1);
            sr.spendCharge((int) (3 / (0.5f * sr.LEVEL) * 66));
        }
		return dmg;
	}
	
	@Override
	public float speed() {

		float speed = super.speed();

		int hasteLevel = RingOfHaste.getBonus(this, RingOfHaste.Haste.class);

		if (hasteLevel != 0)
			speed *= Math.pow(1.2, hasteLevel);

		Armor armor = belongings.armor;

		if (armor != null){

			if (armor.hasGlyph(Swiftness.class)) {
				speed *= (1.1f + 0.01f * belongings.armor.level());
			} else if (armor.hasGlyph(Flow.class) && Level.water[pos]){
				speed *= (1.5f + 0.05f * belongings.armor.level());
			}
		}
		
		int aEnc = armor != null ? armor.STRReq() - STR() : 0;
		if (aEnc > 0) {
			
			return (float)(speed / Math.pow( 1.2, aEnc ));
			
		} else {

			/*return ((HeroSprite)sprite).sprint( subClass == HeroSubClass.FREERUNNER && !isStarving() ) ?
					invisible > 0 ?
							2f * speed :
							1.5f * speed :
					speed;*/
			return speed;
			
		}
	}

	public boolean canSurpriseAttack(){
		if (belongings.weapon == null || !(belongings.weapon instanceof Weapon))
			return true;

		if (STR() < ((Weapon)belongings.weapon).STRReq())
			return false;

		if (belongings.weapon instanceof Flail && rangedWeapon == null)
			return false;

		return true;
	}

	public boolean canAttack(Char enemy){
		if (enemy == null || pos == enemy.pos)
			return false;
		if (enemy instanceof Mob){
			if (((Mob)enemy).ally)
				return false;
		}
		if (buff(Transformation.class)!=null&&buff(Transformation.class).mob!=null) {
			buff(Transformation.class).mob.pos=pos;
			return buff(Transformation.class).mob.canAttack(enemy);
		}
		//can always attack adjacent enemies
		KindOfWeapon wep = Dungeon.hero.belongings.weapon;
		boolean attackAdj = true;
		if (wep!=null){
			attackAdj=wep.minReachFactor(this)==1;
		}
		if (Dungeon.level.adjacent(pos, enemy.pos) && attackAdj)
			return true;


		if (wep != null && Dungeon.level.distance( pos, enemy.pos ) <= wep.reachFactor(this)){

			boolean[] passable = BArray.not(Level.getSolid(), null);
			for (Mob m : Dungeon.level.mobs)
				passable[m.pos] = false;

			PathFinder.buildDistanceMap(enemy.pos, passable, wep.reachFactor(this));

			return PathFinder.distance[pos] <= wep.reachFactor(this) && PathFinder.distance[pos]>=wep.minReachFactor(this);

		} else {
			return false;
		}
	}
	
	public float attackDelay() {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;
		if (wep != null) {
			
			return wep.speedFactor( this );
						
		} else {
			//Normally putting furor speed on unarmed attacks would be unnecessary
			//But there's going to be that one guy who gets a furor+force ring combo
			//This is for that one guy, you shall get your fists of fury!
			int bonus = RingOfFuror.getBonus(this, RingOfFuror.Furor.class);
			return (float)(0.2 + (1 - 0.2)*Math.pow(0.85, bonus));
		}
	}

	@Override
	public void spend( float time ) {
		TimekeepersHourglass.timeFreeze buff = buff(TimekeepersHourglass.timeFreeze.class);
		if (!(buff != null && buff.processTime(time))) {
            super.spend(time);
        }
	}
	
	public void spendAndNext( float time ) {
		busy();
		spend( time );
		next();
	}
	
	@Override
	public boolean act() {
		
		super.act();

        if (Dungeon.isChallenged(Challenges.AMNESIA)) {
            for (Item itm : belongings.backpack) {
                itm.unIdentifyTry(2);
            }
            if(belongings.weapon!=null) {
                belongings.weapon.unIdentifyTry(1, true);
            }
            if (belongings.armor!=null) {
                belongings.armor.unIdentifyTry(1, true);
            }
            if (belongings.misc1!=null) {
                belongings.misc1.unIdentifyTry(1, true);
            }
            if (belongings.misc2!=null) {
                belongings.misc2.unIdentifyTry(1, true);
            }
        }
		if (paralysed > 0) {
			
			curAction = null;
			
			spendAndNext( TICK );
			return false;
		}
		
		checkVisibleMobs();
		
		if (curAction == null) {
			
			if (resting) {
				spend( TIME_TO_REST ); next();
				Drunk dr = buff(Drunk.class);
				if (dr!=null){
					dr.drunk=Math.max(dr.drunk- Random.IntRange(2,6),0);
				}
				return false;
			}

			ready();
			return false;
			
		} else {
			
			resting = false;
			
			ready = false;
			
			if (curAction instanceof HeroAction.Move) {
				
				return actMove( (HeroAction.Move)curAction );
				
			} else
			if (curAction instanceof HeroAction.Interact) {

				return actInteract( (HeroAction.Interact)curAction );
				
			} else
			if (curAction instanceof HeroAction.Buy) {

				return actBuy( (HeroAction.Buy)curAction );
				
			}else
			if (curAction instanceof HeroAction.PickUp) {

				return actPickUp( (HeroAction.PickUp)curAction );
				
			} else
			if (curAction instanceof HeroAction.OpenChest) {

				return actOpenChest( (HeroAction.OpenChest)curAction );
				
			} else
			if (curAction instanceof HeroAction.Unlock) {

				return actUnlock((HeroAction.Unlock) curAction);
				
			} else
			if (curAction instanceof HeroAction.Descend) {

				return actDescend( (HeroAction.Descend)curAction );
				
			} else
			if (curAction instanceof HeroAction.Ascend) {

				return actAscend( (HeroAction.Ascend)curAction );
				
			} else
			if (curAction instanceof HeroAction.Attack) {

				return actAttack( (HeroAction.Attack)curAction );
				
			} else
			if (curAction instanceof HeroAction.Cook) {

				return actCook( (HeroAction.Cook)curAction );
				
			}
		}
		return false;
	}

	public void busy() {
		ready = false;
	}
	
	private void ready() {
		if (sprite.looping()) sprite.idle();
		curAction = null;
		damageInterrupt = true;
		ready = true;

		AttackIndicator.updateState();
		GameScene.ready();
	}
	
	public void interrupt() {
		if (isAlive() && curAction != null &&
			((curAction instanceof HeroAction.Move && curAction.dst != pos) ||
			(curAction instanceof HeroAction.Ascend || curAction instanceof HeroAction.Descend))) {
			lastAction = curAction;
		}
		curAction = null;
	}
	
	public void resume() {
		curAction = lastAction;
		lastAction = null;
		damageInterrupt = false;
		next();
	}

	private boolean actMove( HeroAction.Move action ) {

		if (getCloser( action.dst )) {

			return true;

		} else {
			if (Dungeon.level.map[pos] == Terrain.SIGN) {
				Sign.read(pos);
			}
			ready();

			return false;
		}
	}
	
	private boolean actInteract( HeroAction.Interact action ) {
		
		NPC npc = action.npc;

		if (Dungeon.level.adjacent( pos, npc.pos )) {
			
			ready();
			sprite.turnTo( pos, npc.pos );
			return npc.interact();
			
		} else {
			
			if (Level.fieldOfView[npc.pos] && getCloser( npc.pos )) {

				return true;

			} else {
				ready();
				return false;
			}
			
		}
	}
	
	private boolean actBuy( HeroAction.Buy action ) {
		int dst = action.dst;
		if (pos == dst || Dungeon.level.adjacent( pos, dst )) {

			ready();
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && heap.type == Heap.Type.FOR_SALE && heap.size() == 1) {
				GameScene.show( new WndTradeItem( heap, true ) );
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actCook( HeroAction.Cook action ) {
		int dst = action.dst;
		if (Dungeon.visible[dst]) {

			ready();
			AlchemyPot.operate( this, dst );
			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}

	private boolean actPickUp( HeroAction.PickUp action ) {
		int dst = action.dst;
		if (pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( pos );
			if (heap != null) {
				Item item = heap.peek();
				if (item.doPickUp( this )) {
					heap.pickUp();

					if (item instanceof Dewdrop
							|| item instanceof TimekeepersHourglass.sandBag
							|| item instanceof DriedRose.Petal
							|| item instanceof Key) {
						//Do Nothing
					} else {

						boolean important =
								((item instanceof ScrollOfUpgrade || item instanceof ScrollOfMagicalInfusion) && ((Scroll)item).isKnown()) ||
								((item instanceof PotionOfStrength || item instanceof PotionOfMight) && ((Potion)item).isKnown());
						if (important) {
							GLog.p( Messages.get(this, "you_now_have", item.name()) );
						} else {
							GLog.i( Messages.get(this, "you_now_have", item.name()) );
						}
					}

					if (!heap.isEmpty()) {
						GLog.i( Messages.get(this, "something_else") );
					}
					curAction = null;
				} else {
					heap.sprite.drop();
					ready();
				}
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actOpenChest( HeroAction.OpenChest action ) {
		int dst = action.dst;
		if (Dungeon.level.adjacent( pos, dst ) || pos == dst) {
			
			Heap heap = Dungeon.level.heaps.get( dst );
			if (heap != null && (heap.type != Heap.Type.HEAP && heap.type != Heap.Type.FOR_SALE)) {
				
				if ((heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST)
						&& belongings.specialKeys[Dungeon.depth] < 1) {

						GLog.w( Messages.get(this, "locked_chest") );
						ready();
						return false;

				}
				
				switch (heap.type) {
				case TOMB:
					Sample.INSTANCE.play( Assets.SND_TOMB );
					Camera.main.shake( 1, 0.5f );
					break;
				case SKELETON:
				case REMAINS:
					break;
				default:
					Sample.INSTANCE.play( Assets.SND_UNLOCK );
				}
				
				spend( Key.TIME_TO_UNLOCK );
				sprite.operate( dst );
				
			} else {
				ready();
			}

			return false;

		} else if (getCloser( dst )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actUnlock( HeroAction.Unlock action ) {
		int doorCell = action.dst;
		if (Dungeon.level.adjacent( pos, doorCell )) {
			
			boolean hasKey = false;
			int door = Dungeon.level.map[doorCell];
			
			if (door == Terrain.LOCKED_DOOR
					&& belongings.ironKeys[Dungeon.depth] > 0) {
				
				hasKey = true;
				
			} else if (door == Terrain.LOCKED_EXIT
					&& belongings.specialKeys[Dungeon.depth] > 0) {

				hasKey = true;
				
			}
			
			if (hasKey) {
				
				spend( Key.TIME_TO_UNLOCK );
				sprite.operate( doorCell );
				
				Sample.INSTANCE.play( Assets.SND_UNLOCK );
				
			} else {
				GLog.w( Messages.get(this, "locked_door") );
				ready();
			}

			return false;

		} else if (getCloser( doorCell )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actDescend( HeroAction.Descend action ) {
		int stairs = action.dst;
		if (pos == stairs && pos == Dungeon.level.exit) {

			if (!Dungeon.level.cleared()&&Dungeon.isChallenged(Challenges.EXTERMINATION)) {
				GameScene.scene.add(new WndMessage(Messages.get(this, "locked", Dungeon.level.initMobs.size())));
				ready();
				return false;
			}

			curAction = null;

			Buff buff = buff(TimekeepersHourglass.timeFreeze.class);
			if (buff != null) buff.detach();

			for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] ))
				if (mob instanceof DriedRose.GhostHero) mob.destroy();
			
			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
			Game.switchScene( InterlevelScene.class );

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actAscend( HeroAction.Ascend action ) {
		int stairs = action.dst;
		if (pos == stairs && pos == Dungeon.level.entrance) {
			
			if (Dungeon.depth == 1) {
				boolean cheated = Dungeon.cheated();
				if (belongings.getItem( Amulet.class ) == null) {
					GameScene.show( new WndMessage( Messages.get(this, "leave") ) );
					ready();
				} else {
					Dungeon.win( Amulet.class, true );
					Dungeon.deleteGame( Dungeon.gameSlot, true );
					DynastyScene.surface=true;
					Game.switchScene( cheated?TitleScene.class:DynastyScene.class );
				}
				
			} else if (Dungeon.depth == 31){
				boolean cheated = Dungeon.cheated();
				if (belongings.getItem( Amulet.class ) == null) {
					GameScene.show( new WndMessage( Messages.get(this, "leave") ) );
					ready();
				} else {
					Dungeon.win( Amulet.class, true );
					Dungeon.deleteGame( Dungeon.gameSlot, true );
					DynastyScene.surface=true;
					Game.switchScene( cheated?TitleScene.class:DynastyScene.class );
				}
			} else {

				if (!Dungeon.level.cleared()&&Dungeon.isChallenged(Challenges.EXTERMINATION)) {
					GameScene.scene.add(new WndMessage(Messages.get(this, "locked", Dungeon.level.initMobs.size())));
					ready();
					return false;
				}

				curAction = null;
				
				Hunger hunger = buff( Hunger.class );
				if (hunger != null && !hunger.isStarving()) {
					hunger.reduceHunger( -Hunger.STARVING / 10 );
				}

				Buff buff = buff(TimekeepersHourglass.timeFreeze.class);
				if (buff != null) buff.detach();

				for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] ))
					if (mob instanceof DriedRose.GhostHero) mob.destroy();

				InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
				Game.switchScene( InterlevelScene.class );
			}

			return false;

		} else if (getCloser( stairs )) {

			return true;

		} else {
			ready();
			return false;
		}
	}
	
	private boolean actAttack( HeroAction.Attack action ) {

		enemy = action.target;
		boolean swap = false;
		if (enemy instanceof Mob) if (((Mob)enemy).ally) swap = true;
        if (!swap) {
            if (enemy.isAlive() && canAttack(enemy) && !isCharmedBy(enemy)) {

                Invisibility.dispel();
                final Transformation tbuff = buff(Transformation.class);
                if (tbuff!=null) {
					tbuff.prepareAttack();

					tbuff.mob.sprite.attack(enemy.pos);

					spend(tbuff.mob.attackDelay());
					sprite.attack(enemy.pos, new Callback() {
						@Override
						public void call() {
							tbuff.agressive=true;
							next();
						}
					});

					tbuff.finishAttack();
				} else {
					spend(attackDelay());

					sprite.attack(enemy.pos);
				}

                return false;

            } else {

                if (Level.fieldOfView[enemy.pos] && getCloser(enemy.pos)) {

                    return true;

                } else {
                    ready();
                    return false;
                }

            }
        } else {
            if(Dungeon.level.adjacent(pos,enemy.pos)){

                int mobpos = enemy.pos;
                int heropos = pos;
                Mob ch=(Mob)enemy;

                sprite.move( heropos, mobpos );
                move( mobpos );

                ch.moveSprite( mobpos, heropos );
                ch.move(heropos);

                if (Dungeon.level.map[pos] == Terrain.DOOR) {
                    Door.enter( pos );
                }

                spendAndNext( 1 / speed() );
                busy();
                interrupt();
            }
            return false;
        }
	}

	public Char enemy(){
		return enemy;
	}
	
	public void rest( boolean fullRest ) {
		spendAndNext( TIME_TO_REST );
		if (!fullRest) {
			sprite.showStatus( CharSprite.DEFAULT, Messages.get(this, "wait") );
		}
		resting = fullRest;
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		KindOfWeapon wep = rangedWeapon != null ? rangedWeapon : belongings.weapon;

		if (wep != null) damage = wep.proc( this, enemy, damage );
			
		switch (subClass) {
		case SNIPER:
			if (rangedWeapon != null) {
				Buff.prolong( this, SnipersMark.class, attackDelay() * 1.1f ).object = enemy.id();
			}
			break;
		default:
		}

		
		return damage;
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		
		Earthroot.Armor armor = buff( Earthroot.Armor.class );
		if (armor != null) {
			damage = armor.absorb( damage );
		}

		Sungrass.Health health = buff( Sungrass.Health.class );
		if (health != null) {
			health.absorb( damage );
		}
		
		if (belongings.armor != null) {
			damage = belongings.armor.proc( enemy, this, damage );
		}
		
		return damage;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
		if ((Dungeon.devoptions&4)!=0){
			dmg=0;
		}

		if (buff(TimekeepersHourglass.timeStasis.class) != null)
			return;

		if (!(src instanceof Hunger || src instanceof Viscosity.DeferedDamage) && damageInterrupt) {
			interrupt();
			resting = false;
		}

		if (this.buff(Drowsy.class) != null){
			Buff.detach(this, Drowsy.class);
			GLog.w( Messages.get(this, "pain_resist") );
		}

		CapeOfThorns.Thorns thorns = buff( CapeOfThorns.Thorns.class );
		if (thorns != null) {
			dmg = thorns.proc(dmg, (src instanceof Char ? (Char)src : null),  this);
		}

		int tenacity = RingOfTenacity.getBonus(this, RingOfTenacity.Tenacity.class);
		if (tenacity != 0) //(HT - HP)/HT = heroes current % missing health.
			dmg = (int)Math.ceil((float)dmg * Math.pow(0.85, tenacity*((float)(HT - HP)/HT)));

		//TODO improve this when I have proper damage source logic
		if (belongings.armor != null && belongings.armor.hasGlyph(AntiMagic.class)
				&& RingOfElements.FULL.contains(src.getClass())){
			dmg -= Random.NormalIntRange(belongings.armor.DRMin(), belongings.armor.DRMax())/3;
		}

		if (subClass == HeroSubClass.BERSERKER && berserk == null){
			berserk = Buff.affect(this, Berserk.class);
		}

		super.damage( dmg, src );
	}

	private void checkVisibleMobs() {
		ArrayList<Mob> visible = new ArrayList<>();

		boolean newMob = false;

		Mob target = null;
		for (Mob m : Dungeon.level.mobs) {
			if (Level.fieldOfView[ m.pos ] && m.hostile && !m.ally) {
				visible.add(m);
				if (!visibleEnemies.contains( m )) {
					newMob = true;
				}

				if (!mindVisionEnemies.contains(m) && QuickSlotButton.autoAim(m) != -1){
					if (target == null){
						target = m;
					} else if (distance(target) > distance(m)) {
						target = m;
					}
				}
			}
		}

		if (target != null && (QuickSlotButton.lastTarget == null ||
							!QuickSlotButton.lastTarget.isAlive() ||
							!Dungeon.visible[QuickSlotButton.lastTarget.pos])){
			QuickSlotButton.target(target);
		}

		if (newMob) {
			interrupt();
			resting = false;
		}

		visibleEnemies = visible;
	}
	
	public int visibleEnemies() {
		return visibleEnemies.size();
	}
	
	public Mob visibleEnemy( int index ) {
		return visibleEnemies.get(index % visibleEnemies.size());
	}
	
	private boolean getCloser( final int target ) {

		if (target == pos)
			return false;

		if (rooted) {
			Camera.main.shake( 1, 1f );
			return false;
		}
		
		int step = -1;
		
		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (Actor.findChar( target ) == null) {
				if (Level.pit[target] && !flying && !Level.getSolid(target)) {
					if (!Chasm.jumpConfirmed){
						Chasm.heroJump(this);
						interrupt();
					} else {
						Chasm.heroFall(target);
					}
					return false;
				}
				if (Level.getPassable(target) || Level.getAvoid(target)) {
					step = target;
				}
			}
			
		} else {

			boolean newPath = false;
			if (path == null || path.isEmpty() || !Dungeon.level.adjacent(pos, path.getFirst()))
				newPath = true;
			else if (path.getLast() != target)
				newPath = true;
			else {
				//looks ahead for path validity, up to length-1 or 2.
				//Note that this is shorter than for mobs, so that mobs usually yield to the hero
				int lookAhead = (int) GameMath.gate(0, path.size()-1, 2);
				for (int i = 0; i < lookAhead; i++){
					int cell = path.get(i);
					if (!Level.getPassable(cell) || (Dungeon.visible[cell] && Actor.findChar(cell) != null)) {
						newPath = true;
						break;
					}
				}
			}

			if (newPath) {

				int len = Dungeon.level.length();
				boolean[] p = Level.getPassable();
				boolean[] v = Dungeon.level.visited;
				boolean[] m = Dungeon.level.mapped;
				boolean[] passable = new boolean[len];
				for (int i = 0; i < len; i++) {
					passable[i] = p[i] && (v[i] || m[i]);
				}

				path = Dungeon.findPath(this, pos, target, passable, Level.fieldOfView);
			}

			if (path == null) return false;
			step = path.removeFirst();

		}

		if (step != -1) {

			int moveTime = 1;
			if (belongings.armor != null && belongings.armor.hasGlyph(Stone.class) &&
							(Dungeon.level.map[pos] == Terrain.DOOR
							|| Dungeon.level.map[pos] == Terrain.OPEN_DOOR
							|| Dungeon.level.map[step] == Terrain.DOOR
							|| Dungeon.level.map[step] == Terrain.OPEN_DOOR )){
				moveTime *= 2;
			}
			sprite.move(pos, step);
			move(step);
            float timeScale = 1;
            ShadowRage rage = buff(ShadowRage.class);
            if (rage!=null){
                timeScale*=Math.max(rage.LEVEL/2,1);
                rage.spendCharge((int)rage.LEVEL);
            }
			spend( moveTime / speed() / timeScale );

			//FIXME this is a fairly sloppy fix for a crash involving pitfall traps.
			//really there should be a way for traps to specify whether action should continue or
			//not when they are pressed.
			return InterlevelScene.mode != InterlevelScene.Mode.FALL;

		} else {

			return false;
			
		}

	}
	
	public boolean handle( int cell ) {

		if (cell == -1) {
			return false;
		}
		
		Char ch;
		Heap heap;
		
		if (Dungeon.level.map[cell] == Terrain.ALCHEMY && cell != pos) {
			
			curAction = new HeroAction.Cook( cell );
			
		} else if (Level.fieldOfView[cell] && (ch = Actor.findChar( cell )) instanceof Mob) {

			if (ch instanceof NPC) {
				curAction = new HeroAction.Interact( (NPC)ch );
			} else {
				curAction = new HeroAction.Attack( ch );
			}

		} else if ((heap = Dungeon.level.heaps.get( cell )) != null
				//moving to an item doesn't auto-pickup when enemies are near...
				&& (visibleEnemies.size() == 0 || cell == pos ||
				//...but only for standard heaps, chests and similar open as normal.
				(heap.type != Heap.Type.HEAP && heap.type != Heap.Type.FOR_SALE))) {

			switch (heap.type) {
			case HEAP:
				curAction = new HeroAction.PickUp( cell );
				break;
			case FOR_SALE:
				curAction = heap.size() == 1 && heap.peek().price() > 0 ?
					new HeroAction.Buy( cell ) :
					new HeroAction.PickUp( cell );
				break;
			default:
				curAction = new HeroAction.OpenChest( cell );
			}
			
		} else if (Dungeon.level.map[cell] == Terrain.LOCKED_DOOR || Dungeon.level.map[cell] == Terrain.LOCKED_EXIT) {
			
			curAction = new HeroAction.Unlock( cell );
			
		} else if (cell == Dungeon.level.exit && (Dungeon.depth < 26 || Dungeon.depth>30)) {
			
			curAction = new HeroAction.Descend( cell );
			
		} else if (cell == Dungeon.level.entrance) {
			
			curAction = new HeroAction.Ascend( cell );
			
		} else  {
			
			curAction = new HeroAction.Move( cell );
			lastAction = null;
			
		}

		if (ready)
			return act();
		else
			return false;
	}
	
	public void earnExp( int exp ) {
		
		this.exp += exp;
		float percent = exp/(float)maxExp();

		EtherealChains.chainsRecharge chains = buff(EtherealChains.chainsRecharge.class);
		if (chains != null) chains.gainExp(percent);

		HornOfPlenty.hornRecharge horn = buff(HornOfPlenty.hornRecharge.class);
		if (horn != null) horn.gainCharge(percent);

		if (subClass == HeroSubClass.BERSERKER){
			berserk = Buff.affect(this, Berserk.class);
			berserk.recover(percent);
		}
		
		boolean levelUp = false;
		while (this.exp >= maxExp()) {
			this.exp -= maxExp();
			if (lvl < MAX_LEVEL) {
				lvl++;
				levelUp = true;
				updateHT();
				updateStats();
			} else {
				Buff.prolong(this, Bless.class, 30f);
				this.exp = 0;

				GLog.p( Messages.get(this, "level_cap"));
				Sample.INSTANCE.play( Assets.SND_LEVELUP );
			}
			
			if (lvl < 10) {
				updateAwareness();
			}
		}
		
		if (levelUp) {
			try {
				if(Dungeon.isChallenged(Challenges.AMNESIA)||Dungeon.isChallenged(Challenges.ANALGESIA))
					GLog.p(Messages.get(this, "new_level_amnesia"), lvl);
				else
					GLog.p(Messages.get(this, "new_level"), lvl);
				sprite.showStatus(CharSprite.POSITIVE, Messages.get(Hero.class, "level_up"));
				Sample.INSTANCE.play(Assets.SND_LEVELUP);
			} catch (Exception e){
				e.printStackTrace();
			}
			
			Badges.validateLevelReached();
		}
	}
	
	public int maxExp() {
		return 5 + lvl * 5;
	}
	
	void updateAwareness() {
		awareness = (float)(1 - Math.pow(
			(heroClass == HeroClass.ROGUE ? 0.85 : 0.90),
			(1 + Math.min( lvl,  9 )) * 0.5
		));
	}
	
	public boolean isStarving() {
		return buff(Hunger.class) != null && ((Hunger)buff( Hunger.class )).isStarving();
	}
	
	@Override
	public void add( Buff buff ) {

		if (buff(TimekeepersHourglass.timeStasis.class) != null)
			return;
		GrimoireOfWind.SlyphBuff sb = buff(GrimoireOfWind.SlyphBuff.class);
		if (sb!=null){

		}
		super.add( buff );

		if (sprite != null) {
			String msg = buff.heroMessage();
			if (msg != null&&!Dungeon.isChallenged(Challenges.ANALGESIA)){
				GLog.w(msg);
			}

			if (buff instanceof Paralysis || buff instanceof Vertigo) {
				interrupt();
			}
		}
		
		BuffIndicator.refreshHero();
	}
	
	@Override
	public void remove( Buff buff ) {
		super.remove( buff );

		BuffIndicator.refreshHero();
	}
	
	@Override
	public int stealth() {
		int stealth = super.stealth();

		stealth += RingOfEvasion.getBonus(this, RingOfEvasion.Evasion.class);

		if (belongings.armor != null && belongings.armor.hasGlyph(Obfuscation.class)){
			stealth += belongings.armor.level();
		}

		stealth=buff(Transformation.class)!=null?buff(Transformation.class).agressive?stealth:stealth+10:stealth;
		return stealth;
	}
	
	@Override
	public void die( Object cause  ) {
		
		curAction = null;

		Ankh ankh = null;

		//look for ankhs in player inventory, prioritize ones which are blessed.
		for (Item item : belongings){
			if (item instanceof Ankh) {
				if (ankh == null || ((Ankh) item).isBlessed()) {
					ankh = (Ankh) item;
				}
			}
		}

        SoulVial sv = belongings.getItem(SoulVial.class);

        if (sv!=null){
            ShadowRage sr = buff(ShadowRage.class);
            if (sr!=null){
                float charge = sv.getVolume();
                float cap = sv.volumeCap();
                this.HP = Math.max((int)(HT*(charge/cap)),1);
                Buff.detach(this, Paralysis.class);
                spend(-cooldown());

                new Flare(8, 32).color(0xFFFFFF, true).show(sprite, 2f);
                CellEmitter.get(this.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
                GameScene.flash(0xFFFFFF);
                Sample.INSTANCE.play( Assets.SND_TELEPORT );
                GLog.w( Messages.get(this, "revivevial") );
                sr.spendCharge(666);
                return;
            }
        }

		if (ankh != null && ankh.isBlessed()) {
			this.HP = HT/4;

			//ensures that you'll get to act first in almost any case, to prevent reviving and then instantly dieing again.
			Buff.detach(this, Paralysis.class);
			spend(-cooldown());

			new Flare(8, 32).color(0xFFFF66, true).show(sprite, 2f);
			CellEmitter.get(this.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

			ankh.detach(belongings.backpack);
            GameScene.flash(0xFFFFFF);
			Sample.INSTANCE.play( Assets.SND_TELEPORT );
			GLog.w( Messages.get(this, "revive") );
			Statistics.ankhsUsed++;

			return;
		}
		
		Actor.fixTime();
		super.die( cause );

		if (ankh == null) {
			
			reallyDie( cause );
			
		} else {
			
			Dungeon.deleteGame( Dungeon.gameSlot, false );
			GameScene.show( new WndResurrect( ankh, cause ) );
			
		}
	}
	
	public static void reallyDie( Object cause ) {
		
		int length = Dungeon.level.length();
		int[] map = Dungeon.level.map;
		boolean[] visited = Dungeon.level.visited;
		boolean[] discoverable = Level.discoverable;
		
		for (int i=0; i < length; i++) {
			
			int terr = map[i];
			
			if (discoverable[i]) {
				
				visited[i] = true;
				if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
					Dungeon.level.discover( i );
				}
			}
		}
		
		Bones.leave();
		
		Dungeon.observe();
		GameScene.updateFog();
				
		Dungeon.hero.belongings.identify();

		int pos = Dungeon.hero.pos;

		ArrayList<Integer> passable = new ArrayList<Integer>();
		for (Integer ofs : PathFinder.NEIGHBOURS8) {
			int cell = pos + ofs;
			if ((Level.getPassable(cell) || Level.getAvoid(cell)) && Dungeon.level.heaps.get( cell ) == null) {
				passable.add( cell );
			}
		}
		Collections.shuffle( passable );

		ArrayList<Item> items = new ArrayList<Item>( Dungeon.hero.belongings.backpack.items );
		for (Integer cell : passable) {
			if (items.isEmpty()) {
				break;
			}

			Item item = Random.element( items );
			try {
				Dungeon.level.drop(item, cell).sprite.drop(pos);
			} catch (Exception e){
				MoonshinePixelDungeon.reportException(e);
			}
			items.remove( item );
		}

		GameScene.gameOver();
		
		if (cause instanceof Hero.Doom) {
			((Hero.Doom)cause).onDeath();
		}
		
		Dungeon.deleteGame( Dungeon.gameSlot, true );
	}

	//effectively cache this buff to prevent having to call buff(Berserk.class) a bunch.
	//This is relevant because we call isAlive during drawing, which has both performance
	//and concurrent modification implications if that method calls buff(Berserk.class)
	private Berserk berserk;

	@Override
	public boolean isAlive() {
		if (subClass == HeroSubClass.BERSERKER
				&& berserk != null
				&& berserk.berserking()
				&& SHLD > 0){
			return true;
		}
		return super.isAlive();
	}

	@Override
	public void move( int step ) {
		super.move( step );
		if (!flying) {
			
			if (Level.water[pos]) {
				Sample.INSTANCE.play( Assets.SND_WATER, 1, 1, Random.Float( 0.8f, 1.25f ) );
			} else {
				Sample.INSTANCE.play( Assets.SND_STEP );
			}
			Dungeon.level.press(pos, this);
		}
	}
	
	@Override
	public void onMotionComplete() {
		Dungeon.observe();
		search( false );
		GameScene.checkKeyHold();
	}
	
	@Override
	public void onAttackComplete() {
		
		AttackIndicator.target(enemy);
		
		boolean hit = attack( enemy );

		if (subClass == HeroSubClass.GLADIATOR){
			if (hit) {
				Buff.affect( this, Combo.class ).hit();
			} else {
				Combo combo = buff(Combo.class);
				if (combo != null) combo.miss();
			}
		}

		if (buff(Transformation.class)!=null){
			buff(Transformation.class).agressive=true;
		}

		curAction = null;

		super.onAttackComplete();
	}
	
	@Override
	public void onOperateComplete() {
		
		if (curAction instanceof HeroAction.Unlock) {

			int doorCell = ((HeroAction.Unlock)curAction).dst;
			int door = Dungeon.level.map[doorCell];

			if (door == Terrain.LOCKED_DOOR){
				belongings.ironKeys[Dungeon.depth]--;
				Level.set( doorCell, Terrain.DOOR );
			} else {
				belongings.specialKeys[Dungeon.depth]--;
				Level.set( doorCell, Terrain.UNLOCKED_EXIT );
			}
			StatusPane.needsKeyUpdate = true;
			
			Level.set( doorCell, door == Terrain.LOCKED_DOOR ? Terrain.DOOR : Terrain.UNLOCKED_EXIT );
			GameScene.updateMap( doorCell );
			
		} else if (curAction instanceof HeroAction.OpenChest) {

			Heap heap = Dungeon.level.heaps.get( ((HeroAction.OpenChest)curAction).dst );
			if (heap.type == Heap.Type.SKELETON || heap.type == Heap.Type.REMAINS) {
				Sample.INSTANCE.play( Assets.SND_BONES );
			} else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST){
				belongings.specialKeys[Dungeon.depth]--;
			}
			StatusPane.needsKeyUpdate = true;
			heap.open( this );
		}
		curAction = null;

		super.onOperateComplete();
	}
	
	public boolean search( boolean intentional ) {
		
		boolean smthFound = false;

		int positive = 0;
		int negative = 0;

		int distance = 1 + positive + negative;

		float level = intentional ? (2 * awareness - awareness * awareness) : awareness;
		if (distance <= 0) {
			level /= 2 - distance;
			distance = 1;
		}
		
		int cx = pos % Dungeon.level.width();
		int cy = pos / Dungeon.level.width();
		int ax = cx - distance;
		if (ax < 0) {
			ax = 0;
		}
		int bx = cx + distance;
		if (bx >= Dungeon.level.width()) {
			bx = Dungeon.level.width() - 1;
		}
		int ay = cy - distance;
		if (ay < 0) {
			ay = 0;
		}
		int by = cy + distance;
		if (by >= Dungeon.level.height()) {
			by = Dungeon.level.height() - 1;
		}

		TalismanOfForesight.Foresight foresight = buff( TalismanOfForesight.Foresight.class );

		//cursed talisman of foresight makes unintentionally finding things impossible.
		if (foresight != null && foresight.isCursed()){
			level = -1;
		}
		
		for (int y = ay; y <= by; y++) {
			for (int x = ax, p = ax + y * Dungeon.level.width(); x <= bx; x++, p++) {
				
				if (Dungeon.visible[p]) {
					
					if (intentional) {
						sprite.parent.addToBack( new CheckedCell( p ) );
					}
					
					if (Level.secret[p] && (intentional || Random.Float() < level)) {
						
						int oldValue = Dungeon.level.map[p];
						
						GameScene.discoverTile( p, oldValue );
						
						Dungeon.level.discover( p );
						
						ScrollOfMagicMapping.discover( p );
						
						smthFound = true;

						if (foresight != null && !foresight.isCursed())
							foresight.charge();
					}
				}
			}
		}

		
		if (intentional) {
			sprite.showStatus( CharSprite.DEFAULT, Messages.get(this, "search") );
			sprite.operate( pos );
			if (foresight != null && foresight.isCursed()){
				GLog.n(Messages.get(this, "search_distracted"));
				spendAndNext(TIME_TO_SEARCH * 3);
			} else {
				spendAndNext(TIME_TO_SEARCH);
			}
			
		}
		
		if (smthFound) {
			GLog.w( Messages.get(this, "noticed_smth") );
			Sample.INSTANCE.play( Assets.SND_SECRET );
			interrupt();
		}
		
		return smthFound;
	}
	
	public void resurrect( int resetLevel ) {
		
		HP = HT;
		Dungeon.gold = 0;
		exp = 0;
		
		belongings.resurrect( resetLevel );

		live();
	}
	
	@Override
	public HashSet<Class> resistances() {
		RingOfElements.Resistance r = buff( RingOfElements.Resistance.class );
		HashSet<Class> res = new HashSet<>();
		try {
			res.addAll(super.resistances());
		} catch (Exception e){

		}
		if (buff(WaterHealing.class)!=null){
			res.add(LightningTrap.LIGHTNING.getClass());
		}
		res.addAll(RingOfElements.resistances( this ));
		return res;
	}
	
	@Override
	public HashSet<Class> immunities() {
		if (buff(Transformation.class)!=null){
			return buff(Transformation.class).mob.immunities();
		}

		HashSet<Class> immunities = new HashSet<Class>();
		for (Buff buff : buffs()){
			for (Class<?> immunity : buff.immunities)
				immunities.add(immunity);
		}
		return immunities;
	}

	@Override
	public void next() {
		if (isAlive())
			super.next();
	}

	public static interface Doom {
		public void onDeath();
	}

	@Override
	public void onKill(Char enemy) {
		super.onKill(enemy);
		if(belongings.weapon instanceof Weapon&&((Weapon) belongings.weapon).enchantment!=null)((Weapon) belongings.weapon).enchantment.onKill((Weapon) belongings.weapon,this,enemy);
	}

	public int getSouls(){
		SoulVial sv=belongings.getItem(SoulVial.class);
		if (sv!=null){
			return sv.getVolume();
		} else return 0;
	}
	public void spendSouls(int num){
        SoulVial sv=belongings.getItem(SoulVial.class);
        if (sv!=null){
            sv.setVolume(sv.getVolume()-num);
        }
    }
}
