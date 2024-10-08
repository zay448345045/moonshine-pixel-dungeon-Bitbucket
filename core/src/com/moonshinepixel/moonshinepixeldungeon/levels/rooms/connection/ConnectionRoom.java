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

package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.connection;

import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class ConnectionRoom extends Room {
	
	@Override
	public int minWidth() { return 3; }
	public int maxWidth() { return 10; }
	
	@Override
	public int minHeight() { return 3; }
	public int maxHeight() { return 10; }
	
	@Override
	public int minConnections(int direction) {
		if (direction == ALL)   return 2;
		else                    return 0;
	}
	
	@Override
	public int maxConnections(int direction) {
		if (direction == ALL)   return 16;
		else                    return 4;
	}
	
	@Override
	public boolean canPlaceTrap(Point p) {
		//traps cannot appear in connection rooms on floor 1
		return super.canPlaceTrap(p) && (Dungeon.fakedepth[Dungeon.depth] > 1||Dungeon.isChallenged(Challenges.TRAPS));
	}
	
	//FIXME this is a very messy way of handing variable connection rooms
	private static ArrayList<Class<?extends ConnectionRoom>> rooms = new ArrayList<>();
	static {
		rooms.add(TunnelRoom.class);
		rooms.add(BridgeRoom.class);
		
		rooms.add(PerimeterRoom.class);
		rooms.add(WalkwayRoom.class);
		
		rooms.add(MazeConnectionRoom.class);
		rooms.add(WellConnectorRoom.class);
	}
	
	private static float[][] chances = new float[100][];
	static {
		Arrays.fill(chances, new float[]{1, 1, 1, 1, 1, 1});
		chances[1] =  new float[]{10, 1,  0, 1,  0, 0};
		chances[4] =  chances[3] = chances[2] = chances[1];
		chances[5] =  new float[]{1, 0,  0, 0,  0, 0};
		
		chances[6] =  new float[]{0, 0,  10, 2,  0, 0};
		chances[10] = chances[9] = chances[8] = chances[7] = chances[6];
		
		chances[11] = new float[]{10, 0,  0, 5,  0, 0};
		chances[15] = chances[14] = chances[13] = chances[12] = chances[11];
		
		chances[16] = new float[]{0, 1,  10, 1,  0, 0};
		chances[20] = chances[19] = chances[18] = chances[17] = chances[16];
		
		chances[21] = chances[5];
		
		chances[22] = new float[]{10, 3,  2, 0,  0, 0};
		chances[26] = chances[25] = chances[24] = chances[23] = chances[22];
		chances[31] = new float[]{10, 0,  0, 0,  3, 2};
//		chances[31] = new float[]{0, 0,  0, 0,  0, 2};
		chances[34] = chances[33] = chances[32] = chances[31];
	}
	
	public static ConnectionRoom createRoom(){
		try {
			return rooms.get(Random.chances(chances[Dungeon.depth])).newInstance();
		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}
}
