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
package com.moonshinepixel.moonshinepixeldungeon.items.armor;

import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.items.EquipableItem;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.curses.AntiEntropy;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Entanglement;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Viscosity;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Affection;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.items.BrokenSeal;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.curses.Corrosion;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.curses.Displacement;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.curses.Metabolism;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.curses.Multiplicity;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.curses.Stench;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.AntiMagic;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Brimstone;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Camouflage;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Flow;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Obfuscation;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Potential;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Repulsion;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Stone;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Swiftness;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.glyphs.Thorns;
import com.moonshinepixel.moonshinepixeldungeon.sprites.HeroSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Armor extends EquipableItem {

	private static final int HITS_TO_KNOW    = 10;

	protected static final String AC_DETACH       = "DETACH";
	
	public int tier;
	public int spriteId;

	private int hitsToKnow = HITS_TO_KNOW;
	
	public Glyph glyph;
	private BrokenSeal seal;
	
	public Armor( int tier ) {
		this.tier = tier;
		this.spriteId = tier;
	}
	public Armor( int tier, int spriteId ) {
		this.tier = tier;
		this.spriteId = spriteId;
	}

	private static final String UNFAMILIRIARITY	= "unfamiliarity";
	private static final String GLYPH			= "glyph";
	private static final String SEAL            = "seal";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, hitsToKnow );
		bundle.put( GLYPH, glyph );
		bundle.put( SEAL, seal);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		if ((hitsToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			hitsToKnow = HITS_TO_KNOW;
		}
		inscribe((Glyph) bundle.get(GLYPH));
		seal = (BrokenSeal)bundle.get(SEAL);
	}

	@Override
	public void reset() {
		super.reset();
		//armor can be kept in bones between runs, the seal cannot.
		seal = null;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (seal != null) actions.add(AC_DETACH);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_DETACH) && seal != null){
			BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			if (seal.level() > 0){
				degrade();
			}
			GLog.i( Messages.get(Armor.class, "detach_seal") );
			hero.sprite.operate(hero.pos);
			if (!seal.collect()){
				Dungeon.level.drop(seal, hero.pos);
			}
			seal = null;
		}
	}

	@Override
	public boolean doEquip( Hero hero ) {
		
		detach(hero.belongings.backpack);

		if (hero.belongings.armor == null || hero.belongings.armor.doUnequip( hero, true, false )) {
			
			hero.belongings.armor = this;
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(Armor.class, "equip_cursed") );
			}
			if(hero.sprite instanceof HeroSprite) {
				((HeroSprite) hero.sprite).updateArmor();
			}
			activate(hero);

			hero.spendAndNext( time2equip( hero ) );
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
			
		}
	}

	@Override
	public void activate(Char ch) {
		if (seal != null) Buff.affect(ch, BrokenSeal.WarriorShield.class).setArmor(this);
	}

	public void affixSeal(BrokenSeal seal){
		this.seal = seal;
		if (seal.level() > 0){
			//doesn't trigger upgrading logic such as affecting curses/glyphs
			level(level()+1);
			Badges.validateItemLevelAquired(this);
		}
		if (isEquipped(Dungeon.hero)){
			Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
		}
	}

	public BrokenSeal checkSeal(){
		return seal;
	}

	@Override
	protected float time2equip( Hero hero ) {
		return 2 / hero.speed();
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			hero.belongings.armor = null;
			if(hero.sprite instanceof HeroSprite) {
				((HeroSprite) hero.sprite).updateArmor();
			}

			BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			return true;

		} else {

			return false;

		}
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.armor == this;
	}

	public final int DRMax(){
		return DRMax(level());
	}

	public int DRMax(int lvl){
		int effectiveTier = tier;
		if (glyph != null) effectiveTier += glyph.tierDRAdjust();
		effectiveTier = Math.max(0, effectiveTier);

		return Math.max(DRMin(lvl), effectiveTier * (2 + lvl));
	}

	public final int DRMin(){
		return DRMin(level());
	}

	public int DRMin(int lvl){
		if (glyph != null && glyph instanceof Stone)
			return 2*lvl;
		else
			return lvl;
	}

	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean inscribe ) {

		if (inscribe && (glyph == null || glyph.curse())){
			inscribe( Glyph.random() );
		} else if (!inscribe && Random.Float() > Math.pow(0.9, level())){
			inscribe(null);
		}

		if (seal != null && seal.level() == 0)
			seal.upgrade();

		return super.upgrade();
	}
	
	public int proc( Char attacker, Char defender, int damage ) {

		if(Random.Int(2)==0)damage(.02f);

		if (glyph != null) {
			damage = glyph.proc( this, attacker, defender, damage );
		}
		
		if (!levelKnown) {
			if (--hitsToKnow <= 0) {
				levelKnown = true;
				GLog.w( Messages.get(Armor.class, "identify") );
				Badges.validateItemLevelAquired( this );
			}
		}
		
		return damage;
	}


	@Override
	public String name() {
		if(givenName.equals("")) {
			return glyph != null && (cursedKnown || !glyph.curse()) ? glyph.name(super.name()) : super.name();
		} else {
			return givenName;
		}
	}
	
	@Override
	public String info() {
		String info = desc();
		
		if (levelKnown) {
			info += "\n\n" + Messages.get(Armor.class, "curr_absorb", DRMin(), DRMax(), STRReq());
			
			if (STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "too_heavy");
			} else if (Dungeon.hero.heroClass == HeroClass.ROGUE && Dungeon.hero.STR() > STRReq()){
				info += " " + Messages.get(Armor.class, "excess_str");
			}
		} else {
			info += "\n\n" + Messages.get(Armor.class, "avg_absorb", DRMin(0), DRMax(0), STRReq(0));

			if (!Dungeon.isChallenged(Challenges.ANALGESIA)&&STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "probably_too_heavy");
			}
		}
		
		if (glyph != null  && (cursedKnown || !glyph.curse())) {
			info += "\n\n" +  Messages.get(Armor.class, "inscribed", glyph.name());
			info += " " + glyph.desc();
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Armor.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Armor.class, "cursed");
		} else if (seal != null) {
			info += "\n\n" + Messages.get(Armor.class, "seal_attached");
		}

		info+=(broken()?"\n"+Messages.get(Item.class,"brokendesc"):"");
		return info;
	}

	@Override
	public Emitter emitter() {
		if (seal == null) return super.emitter();
		Emitter emitter = new Emitter();
		emitter.pos(ItemSpriteSheet.film.width(image)/2f + 2f, ItemSpriteSheet.film.height(image)/3f);
		emitter.fillTarget = false;
		emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
		return emitter;
	}

	@Override
	public Item random() {
		float roll = Random.Float();
		float roll2 =Random.Float();
		if (roll < 0.75f){
			//75% chance to be level 0
		} else if (roll < 0.95f){
			//15% chance to be +1
			upgrade(1);
		} else {
			//5% chance to be +2
			upgrade(2);
		}
		if (roll2 < 0.3f || Dungeon.isChallenged(Challenges.CURSE)){
			//30% chance to be cursed, also inverts level
			level(level()*-1);
			inscribe(Glyph.randomCurse());
			cursed = true;
			return this;
		}

		//if not cursed, 16.67% chance to be inscribed (11.67% overall)
		if (Random.Int(6) == 0)
			inscribe();

		return this;
	}

	public int STRReq(){
		return STRReq(level());
	}

	public int STRReq(int lvl){
		lvl = Math.max(0, lvl);
		float effectiveTier = tier;
		if (glyph != null) effectiveTier += glyph.tierSTRAdjust();
		effectiveTier = Math.max(0, effectiveTier);

		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + Math.round(effectiveTier * 2)) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}
	
	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		if (seal != null) return 0;

		int price = 20 * tier;
		if (hasGoodGlyph()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseGlyph())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public Armor inscribe( Glyph glyph ) {
		this.glyph = glyph;

		return this;
	}

	public Armor inscribe() {

		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		Glyph gl = Glyph.random();
		while (gl.getClass() == oldGlyphClass) {
			gl = Armor.Glyph.random();
		}

		return inscribe( gl );
	}

	public boolean hasGlyph(Class<?extends Glyph> type) {
		return glyph != null && glyph.getClass() == type;
	}

	public boolean hasGoodGlyph(){
		return glyph != null && !glyph.curse();
	}

	public boolean hasCurseGlyph(){
		return glyph != null && glyph.curse();
	}
	
	@Override
	public ItemSprite.Glowing glowing() {
		return glyph != null && (cursedKnown || !glyph.curse()) ? glyph.glowing() : null;
	}
	
	public static abstract class Glyph implements Bundlable {
		
		private static final Class<?>[] glyphs = new Class<?>[]{
				Obfuscation.class, Swiftness.class, Stone.class, Potential.class,
				Brimstone.class, Viscosity.class, Entanglement.class, Repulsion.class, Camouflage.class, Flow.class,
				Affection.class, AntiMagic.class, Thorns.class };
		private static final float[] chances= new float[]{
				10, 10, 10, 10,
				5, 5, 5, 5, 5, 5,
				2, 2, 2 };

		private static final Class<?>[] curses = new Class<?>[]{
				AntiEntropy.class, Corrosion.class, Displacement.class, Metabolism.class, Multiplicity.class, Stench.class
		};
			
		public abstract int proc( Armor armor, Char attacker, Char defender, int damage );
		
		public String name() {
			if (!curse())
				return name( Messages.get(this, "glyph") );
			else
				return name( Messages.get(Item.class, "curse"));
		}
		
		public String name( String armorName ) {
			return Messages.get(this, "name", armorName);
		}

		public String desc() {
			return Messages.get(this, "desc");
		}

		public boolean curse() {
			return false;
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();

		public int tierDRAdjust(){
			return 0;
		}

		public float tierSTRAdjust(){
			return 0;
		}

		public boolean checkOwner( Char owner ) {
			if (!owner.isAlive() && owner instanceof Hero) {

				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "killed", name()) );

				Badges.validateDeathFromGlyph();
				return true;
				
			} else {
				return false;
			}
		}

		@SuppressWarnings("unchecked")
		public static Glyph random() {
			try {
				return ((Class<Glyph>)glyphs[ Random.chances( chances ) ]).newInstance();
			} catch (Exception e) {
				MoonshinePixelDungeon.reportException(e);
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		public static Glyph randomCurse(){
			try {
				return ((Class<Glyph>)Random.oneOf(curses)).newInstance();
			} catch (Exception e) {
				MoonshinePixelDungeon.reportException(e);
				return null;
			}
		}
		
	}
}
