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
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.IronKey;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class ArmoryRoom extends SpecialRoom {

	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );
		
		Room.Door entrance = entrance();
		Point statue = null;
		if (entrance.x == left) {
			statue = new Point( right-1, Random.Int( 2 ) == 0 ? top+1 : bottom-1 );
		} else if (entrance.x == right) {
			statue = new Point( left+1, Random.Int( 2 ) == 0 ? top+1 : bottom-1 );
		} else if (entrance.y == top) {
			statue = new Point( Random.Int( 2 ) == 0 ? left+1 : right-1, bottom-1 );
		} else if (entrance.y == bottom) {
			statue = new Point( Random.Int( 2 ) == 0 ? left+1 : right-1, top+1 );
		}
		if (statue != null) {
			Painter.set( level, statue, Terrain.STATUE );
		}
		
		int n = Random.IntRange( 1, 2 );
		for (int i=0; i < n; i++) {
			int pos;
			do {
				pos = level.pointToCell(random());
			} while (level.map[pos] != Terrain.EMPTY || level.heaps.get( pos ) != null);
			level.drop( prize( level ), pos );
		}
		
		entrance.set( Room.Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey( Dungeon.depth ) );
	}
	
	private static Item prize(Level level ) {
		return Random.Int( 6 ) == 0 ?
				new Bomb().random() :
				Generator.random( Random.oneOf(
						Generator.Category.ARMOR,
						Generator.Category.WEAPON
				) );
	}
}
