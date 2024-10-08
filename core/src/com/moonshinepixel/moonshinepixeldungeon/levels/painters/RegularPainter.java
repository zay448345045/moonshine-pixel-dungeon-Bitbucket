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

package com.moonshinepixel.moonshinepixeldungeon.levels.painters;

import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.StatueRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard.EmptyRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.Trap;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.levels.Patch;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;

public abstract class RegularPainter extends Painter {
	
	private float waterFill = 0f;
	private int waterSmoothness;
	
	public RegularPainter setWater(float fill, int smoothness){
		waterFill = fill;
		waterSmoothness = smoothness;
		return this;
	}
	
	private float grassFill = 0f;
	private int grassSmoothness;
	
	public RegularPainter setGrass(float fill, int smoothness){
		grassFill = fill;
		grassSmoothness = smoothness;
		return this;
	}
	
	private int nTraps = 0;
	private Class<? extends Trap>[] trapClasses;
	private float[] trapChances;
	
	public RegularPainter setTraps(int num, Class<?>[] classes, float[] chances){
		nTraps = num;
		trapClasses = (Class<? extends Trap>[]) classes;
		trapChances = chances;
		return this;
	}
	
	@Override
	public boolean paint(Level level, ArrayList<Room> rooms) {
		
		//painter can be used without rooms
		if (rooms != null) {
			int leftMost = Integer.MAX_VALUE, topMost = Integer.MAX_VALUE;
			
			for (Room r : rooms) {
				if (r.left < leftMost) leftMost = r.left;
				if (r.top < topMost) topMost = r.top;
			}
			
			//subtract 1 for padding
			leftMost--;
			topMost--;
			
			int rightMost = 0, bottomMost = 0;
			
			for (Room r : rooms) {
				r.shift(-leftMost, -topMost);
				if (r.right > rightMost) rightMost = r.right;
				if (r.bottom > bottomMost) bottomMost = r.bottom;
			}
			
			//add 1 for padding
			rightMost++;
			bottomMost++;
			
			//add 1 to account for 0 values
			level.setSize(rightMost + 1, bottomMost + 1);
		} else {
			//check if the level's size was already initialized by something else
			if (level.length() == 0) return false;
			
			//easier than checking for null everywhere
			rooms = new ArrayList<>();
		}
		
		for (Room r : rooms) {
			placeDoors( r );
			r.paint( level );
		}

		for (Room r : rooms) {
			paintDoors( level, r );
		}
		
		if (waterFill > 0f) {
			paintWater( level, rooms );
		}
		
		if (grassFill > 0f){
			paintGrass( level, rooms );
		}
		
		if (nTraps > 0){
			paintTraps( level, rooms );
		}
		
		decorate( level, rooms );
		
		return true;
	}
	
	protected abstract void decorate(Level level, ArrayList<Room> rooms);
	
	private void placeDoors( Room r ) {
		for (Room n : r.connected.keySet()) {
			Room.Door door = r.connected.get( n );
			if (door == null) {
				
				Rect i = r.intersect( n );
				ArrayList<Point> doorSpots = new ArrayList<>();
				for (Point p : i.getPoints()){
					if (r.canConnect(p) && n.canConnect(p))
						doorSpots.add(p);
				}
				door = new Room.Door(Random.element(doorSpots));
				
				r.connected.put( n, door );
				n.connected.put( r, door );
			}
		}
	}
	
	protected void paintDoors( Level l, Room r ) {
		for (Room n : r.connected.keySet()) {
			
			if (joinRooms( l, r, n )) {
				
				continue;
			}
			
			Room.Door d = r.connected.get( n );
			int door = d.x + d.y * l.width();
			
			switch (d.type) {
				case EMPTY:
					l.map[door] = d.tile = Terrain.EMPTY;
					break;
				case TUNNEL:
					l.map[door] = d.tile =  l.tunnelTile();
					break;
				case REGULAR:
					if (Dungeon.depth <= 1) {
						l.map[door] = d.tile = Terrain.DOOR;
					} else {
						boolean secret = (Dungeon.fakedepth[Dungeon.depth] < 6 ? Random.Int( 12 - Dungeon.fakedepth[Dungeon.depth] ) : Random.Int( 6 )) == 0;
						l.map[door] = d.tile = secret ? Terrain.SECRET_DOOR : Terrain.DOOR;
					}
					break;
				case UNLOCKED:
					l.map[door] = d.tile = Terrain.DOOR;
					break;
				case HIDDEN:
					l.map[door] = d.tile = Terrain.SECRET_DOOR;
					break;
				case BARRICADE:
					l.map[door] = d.tile = Terrain.BARRICADE;
					break;
				case LOCKED:
					l.map[door] = d.tile = Terrain.LOCKED_DOOR;
					break;
			}
		}
	}
	
	protected boolean joinRooms( Level l, Room r, Room n ) {
		
		if (!(r instanceof EmptyRoom && n instanceof EmptyRoom)) {
			return false;
		}
		
		//TODO decide on good probabilities and dimension restrictions
		Rect w = r.intersect( n );
		if (w.left == w.right) {
			
			if (w.bottom - w.top < 3) {
				return false;
			}
			
			if (w.height()+1 == Math.max( r.height(), n.height() )) {
				return false;
			}
			
			if (r.width() + n.width() > 10) {
				return false;
			}
			
			w.top += 1;
			w.bottom -= 0;
			
			w.right++;
			
			fill( l, w.left, w.top, 1, w.height(), Terrain.EMPTY );
			
		} else {
			
			if (w.right - w.left < 3) {
				return false;
			}
			
			if (w.width()+1 == Math.max( r.width(), n.width() )) {
				return false;
			}
			
			if (r.height() + n.height() > 10) {
				return false;
			}
			
			w.left += 1;
			w.right -= 0;
			
			w.bottom++;
			
			fill( l, w.left, w.top, w.width(), 1, Terrain.EMPTY );
		}
		
		return true;
	}
	
	protected void paintWater( Level l, ArrayList<Room> rooms ){
		boolean[] lake = Patch.generate( l.width(), l.height(), waterFill, waterSmoothness, true );
		
		if (!rooms.isEmpty()){
			for (Room r : rooms){
				for (Point p : r.terrainModifiablePoints()){
					int i = l.pointToCell(p);
					if (lake[i] && l.map[i] == Terrain.EMPTY){
						l.map[i] = Terrain.WATER;
					}
				}
			}
		} else {
			for (int i = 0; i < l.length(); i ++) {
				if (lake[i] && l.map[i] == Terrain.EMPTY){
					l.map[i] = Terrain.WATER;
				}
			}
		}
		
	}
	
	protected void paintGrass( Level l, ArrayList<Room> rooms ) {
		boolean[] grass = Patch.generate( l.width(), l.height(), grassFill, grassSmoothness, true );
		
		ArrayList<Integer> grassCells = new ArrayList<>();
		
		if (!rooms.isEmpty()){
			for (Room r : rooms){
				for (Point p : r.terrainModifiablePoints()){
					int i = l.pointToCell(p);
					if (grass[i] && l.map[i] == Terrain.EMPTY){
						grassCells.add(i);
					}
				}
			}
		} else {
			for (int i = 0; i < l.length(); i ++) {
				if (grass[i] && l.map[i] == Terrain.EMPTY){
					grassCells.add(i);
				}
			}
		}
		
		//Adds chaos to grass height distribution. Ratio of high grass depends on fill and smoothing
		//Full range is 8.3% to 75%, but most commonly (20% fill with 3 smoothing) is around 60%
		//low smoothing, or very low fill, will begin to push the ratio down, normally to 50-30%
		for (int i : grassCells) {
			if (l.heaps.get(i) != null || l.findMob(i) != null) {
				l.map[i] = Terrain.GRASS;
				continue;
			}
			
			int count = 1;
			for (int n : PathFinder.NEIGHBOURS8) {
				try {
					if (grass[i + n]) {
						count++;
					}
				} catch (Exception ignored){}
			}
			l.map[i] = (Random.Float() < count / 12f) ? Terrain.HIGH_GRASS : Terrain.GRASS;
		}
	}
	
	protected void paintTraps( Level l, ArrayList<Room> rooms ) {
		ArrayList<Integer> validCells = new ArrayList<>();
		
		if (!rooms.isEmpty()){
			for (Room r : rooms){
				for (Point p : r.trapPlaceablePoints()){
					int i = l.pointToCell(p);
					if (l.map[i] == Terrain.EMPTY){
						validCells.add(i);
					}
				}
			}
		} else {
			for (int i = 0; i < l.length(); i ++) {
				if (l.map[i] == Terrain.EMPTY){
					validCells.add(i);
				}
			}
		}
		
		//no more than one trap every 5 valid tiles (1 with traps challenge).
		nTraps = Math.min(nTraps, validCells.size()/(Dungeon.isChallenged(Challenges.TRAPS)?1:5));
		
		for (int i = 0; i < nTraps; i++) {
			
			Integer trapPos = Random.element(validCells);
			validCells.remove(trapPos); //removes the integer object, not at the index
			
			try {
				Trap trap = trapClasses[Random.chances( trapChances )].newInstance().hide();
				l.setTrap( trap, trapPos );
				//some traps will not be hidden
				l.map[trapPos] = trap.visible ? Terrain.TRAP : Terrain.SECRET_TRAP;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
