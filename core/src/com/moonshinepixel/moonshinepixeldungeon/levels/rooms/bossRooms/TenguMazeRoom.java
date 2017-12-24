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
package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.bossRooms;

import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.MoonshineMaze;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.Maze;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;

//TODO figure out where this should go, tunnel room type perhaps?
public class TenguMazeRoom extends Room {

//	public void paint( Level level ) {
//		Painter.fill(level, this, 1, Terrain.EMPTY);
//
//		//true = space, false = wall
//		boolean[][] maze = Maze.generate(this);
//
//		Painter.fill(level, this, 1, Terrain.EMPTY);
//		for (int x = 0; x < maze.length; x++)
//			for (int y = 0; y < maze[0].length; y++) {
//				if (maze[x][y] == Maze.FILLED) {
//					Painter.fill(level, x + left, y + top, 1, 1, Terrain.WALL);
//				}
//			}
//	}
	@Override
	public void paint(Level level) {
		super.paint(level);

		Painter.fill(level, this, 1, Terrain.EMPTY);

		//true = space, false = wall
//		boolean[][] maze = Maze.generate(this);
		boolean[][] maze = new MoonshineMaze(this).array();

		Painter.fill(level, this, 1, Terrain.EMPTY);
		for (int x = 0; x < maze.length; x++)
			for (int y = 0; y < maze[0].length; y++) {
				Painter.set(level, left+x, top+y, maze[x][y] ? Terrain.EMPTY : Terrain.WALL);
			}
		for (Door door : connected.values()) {
			if (door.x==right){
				Painter.set(level, door.x-1,door.y, Terrain.EMPTY);
			}
			if (door.y==bottom){
				Painter.set(level, door.x,door.y-1, Terrain.EMPTY);
			}
			Painter.set(level, door.x,door.y, Terrain.DOOR);
		}
	}

}
