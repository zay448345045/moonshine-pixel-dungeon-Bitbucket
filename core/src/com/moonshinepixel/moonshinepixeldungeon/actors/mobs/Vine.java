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
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.triggers.VineTrigger;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Burning;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Roots;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.sprites.TentacleGreenSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Vine extends Mob {

	{
		spriteClass = TentacleGreenSprite.class;

		HP = HT = 8;
		defenseSkill = 2;

		EXP = 2;

		loot = Generator.Category.SEED;
		lootChance = 1f;

		state = WANDERING = new Waiting();

		maxLvl = 6;
		properties.add(Property.IMMOVABLE);
	}

	@Override
	protected boolean act() {
		chooseEnemy();
		if (!Dungeon.level.adjacent(pos, Dungeon.hero.pos)) {
			EXP=0;
			((TentacleGreenSprite)sprite).playDisapear();
			destroy();
			TriggerBlob.place(pos,Dungeon.level, VineTrigger.class);
		}
		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		if (src instanceof Burning) {
			die(src);
			sprite.die();
			Blob.seed(pos,2, Fire.class);
		} else {
			super.damage(dmg, src);
		}
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		Buff.affect( enemy, Roots.class, 2f );
		return super.attackProc(enemy, damage);
	}

	@Override
	protected boolean getCloser(int target) {
		return true;
	}

	@Override
	protected boolean getFurther(int target) {
		return true;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(1, 3);
	}

	@Override
	public int attackSkill( Char target ) {
		return 8;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	private static final HashSet<Class> IMMUNITIES = new HashSet<>();
	static {
		IMMUNITIES.add( ToxicGas.class );
		IMMUNITIES.add( ParalyticGas.class );
	}

	@Override
	public HashSet<Class> immunities() {
		return IMMUNITIES;
	}

	private class Waiting extends Wandering{}
}
