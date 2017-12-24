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
package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.PinCushion;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.items.EquipableItem;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfSharpshooting;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Projecting;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.watabou.utils.Random;

import java.util.ArrayList;

abstract public class MissileWeapon extends Weapon {

	{
		stackable = true;
		levelKnown = true;

		defaultAction = Item.AC_THROW;
		usesTargeting = true;

		renameable = false;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( EquipableItem.AC_EQUIP );
		return actions;
	}

	@Override
	public int throwPos(Hero user, int dst) {
		if (hasEnchant(Projecting.class)
				&& !Level.getSolid(dst) && Dungeon.level.distance(user.pos, dst) <= 4){
			return dst;
		} else {
			return super.throwPos(user, dst);
		}
	}

	@Override
    public void onThrow(int cell) {
		Char enemy = Actor.findChar( cell );
		if (enemy == null || enemy == Item.curUser) {
			if (this instanceof Boomerang)
				super.onThrow( cell );
			else
				miss( cell );
		} else {
			if (!Item.curUser.shoot( enemy, this )) {
				miss( cell );
			} else if (!(this instanceof Boomerang)){

				int bonus = RingOfSharpshooting.getBonus(Item.curUser, RingOfSharpshooting.Aim.class);

				if (Item.curUser.heroClass == HeroClass.HUNTRESS && enemy.buff(PinCushion.class) == null)
					bonus += 3;

				if (Random.Float() > Math.pow(0.7, bonus)){
					if (enemy.isAlive())
						Buff.affect(enemy, PinCushion.class).stick(this);
					else
						Dungeon.level.drop( this, enemy.pos).sprite.drop();
				}

			}
		}
	}
	
	protected void miss( int cell ) {
		int bonus = RingOfSharpshooting.getBonus(Item.curUser, RingOfSharpshooting.Aim.class);

		//degraded ring of sharpshooting will even make missed shots break.
		if (Random.Float() < Math.pow(0.6, -bonus))
			super.onThrow( cell );
	}
	
	@Override
	public int proc( Char attacker, Char defender, int damage ) {
		
		Hero hero = (Hero)attacker;
		if (hero.rangedWeapon == null && stackable) {
			if (quantity == 1) {
				doUnequip( hero, false, false );
			} else {
				detach( null );
			}
		}

		return super.proc( attacker, defender, damage );

	}
	
	@Override
	public Item random() {
		return this;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public String info() {

		String info = desc();
		
		info += "\n\n" + Messages.get( MissileWeapon.class, "stats", imbue.damageFactor(min()), imbue.damageFactor(max()), STRReq());

		if (STRReq() > Dungeon.hero.STR()) {
			info += " " + Messages.get(Weapon.class, "too_heavy");
		} else if (Dungeon.hero.heroClass == HeroClass.HUNTRESS && Dungeon.hero.STR() > STRReq()){
			info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		}

		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");

		info+=(broken()?"\n"+Messages.get(Item.class,"brokendesc"):"");
		return info;
	}

	@Override
	public Item damage(float durability) {
		return getClass().equals(Boomerang.class)?super.damage(durability):this;
	}
}
