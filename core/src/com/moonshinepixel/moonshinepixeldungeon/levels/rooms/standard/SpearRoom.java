/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2017 Evan Debenham
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

package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard;

import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.DeadlySpearTrap;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.FireTrap;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SpearRoom extends StandardRoom {
	
	@Override
	public float[] sizeCatProbs() {
		return new float[]{4, 1, 0};
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.TRAP );
		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		for (int i=top; i < bottom; i++) {
			for (int j=left; j < right; j++) {
				if (level.map[i*level.width()+j]==Terrain.TRAP) {
					DeadlySpearTrap trap = level.setTrap(new DeadlySpearTrap(), i * level.width() + j);
					trap.reveal();
					if (Random.Int(2)==0){
						trap.active=false;
						Painter.set(level,trap.pos,Terrain.INACTIVE_TRAP);
					}
				}
			}
		}
	}
}
