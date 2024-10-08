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

import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.GoldenKey;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.IronKey;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class VaultRoom extends SpecialRoom {

	public void paint( Level level ) {

		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );
		Painter.fill( level, this, 2, Terrain.EMPTY );
		
		int cx = (left + right) / 2;
		int cy = (top + bottom) / 2;
		int c = cx + cy * level.width();
		
		switch (Random.Int( 3 )) {
		
		case 0:
			level.drop( prize( level ), c ).type = Heap.Type.LOCKED_CHEST;
			level.addItemToSpawn( new GoldenKey( Dungeon.depth ) );
			break;
			
		case 1:
			Item i1, i2;
			do {
				i1 = prize( level );
				i2 = prize( level );
			} while (i1.getClass() == i2.getClass());
			level.drop( i1, c ).type = Heap.Type.CRYSTAL_CHEST;
			level.drop( i2, c + PathFinder.NEIGHBOURS8[Random.Int( 8 )]).type = Heap.Type.CRYSTAL_CHEST;
			level.addItemToSpawn( new GoldenKey( Dungeon.depth ) );
			break;
			
		case 2:
			level.drop( prize( level ), c );
			Painter.set( level, c, Terrain.PEDESTAL );
			break;
		}
		
		entrance().set( Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey( Dungeon.depth ) );
	}
	
	private static Item prize( Level level ) {
		return Generator.random( Random.oneOf(
			Generator.Category.WAND,
			Generator.Category.RING,
			Generator.Category.ARTIFACT
		) );
	}
}
