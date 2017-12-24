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

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashMap;

abstract public class KindOfWeapon extends EquipableItem {

	public Suffix suffix;

	protected static final float TIME_TO_EQUIP = 1f;
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero.belongings.weapon == this;
	}
	
	@Override
	public boolean doEquip( Hero hero ) {

		detachAll( hero.belongings.backpack );
		
		if (hero.belongings.weapon == null || hero.belongings.weapon.doUnequip( hero, true )) {
			
			hero.belongings.weapon = this;
			activate( hero );

			updateQuickslot();
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(KindOfWeapon.class, "cursed") );
			}
			
			hero.spendAndNext( TIME_TO_EQUIP );
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
		}
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (suffix!=null){
			suffix.activate(ch);
		}
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			hero.belongings.weapon = null;
			return true;

		} else {

			return false;

		}
	}

	public int min(){
		int min=min(broken()?0:level());
		if (suffix!=null){
			min=(int)(min*suffix.modifiers()[0]);
		}
		return min;
	}

	public int max(){
		int max=max(broken()?0:level());
		if (suffix!=null){
			max=(int)(max*suffix.modifiers()[1]);
		}
		return max;
	}

	abstract public int min(int lvl);
	abstract public int max(int lvl);

	public int damageRoll( Hero owner ) {
		return Random.NormalIntRange( min(), max() );
	}
	
	public float accuracyFactor(Hero hero ) {
		return 1f;
	}
	
	public float speedFactor( Hero hero ) {
		return suffix!=null?suffix.modifiers()[2]:1f;
	}

	public int reachFactor( Hero hero ){
		return 1;
	}
	public int minReachFactor( Hero hero ){
		return 1;
	}

	public int defenseFactor( Hero hero ) {
		return suffix!=null?(int)suffix.modifiers()[3]:1;
	}
	
	public int proc(Char attacker, Char defender, int damage ) {
		return damage;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("suff",suffix);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		suffix=(Suffix)bundle.get("suff");
	}

	public static abstract class Suffix implements Bundlable {

		private static final Class<?>[] sufixes = new Class<?>[]{};
		private static final float[] chances= new float[]{};

		public static Suffix random(){
			try {
				return (Suffix)sufixes[Random.chances(chances)].newInstance();
			} catch (Exception e){
				MoonshinePixelDungeon.reportException(e);
				return null;
			}
		}

		protected SuffixBuff buff;

		public SuffixBuff buff(){
			if (buff!=null){
				return buff;
			}
			else {
				buff=newBuff();
				return buff;
			}
		}

		protected SuffixBuff newBuff(){
			return new SuffixBuff();
		}

		protected Suffix parent;

		public void activate(Char ch){
			buff().attachTo(ch);
		}

		public boolean curse() {
			return false;
		}

		public abstract int proc(Weapon weapon, Char attacker, Char defender, int damage );

		public float[] modifiers(){
			return new float[]{
					1f,		//min dmg(*)
					1f,		//max dmg(*)
					1f,		//speed(/)
					0f,		//defence(casted to int)(+)
			};
		}

		public boolean act(){
			return true;
		}

		public String desc(){
			return Messages.get(this,"desc");
		}

		@Override
		public void storeInBundle(Bundle bundle) {
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
		}

		public class SuffixBuff extends Buff{
			@Override
			public boolean act() {
				if (parent.buff!=this){
					detach();
					return true;
				}
				return parent.act();
			}
		}
	}
}
