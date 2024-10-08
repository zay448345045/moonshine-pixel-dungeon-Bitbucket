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
package com.moonshinepixel.moonshinepixeldungeon.actors.mobs;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Poison;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.Journal;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Grim;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Vampiric;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.MeleeWeapon;
import com.moonshinepixel.moonshinepixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Statue extends Mob {
	
	{
		spriteClass = StatueSprite.class;

		EXP = 0;
		state = PASSIVE;
	}
	
	protected Weapon weapon;
	
	public Statue() {
		super();
		
		do {
			weapon = (Weapon) Generator.random( Generator.Category.WEAPON );
		} while (!(weapon instanceof MeleeWeapon));

		if (((MeleeWeapon) weapon).cursed){
			ScrollOfRemoveCurse.uncurse(null,weapon);
		}
		weapon.identify();
		weapon.enchant( Weapon.Enchantment.random() );
		
		HP = HT = 15 + Dungeon.fakedepth[Dungeon.depth] * 5;
		defenseSkill = 4 + Dungeon.fakedepth[Dungeon.depth];
	}

	@Override
	public void onKill(Char enemy) {
		super.onKill(enemy);
		if (weapon!=null&&weapon.enchantment!=null)weapon.enchantment.onKill(weapon,this,enemy);
	}

	private static final String WEAPON	= "weapon";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( WEAPON, weapon );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		weapon = (Weapon)bundle.get( WEAPON );
	}
	
	@Override
	protected boolean act() {
		if (Dungeon.visible[pos]) {
			Journal.add( Journal.Feature.STATUE );
		}
		return super.act();
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( weapon.min(), weapon.max() );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return (int)((9 + Dungeon.fakedepth[Dungeon.depth]) * weapon.ACC);
	}
	
	@Override
    public float attackDelay() {
		return weapon.DLY;
	}

	@Override
    public boolean canAttack(Char enemy) {
		return Dungeon.level.distance( pos, enemy.pos ) <= weapon.RCH;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, Dungeon.fakedepth[Dungeon.depth] + weapon.defenseFactor(null));
	}
	
	@Override
	public void damage( int dmg, Object src ) {

		if (state == PASSIVE) {
			state = HUNTING;
		}
		
		super.damage( dmg, src );
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		return weapon.proc( this, enemy, damage );
	}
	
	@Override
	public void beckon( int cell ) {
		// Do nothing
	}
	
	@Override
	public void die( Object cause ) {
		Dungeon.level.drop( weapon, pos ).sprite.drop();
		super.die( cause );
	}
	
	@Override
	public void destroy() {
		Journal.remove( Journal.Feature.STATUE );
		super.destroy();
	}
	
	@Override
	public boolean reset() {
		state = PASSIVE;
		return true;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", weapon.tier, weapon.name());
	}
	
	private static final HashSet<Class> RESISTANCES = new HashSet<>();
	private static final HashSet<Class> IMMUNITIES = new HashSet<>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Poison.class );
		RESISTANCES.add( Grim.class );
		IMMUNITIES.add( Vampiric.class );
	}
	
	@Override
	public HashSet<Class> resistances() {
		return RESISTANCES;
	}
	
	@Override
	public HashSet<Class> immunities() {
		return IMMUNITIES;
	}
}
