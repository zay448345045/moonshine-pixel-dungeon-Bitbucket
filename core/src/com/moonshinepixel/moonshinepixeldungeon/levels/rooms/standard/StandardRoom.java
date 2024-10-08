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
package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class StandardRoom extends Room {
	
	public enum SizeCategory {
		
		NORMAL(4, 10, 1),
		LARGE(10, 14, 2),
		GIANT(14, 18, 3);
		
		public final int minDim, maxDim;
		public final int roomValue;
		
		SizeCategory(int min, int max, int val){
			minDim = min;
			maxDim = max;
			roomValue = val;
		}
		
		public int connectionWeight(){
			return roomValue*roomValue;
		}
		
	}
	
	public SizeCategory sizeCat;
	{ setSizeCat(); }
	
	//Note that if a room wishes to allow itself to be forced to a certain size category,
	//but would (effectively) never roll that size category, consider using Float.MIN_VALUE
	public float[] sizeCatProbs(){
		//always normal by default
		return new float[]{1, 0, 0};
	}
	
	public boolean setSizeCat(){
		return setSizeCat(0, SizeCategory.values().length-1);
	}
	
	//assumes room value is always ordinal+1
	public boolean setSizeCat( int maxRoomValue ){
		return setSizeCat(0, maxRoomValue-1);
	}
	
	//returns false if size cannot be set
	public boolean setSizeCat( int minOrdinal, int maxOrdinal ) {
		float[] probs = sizeCatProbs();
		SizeCategory[] categories = SizeCategory.values();
		
		if (probs.length != categories.length) return false;
		
		for (int i = 0; i < minOrdinal; i++)                    probs[i] = 0;
		for (int i = maxOrdinal+1; i < categories.length; i++)  probs[i] = 0;
		
		int ordinal = Random.chances(probs);
		
		if (ordinal != -1){
			sizeCat = categories[ordinal];
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int minWidth() { return sizeCat.minDim; }
	public int maxWidth() { return sizeCat.maxDim; }
	
	@Override
	public int minHeight() { return sizeCat.minDim; }
	public int maxHeight() { return sizeCat.maxDim; }
	
	@Override
	public int minConnections(int direction) {
		if (direction == ALL)   return 1;
		else                    return 0;
	}
	
	@Override
	public int maxConnections(int direction) {
		if (direction == ALL)   return 16;
		else                    return 4;
	}
	
	//FIXME this is a very messy way of handing variable standard rooms
	private static ArrayList<Class<?extends StandardRoom>> rooms = new ArrayList<>();
	static {
		rooms.add(EmptyRoom.class);
		
		rooms.add(RingRoom.class);
		rooms.add(SegmentedRoom.class);
		rooms.add(CaveRoom.class);
		rooms.add(PillarsRoom.class);
		rooms.add(RuinsRoom.class);
		
		rooms.add(GardenRoom.class);
		rooms.add(AquariumRoom.class);
		rooms.add(PlatformRoom.class);
		rooms.add(BurnedRoom.class);
		rooms.add(FissureRoom.class);
		rooms.add(GrassyGraveRoom.class);
		rooms.add(StripedRoom.class);
		rooms.add(StudyRoom.class);
		rooms.add(WoodCircleRoom.class);

		rooms.add(PitRoom.class);
		rooms.add(MazeRoom.class);
	}

	private static float[][] chances = new float[100][];
	static {
		float mod = MoonshinePixelDungeon.previewmode?100:1;
		Arrays.fill(chances, new float[]{11,  1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, .25f, 0,  0.25f, 0.25f});
//		chances[1] =  new float[]{1,  0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
		chances[1] =  new float[]{22,  8, 0, 0, 0, 0,  1, 0, 1, 0, 1, 0, 1, 1, 1.5f,  0, 0};
		chances[2] =  new float[]{22,  8, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, 1.5f,  0.25f, 0.25f};
		chances[4] =  chances[3] = chances[2];
		chances[5] =  new float[]{5,   1, 1, 1, 1, 1,   0, 0, 0, 0, 0, 0, 0, 0, 0,  0.25f, 0.25f};
		
		chances[6] =  new float[]{22,  0, 10, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, .25f*mod,  0.25f, 0.25f};
		chances[10] = chances[9] = chances[8] = chances[7] = chances[6];
		
		chances[11] = new float[]{22,  0, 0, 10, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, .25f*mod,  0.25f, 0.25f};
		chances[15] = chances[14] = chances[13] = chances[12] = chances[11];
		
		chances[16] = new float[]{22,  10, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1, .25f*mod,  0.25f, 0.25f};
		chances[20] = chances[19] = chances[18] = chances[17] = chances[16];
		
		chances[21] = chances[5];
		
		chances[22] = new float[]{22,  0, 0, 0, 0, 10,  1, 1, 1, 1, 1, 1, 1, 1, .25f*mod,  0.25f, 0.25f};
		chances[26] = chances[25] = chances[24] = chances[23] = chances[22];
		chances[31] = new float[]{22,  0, 5, 0, 0, 5,  1, 1, 1, 1, 1, 1, 1, 1, .25f*mod,  0f, 0.5f};
		chances[35] = chances[34] = chances[33] = chances[32] = chances[31];


	}
	
	
	public static StandardRoom createRoom(){
		try{
			return rooms.get(Random.chances(chances[Dungeon.depth])).newInstance();
		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}
	
}
