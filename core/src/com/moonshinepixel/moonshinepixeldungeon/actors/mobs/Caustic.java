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
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Ooze;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CausticSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Caustic extends Slime {

	{
		spriteClass = CausticSprite.class;
		
		HP = HT = 13;
	}
	
	@Override
	public void die( Object cause ) {
		super.die( cause );

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			Char ch = findChar( pos + PathFinder.NEIGHBOURS8[i] );
			if (ch != null && ch.isAlive()) {
				Buff.affect(ch, Ooze.class);
			}
		}

		//Badges.validateRare( this );
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		if (Random.Int( 4 ) == 0) {
			Buff.affect( enemy, Ooze.class );
		}
		
		return damage;
	}
}
