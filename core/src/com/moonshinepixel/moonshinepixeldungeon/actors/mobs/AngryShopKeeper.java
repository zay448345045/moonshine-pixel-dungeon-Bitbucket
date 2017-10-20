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

import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.Statistics;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.VenomGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Burning;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Frost;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Paralysis;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Roots;
import com.moonshinepixel.moonshinepixeldungeon.items.food.MysteryMeat;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfBlastWave;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.sprites.PiranhaSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ShopKeepAngrySprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class AngryShopKeeper extends Mob {

	{
		spriteClass = ShopKeepAngrySprite.class;

		baseSpeed = 1f;

		EXP = 0;

		properties.add(Property.IMMOVABLE);
		properties.add(Property.MINIBOSS);
	}

	public AngryShopKeeper() {
		super();
		
		HP = HT = 10 + Dungeon.fakedepth[Dungeon.depth] * 5;
		defenseSkill = attackSkill( null ) / 2;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if((new Ballistica(pos,enemy.pos,Ballistica.STOP_TARGET|Ballistica.STOP_TERRAIN).collisionPos==enemy.pos)){
			return true;
		}
		return false;
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2)==0 && Dungeon.level.adjacent(pos,enemy.pos)){
			int oppositeHero = enemy.pos + (enemy.pos - pos);
			Ballistica trajectory = new Ballistica(enemy.pos, oppositeHero, Ballistica.MAGIC_BOLT);
			WandOfBlastWave.throwChar(enemy,trajectory,1);
		}
		return damage;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( Dungeon.fakedepth[Dungeon.depth], 4 + Dungeon.fakedepth[Dungeon.depth] * 2 );
	}

	@Override
	protected float attackDelay() {
		return 1.5f;
	}

	@Override
	public int attackSkill( Char target ) {
		return 10 + Dungeon.fakedepth[Dungeon.depth];
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, Dungeon.fakedepth[Dungeon.depth]);
	}
}
