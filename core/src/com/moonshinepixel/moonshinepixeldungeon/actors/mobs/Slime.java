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
import com.moonshinepixel.moonshinepixeldungeon.sprites.SlimeSprite;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Ooze;
import com.watabou.utils.Random;

public class Slime extends Mob {

	{
		spriteClass = SlimeSprite.class;
		
		HP = HT = 7;
		defenseSkill = 2;
        EXP = Random.chances(new float[]{0,10,5,1});

		maxLvl = 6;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 3 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 8;
	}


	@Override
	public int attackProc( Char enemy, int damage ) {

		if (Random.Int(100)<10){
			Buff.affect(enemy, Ooze.class);
		}

		return damage;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}
}
