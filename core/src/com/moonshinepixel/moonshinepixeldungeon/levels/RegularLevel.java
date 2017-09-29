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
package com.moonshinepixel.moonshinepixeldungeon.levels;

import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Bestiary;
import com.moonshinepixel.moonshinepixeldungeon.levels.builders.Builder;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.BlackjackShopRoom;
import com.moonshinepixel.moonshinepixeldungeon.Bones;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfWealth;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.PitRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.ShopRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.SpecialRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.FireTrap;
import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Gold;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.Potion;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.Scroll;
import com.moonshinepixel.moonshinepixeldungeon.levels.builders.LoopBuilder;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard.EntranceRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard.ExitRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard.StandardRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.ChillingTrap;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.ExplosiveTrap;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.WornTrap;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndTradeItem;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class RegularLevel extends Level {
	
	protected ArrayList<Room> rooms;
	
	protected Builder builder;
	
	protected Room roomEntrance;
	protected Room roomExit;
	
	public int secretDoors;
	
	@Override
	protected boolean build() {
		
		builder = builder();
		
		ArrayList<Room> initRooms = initRooms();
		Random.shuffle(initRooms);
		
		do {
			for (Room r : initRooms){
				r.neigbours.clear();
				r.connected.clear();
				System.out.println(r.getClass());
			}
			System.out.println("|");
			rooms = builder.build((ArrayList<Room>)initRooms.clone());
		} while (rooms == null);

		System.out.println("|");

		if (painter().paint(this, rooms)){
			placeSign();
			return true;
		} else {
			return false;
		}
		
	}
	
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> initRooms = new ArrayList<>();
		initRooms.add ( roomEntrance = new EntranceRoom());
		initRooms.add( roomExit = new ExitRoom());
		
		int standards = standardRooms();
		for (int i = 0; i < standards; i++) {
			StandardRoom s;
			do {
				s = StandardRoom.createRoom();
			} while (!s.setSizeCat( standards-i ));
			i += s.sizeCat.roomValue-1;
			initRooms.add(s);
		}
		
		if (Dungeon.shopOnLevel())
			initRooms.add(new ShopRoom());

		if (Dungeon.isChallenged(Challenges.BLACKJACK))
            initRooms.add(new BlackjackShopRoom());

		int specials = specialRooms();
		SpecialRoom.initForFloor();
		for (int i = 0; i < specials; i++)
			initRooms.add(SpecialRoom.createRoom());
		
		return initRooms;
	}
	
	protected int standardRooms(){
		return 0;
	}
	
	protected int specialRooms(){
		return 0;
	}
	
	protected Builder builder(){
		return new LoopBuilder()
				.setLoopShape( 2 ,
						Random.Float(0.55f, 0.85f),
						Random.Float(0f, 0.5f));
	}
	
	protected abstract Painter painter();
	
	protected void placeSign(){
		while (true) {
			int pos = pointToCell(roomEntrance.random());
			if (pos != entrance && traps.get(pos) == null && findMob(pos) == null) {
				map[pos] = Terrain.SIGN;
				break;
			}
		}
		
		//teaches new players about secret doors
		if (Dungeon.depth == 2 && !Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_1)) {
			for (Room r : roomEntrance.connected.keySet()) {
				Room.Door d = roomEntrance.connected.get(r);
				if (d.type == Room.Door.Type.REGULAR)
					map[d.x + d.y * width()] = Terrain.SECRET_DOOR;
			}
		}
	}
	
	protected float waterFill(){
		return 0;
	}
	
	protected int waterSmoothing(){
		return 0;
	}
	
	protected float grassFill(){
		return 0;
	}
	
	protected int grassSmoothing(){
		return 0;
	}
	
	protected int nTraps() {
		return Random.NormalIntRange( 1, 3+(Dungeon.fakedepth[Dungeon.depth]/3) );
	}
	
	protected Class<?>[] trapClasses(){
		return new Class<?>[]{WornTrap.class};
	}

	protected float[] trapChances() {
		return new float[]{1};
	}
	
	@Override
	public int nMobs() {
		float mod = Dungeon.isChallenged(Challenges.HORDE)?1:Challenges.hiveMobsMod();
		switch(Dungeon.depth) {
			case 1:
				//mobs are not randomly spawned on floor 1.
				return 0;
			default:
				return (int)((2 + (int)((Dungeon.fakedepth[Dungeon.depth] % 5)*1.25f) + Random.Int(6))*mod);
		}
	}
	
	@Override
	protected void createMobs() {
		float mod = Dungeon.isChallenged(Challenges.HORDE)?1:Challenges.hiveMobsMod();
		//on floor 1, 10 rats are created so the player can get level 2.
		int mobsToSpawn = Dungeon.depth == 1 ? (int)(10*mod*0) : nMobs();
		
		ArrayList<Room> stdRooms = new ArrayList<>();
		for (Room room : rooms) {
			if (room instanceof StandardRoom && room != roomEntrance) {
				for (int i = 0; i < ((StandardRoom) room).sizeCat.roomValue; i++) {
					stdRooms.add(room);
				}
				//pre-0.6.0 save compatibility
			} else if (room.legacyType.equals("STANDARD")){
				stdRooms.add(room);
			}
		}
		Random.shuffle(stdRooms);
		Iterator<Room> stdRoomIter = stdRooms.iterator();
		
		while (mobsToSpawn > 0) {
			if (!stdRoomIter.hasNext())
				stdRoomIter = stdRooms.iterator();
			Room roomToSpawn = stdRoomIter.next();
			
			Mob mob = Bestiary.mob( Dungeon.depth );
			mob.pos = pointToCell(roomToSpawn.random());
			
			if (findMob(mob.pos) == null && getPassable(mob.pos)) {
				mobsToSpawn--;
				mobs.add(mob);
				
				//TODO: perhaps externalize this logic into a method. Do I want to make mobs more likely to clump deeper down?
				if (mobsToSpawn > 0 && Random.Int(4) == 0){
					mob = Bestiary.mob( Dungeon.depth );
					mob.pos = pointToCell(roomToSpawn.random());
					
					if (findMob(mob.pos)  == null && getPassable(mob.pos)) {
						mobsToSpawn--;
						mobs.add(mob);
					}
				}
			}
		}
		
		for (Mob m : mobs){
			if (map[m.pos] == Terrain.HIGH_GRASS) {
				map[m.pos] = Terrain.GRASS;
				setLosBlocking(m.pos,false);
			}
			
		}
		
	}
	
	@Override
	public int randomRespawnCell() {
		int count = 0;
		int cell = -1;
		
		while (true) {
			
			if (++count > 30) {
				return -1;
			}
			
			Room room = randomRoom( StandardRoom.class );
			if (room == null || room == roomEntrance) {
				continue;
			}
			
			cell = pointToCell(room.random());
			if (!Dungeon.visible[cell]
					&& Actor.findChar( cell ) == null
					&& getPassable(cell)
					&& cell != exit) {
				return cell;
			}
			
		}
	}
	
	@Override
	public int randomDestination() {
		
		int cell = -1;
		
		while (true) {
			
			Room room = Random.element( rooms );
			if (room == null) {
				continue;
			}
			
			cell = pointToCell(room.random());
			if (getPassable(cell)) {
				return cell;
			}
			
		}
	}
	
	@Override
	protected void createItems() {
		
		int nItems = 4;
		int bonus = RingOfWealth.getBonus(Dungeon.hero, RingOfWealth.Wealth.class);

		//just incase someone gets a ridiculous ring, cap this at 80%
		bonus = Math.min(bonus, 10);
		while (Random.Float() < (0.3f + bonus*0.05f)) {
			nItems++;
		}

		itemsToBlackJackSpawn=(ArrayList<Item>) itemsToSpawn.clone();

		for (int i=0; i < nItems; i++) {
			Heap.Type type = null;
			switch (Random.Int( 20 )) {
			case 0:
				type = Heap.Type.SKELETON;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				type = Heap.Type.CHEST;
				break;
			case 5:
				type = Dungeon.fakedepth[Dungeon.depth] > 1 ? Heap.Type.MIMIC : Heap.Type.CHEST;
				break;
			default:
				type = Heap.Type.HEAP;
			}
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS) {
				map[cell] = Terrain.GRASS;
				setLosBlocking(cell, false);
			}
			Item itm = Generator.random();
			if (Dungeon.isChallenged(Challenges.BLACKJACK)) {
				if (itm instanceof Gold) {
				} else {
					itemsToBlackJackSpawn.add(itm);
					itm = new Gold((int)(WndTradeItem.prices(itm)*Random.Float(0.5f,0.75f)));
				}
			}
			drop( itm, cell ).type = type;
		}

		for (Item item : itemsToSpawn) {
			int cell;
			do {
				cell = randomDropCell();
				if (item instanceof Scroll) {
					while (traps.get(cell) instanceof FireTrap) {
						cell = randomDropCell();
					}

				} else if (item instanceof Potion) {
					while (traps.get(cell) instanceof ChillingTrap) {
						cell = randomDropCell();
					}
				}
			} while (traps.get(cell) instanceof ExplosiveTrap);
			if (Dungeon.isChallenged(Challenges.BLACKJACK)) {
				item = new Gold((int)(WndTradeItem.prices(item)*Random.Float(0.5f,0.75f)));
			}
			drop( item, cell ).type = Heap.Type.HEAP;
			if (map[cell] == Terrain.HIGH_GRASS) {
				map[cell] = Terrain.GRASS;
				setLosBlocking(cell, false);
			}
		}
		
		Item item = Bones.get();
		if (item != null) {
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS) {
				map[cell] = Terrain.GRASS;
				setLosBlocking(cell, false);
			}
			drop( item, cell ).type = Heap.Type.REMAINS;
		}
	}
	
	protected Room randomRoom( Class<?extends Room> type ) {
		Random.shuffle( rooms );
		for (Room r : rooms) {
			if (type.isInstance(r)
					//compatibility with pre-0.6.0 saves
					|| (type == StandardRoom.class && r.legacyType.equals("STANDARD"))) {
				return r;
			}
		}
		return null;
	}
	
	public Room room( int pos ) {
		for (Room room : rooms) {
			if (room.inside( cellToPoint(pos) )) {
				return room;
			}
		}
		
		return null;
	}
	
	protected int randomDropCell() {
		while (true) {
			Room room = randomRoom( StandardRoom.class );
			if (room != null && room != roomEntrance) {
				int pos = pointToCell(room.random());
				if (getPassable(pos) && pos != exit) {
					return pos;
				}
			}
		}
	}
	
	@Override
	public int fallCell( boolean fallIntoPit ) {
		if (fallIntoPit) {
			for (Room room : rooms) {
				if (room instanceof PitRoom || room.legacyType.equals("PIT")) {
					int result;
					do {
						result = pointToCell(room.random());
					} while (traps.get(result) != null
							|| findMob(result) != null
							|| heaps.get(result) != null);
					return result;
				}
			}
		}
		
		return super.fallCell( false );
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( "rooms", rooms );
		bundle.put( "bjItems", itemsToBlackJackSpawn );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );



		rooms = new ArrayList<>( (Collection<Room>) ((Collection<?>) bundle.getCollection( "rooms" )) );
		itemsToBlackJackSpawn = new ArrayList<>( (Collection<Item>) ((Collection<?>) bundle.getCollection( "bjItems" )) );
		//3 phases of loading
		//First: load all rooms
		for (Room r : rooms) {
			if (r instanceof EntranceRoom || r.legacyType.equals("ENTRANCE")){
				roomEntrance = r;
			} else if (r instanceof ExitRoom  || r.legacyType.equals("EXIT")){
				roomExit = r;
			}
		}
		//Second: fill neigbours and connected sets
		for (Room r : rooms){
			r.postRestore( this );
		}

		//Third: rooms loading is complete, so now you can do fun things with them)
		for (Room r: rooms){
			r.onLevelLoad( this );
		}
	}
	
}
