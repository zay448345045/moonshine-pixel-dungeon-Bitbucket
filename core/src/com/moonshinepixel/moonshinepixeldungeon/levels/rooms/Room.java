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

package com.moonshinepixel.moonshinepixeldungeon.levels.rooms;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.RegularLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Graph;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.*;

//Note that this class should be treated as if it were abstract
// it is currently not abstract to maintain compatibility with pre-0.6.0 saves
// TODO make this class abstract after dropping support for pre-0.6.0 saves
public class Room extends Rect implements Graph.Node, Bundlable {
	
	public ArrayList<Room> neigbours = new ArrayList<Room>();
	public LinkedHashMap<Room, Door> connected = new LinkedHashMap<Room, Door>();
	
	public int distance;
	public int price = 1;
	public boolean sealed = false;

	public Room(){
		super();
	}
	
	public Room( Rect other ){
		super(other);
	}
	
	public Room set( Room other ) {
		super.set( other );
		for (Room r : other.neigbours){
			neigbours.add(r);
			r.neigbours.remove(other);
			r.neigbours.add(this);
		}
		for (Room r : other.connected.keySet()){
			Door d = other.connected.get(r);
			r.connected.remove(other);
			r.connected.put(this, d);
			connected.put(r, d);
		}
		return this;
	}



	int[] conrooms;
	int[] neigboursArr;
	Collection<Bundlable> conDoors;


	// **** Spatial logic ****
	
	//Note: when overriding these YOU MUST store any randomly decided values.
	//With the same room and the same parameters these should always return
	//the same value over multiple calls, even if there's some randomness initially.
	public int minWidth(){
		return -1;
	}
	public int maxWidth() { return -1; }
	
	public int minHeight() { return -1; }
	public int maxHeight() { return -1; }
	
	public boolean setSize(){
		return setSize(minWidth(), maxWidth(), minHeight(), maxHeight());
	}
	
	public boolean forceSize( int w, int h ){
		return setSize( w, w, h, h );
	}
	
	public boolean setSizeWithLimit( int w, int h ){
		if ( w < minWidth() || h < minHeight()) {
			return false;
		} else {
			setSize();
			
			if (width() > w || height() > h){
				resize(Math.min(width(), w)-1, Math.min(height(), h)-1);
			}
			
			return true;
		}
	}
	
	protected boolean setSize(int minW, int maxW, int minH, int maxH) {
		if (minW < minWidth()
				|| maxW > maxWidth()
				|| minH < minHeight()
				|| maxH > maxHeight()
				|| minW > maxW
				|| minH > maxH){
			return false;
		} else {
			//subtract one because rooms are inclusive to their right and bottom sides
			resize(Random.NormalIntRange(minW, maxW) - 1,
					Random.NormalIntRange(minH, maxH) - 1);
			return true;
		}
	}

	public void seal(){
		if (!sealed) {
			for (Door door : connected.values()) {
				int cell = door.x + door.y * Dungeon.level.width();
				door.tile = Dungeon.level.map[cell];
				Dungeon.level.map[cell] = Terrain.WALL;
				if (Dungeon.level.map[cell] != door.tile && (Terrain.flags[door.tile]&Terrain.SECRET)==0)
					CellEmitter.get(cell - Dungeon.level.width()).start(Speck.factory(Speck.ROCK), 0.07f, 10);
			}
			GameScene.updateMap();
			Dungeon.level.buildFlagMaps();
			sealed = true;
		}
	}
	public void unseal(){
		if (sealed) {
			sealed = false;
			for (Door door : connected.values()) {
				int cell = door.x + door.y * Dungeon.level.width();
				int lasttile = Dungeon.level.map[cell];
				Dungeon.level.map[cell] = door.tile;
				if (Dungeon.level.map[cell] != lasttile && (Terrain.flags[door.tile]&Terrain.SECRET)==0)
					CellEmitter.get(cell).start(ElmoParticle.FACTORY, 0.01f, 10);
			}
			GameScene.updateMap();
			Dungeon.level.buildFlagMaps();
		}
	}
	
	//Width and height are increased by 1 because rooms are inclusive to their right and bottom sides
	@Override
	public int width() {
		return super.width()+1;
	}
	
	@Override
	public int height() {
		return super.height()+1;
	}
	
	public Point random() {
		return random( 1 );
	}
	
	public Point random( int m ) {
		return new Point( Random.IntRange( left + m, right - m ),
				Random.IntRange( top + m, bottom - m ));
	}

	//a point is only considered to be inside if it is within the 1 tile perimeter
	public boolean inside( Point p ) {
		return p.x > left && p.y > top && p.x < right && p.y < bottom;
	}
	
	public Point center() {
		return new Point(
				(left + right) / 2 + (((right - left) % 2) == 1 ? Random.Int( 2 ) : 0),
				(top + bottom) / 2 + (((bottom - top) % 2) == 1 ? Random.Int( 2 ) : 0) );
	}
	
	
	// **** Connection logic ****
	
	public static final int ALL     = 0;
	public static final int LEFT    = 1;
	public static final int TOP     = 2;
	public static final int RIGHT   = 3;
	public static final int BOTTOM  = 4;
	
	//TODO make abstract
	public int minConnections(int direction){ return -1; }
	
	public int curConnections(int direction){
		if (direction == ALL) {
			return connected.size();
			
		} else {
			int total = 0;
			for (Room r : connected.keySet()){
				Rect i = intersect( r );
				if      (direction == LEFT && i.width() == 0 && i.left == left)         total++;
				else if (direction == TOP && i.height() == 0 && i.top == top)           total++;
				else if (direction == RIGHT && i.width() == 0 && i.right == right)      total++;
				else if (direction == BOTTOM && i.height() == 0 && i.bottom == bottom)  total++;
			}
			return total;
		}
	}
	
	public int remConnections(int direction){
		if (curConnections(ALL) >= maxConnections(ALL)) return 0;
		else return maxConnections(direction) - curConnections(direction);
	}
	
	//TODO make abstract
	public int maxConnections(int direction){ return -1; }
	
	//only considers point-specific limits, not direction limits
	public boolean canConnect(Point p){
		//point must be along exactly one edge, no corners.
		return (p.x == left || p.x == right) != (p.y == top || p.y == bottom);
	}
	
	//only considers direction limits, not point-specific limits
	public boolean canConnect(int direction){
		return remConnections(direction) > 0;
	}
	
	//considers both direction and point limits
	public boolean canConnect( Room r ){
		Rect i = intersect( r );
		
		boolean foundPoint = false;
		for (Point p : i.getPoints()){
			if (canConnect(p) && r.canConnect(p)){
				foundPoint = true;
				break;
			}
		}
		if (!foundPoint) return false;
		
		if (i.width() == 0 && i.left == left)
			return canConnect(LEFT) && r.canConnect(LEFT);
		else if (i.height() == 0 && i.top == top)
			return canConnect(TOP) && r.canConnect(TOP);
		else if (i.width() == 0 && i.right == right)
			return canConnect(RIGHT) && r.canConnect(RIGHT);
		else if (i.height() == 0 && i.bottom == bottom)
			return canConnect(BOTTOM) && r.canConnect(BOTTOM);
		else
			return false;
	}
	
	public boolean addNeigbour( Room other ) {
		if (neigbours.contains(other))
			return true;
		
		Rect i = intersect( other );
		if ((i.width() == 0 && i.height() >= 2) ||
				(i.height() == 0 && i.width() >= 2)) {
			neigbours.add( other );
			other.neigbours.add( this );
			return true;
		}
		return false;
	}
	
	public boolean connect( Room room ) {
		if ((neigbours.contains(room) || addNeigbour(room))
				&& !connected.containsKey( room ) && canConnect(room)) {
			connected.put( room, null );
			room.connected.put( this, null );
			return true;
		}
		return false;
	}
	
	public void clearConnections(){
		for (Room r : neigbours){
			r.neigbours.remove(this);
		}
		neigbours.clear();
		for (Room r : connected.keySet()){
			r.connected.remove(this);
		}
		connected.clear();
	}
	
	// **** Painter Logic ****
	
	//TODO make abstract
	public void paint(Level level){}
	
	//whether or not a painter can make its own modifications to a specific point
	public boolean canModifyTerrain(Point p){
		return inside(p);
	}
	
	public final ArrayList<Point> terrainModifiablePoints(){
		ArrayList<Point> points = new ArrayList<>();
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bottom; j++) {
				Point p = new Point(i, j);
				if (canModifyTerrain(p)) points.add(p);
			}
		}
		return points;
	}
	
	//whether or not a painter can place a trap at a specific point
	public boolean canPlaceTrap(Point p){
		return inside(p);
	}

	public boolean canPlaceMob(Point p){
		return inside(p);
	}

	public Point randomMobCell(){

		HashSet<Point> cells = new HashSet<>();
		for(Point p:getPoints()){
			if (canPlaceMob(p)){
				cells.add(p);
			}
		}
		if (cells.size()>0){
			return Random.element(cells);
		} else {
			return null;
		}
	}

	public final ArrayList<Point> trapPlaceablePoints(){
		ArrayList<Point> points = new ArrayList<>();
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bottom; j++) {
				Point p = new Point(i, j);
				if (canPlaceTrap(p)) points.add(p);
			}
		}
		return points;
	}
	
	
	// **** Graph.Node interface ****
	
	@Override
	public int distance() {
		return distance;
	}
	
	@Override
	public void distance( int value ) {
		distance = value;
	}
	
	@Override
	public int price() {
		return price;
	}
	
	@Override
	public void price( int value ) {
		price = value;
	}
	
	@Override
	public Collection<Room> edges() {
		return neigbours;
	}
	
	public String legacyType = "NULL";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( "left", left );
		bundle.put( "top", top );
		bundle.put( "right", right );
		bundle.put( "bottom", bottom );
		bundle.put( "seal", sealed );
		int[] rooms = new int[connected.keySet().size()];
		int[] neigbours = new int[this.neigbours.size()];
		Iterator<? extends Room> iter = connected.keySet().iterator();
		for (int i = 0; i< rooms.length; i++){
			if (iter.hasNext()){
				Room room = iter.next();
				if (room!=null)
				rooms[i]=room.center().x+room.center().y* Dungeon.level.width();
				else rooms[i]=-1;

			}
		}
		for (int i = 0; i < neigbours.length; i++){
			Room r = this.neigbours.get(i);
			neigbours[i]=r.center().x+r.center().y*Dungeon.level.width();
		}
		bundle.put("neigbours", neigbours);
		bundle.put( "connected", rooms);
		bundle.put( "connectedvals", connected.values());



		if (!legacyType.equals("NULL"))
			bundle.put( "type", legacyType );
	}

	public void postRestore(Level level){
		if (level.version< MoonshinePixelDungeon.v0_1_25)unseal();
		connected = new LinkedHashMap<>();
		int i = 0;
		for (Bundlable door2 : conDoors) {
			Door door = (Door) door2;

			connected.put(level.room(conrooms[i]), door);
			i++;
		}
		neigbours = new ArrayList<>();
		for (i = 0; i < neigboursArr.length; i++) {
			neigbours.add(level.room(neigboursArr[i]));
		}

	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		left = bundle.getInt( "left" );
		top = bundle.getInt( "top" );
		right = bundle.getInt( "right" );
		bottom = bundle.getInt( "bottom" );
		sealed = bundle.getBoolean( "seal" );
		conrooms = bundle.getIntArray("connected");
		conDoors = bundle.getCollection("connectedvals");
		neigboursArr = bundle.getIntArray("neigbours");
		if (bundle.contains( "type" ))
			legacyType = bundle.getString( "type" );
	}

	public void onLevelLoad( Level level ){
		//does nothing by default
	}
	
	public static class Door extends Point implements Bundlable {
		
		public enum Type {
			EMPTY, TUNNEL, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED,
		}
		public Type type = Type.EMPTY;

		public int tile=Terrain.EMPTY;

		public Door(){ super(0,0); }

		public Door( Point p ){
			super(p);
		}
		
		public Door( int x, int y ) {
			super( x, y );
		}
		
		public void set( Type type ) {
			if (type.compareTo( this.type ) > 0) {
				this.type = type;
			}
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			bundle.put("type", type);
			bundle.put("x", x);
			bundle.put("y", y);
			bundle.put("tile", tile);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			type = bundle.getEnum("type", Type.class);
			x = bundle.getInt("x");
			y = bundle.getInt("y");
			tile = bundle.getInt("tile");
		}
	}
}