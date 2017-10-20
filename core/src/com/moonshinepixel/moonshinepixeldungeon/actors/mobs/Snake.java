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

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Poison;
import com.moonshinepixel.moonshinepixeldungeon.items.food.MysteryMeat;
import com.moonshinepixel.moonshinepixeldungeon.sprites.SnakeSprite;
import com.watabou.utils.Random;

public class Snake extends Mob {

	{
		spriteClass = SnakeSprite.class;
		
		HP = HT = 10;
		defenseSkill = 3;
		baseSpeed = 2f;
		
		EXP = Random.chances(new float[]{0,0,0,10,5,1});
		maxLvl = 9;
		
		loot = new MysteryMeat();
		lootChance = 0.167f;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 3 );
	}


	@Override
	public int attackProc(Char enemy, int damage ) {
        Buff.affect(enemy, Poison.class).set(Random.NormalIntRange(damage, damage*2));
		return damage;
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}
}
