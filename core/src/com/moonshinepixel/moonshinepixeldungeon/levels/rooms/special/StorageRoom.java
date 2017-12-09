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
package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special;

import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Honeypot;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfLiquidFlame;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;

public class StorageRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		final int floor = Terrain.EMPTY_SP;
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, floor );

		boolean honeyPot = Random.Int( 2 ) == 0;
		
		int n = Random.IntRange( 3, 4 );
		for (int i=0; i < n; i++) {
			int pos;
			do {
				pos = level.pointToCell(random());
			} while (level.map[pos] != floor);
			if (honeyPot){
				level.drop( new Honeypot(), pos);
				honeyPot = false;
			} else
				level.drop( prize( level ), pos );
		}
		
		entrance().set( Room.Door.Type.BARRICADE );
		level.addItemToSpawn(Random.oneOf(new PotionOfLiquidFlame(), new Bomb(), new Bomb()));
	}
	
	private static Item prize(Level level ) {

		if (Random.Int(2) != 0){
			Item prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}
		
		return Generator.random( Random.oneOf(
			Generator.Category.POTION,
			Generator.Category.SCROLL,
			Generator.Category.FOOD,
			Generator.Category.GOLD
		) );
	}
}
