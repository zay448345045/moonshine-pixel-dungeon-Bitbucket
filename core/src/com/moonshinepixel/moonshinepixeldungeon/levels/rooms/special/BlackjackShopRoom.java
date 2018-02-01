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

import com.moonshinepixel.moonshinepixeldungeon.items.bags.*;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.plants.Plant;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ShopBlob;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Belongings;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Blackjackkeeper;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.*;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.Potion;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.Scroll;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.*;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.Wand;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BlackjackShopRoom extends SpecialRoom {

	private ArrayList<Item> itemsToSpawn;

	int[] map = new int[0];
	@Override
	public int minWidth() {
		return 7;
	}
	
	@Override
	public int minHeight() {
		return 7;
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );

		placeShopkeeper( level );

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}
	}

	protected void placeShopkeeper( Level level ) {

		int pos = level.pointToCell(center());

		Mob shopkeeper = new Blackjackkeeper();
		shopkeeper.pos = pos;
		level.mobs.add( shopkeeper );

	}
	@Override
	public void seal(){
		for (Door door : connected.values()) {
			int cell = door.x + door.y* Dungeon.level.width();
			Dungeon.level.map[cell]=Terrain.WALL;
			GameScene.updateMap(cell);
			Level.setPassable(cell,false);
		}
		map = Dungeon.level.map.clone();
		Painter.fill( Dungeon.level, this, 1, Terrain.EMPTY_SP );
		GameScene.updateMap();
		Dungeon.level.buildFlagMaps();
		Dungeon.observe();
	}

	@Override
	public void unseal(){
		Painter.fill( Dungeon.level, this, 1, Terrain.EMPTY );
		for (int i = left; i < right; i++) {
			for (int j = top; j < bottom; j++) {
				int cell = i + j*Dungeon.level.width();
				if (Dungeon.level.insideMap(cell)) {
					CellEmitter.get(cell).burst(ElmoParticle.FACTORY, 2);
					Dungeon.level.map[cell] = this.map[cell];
				}
			}
		}
		for (Door door : connected.values()) {
			int cell = door.x + door.y*Dungeon.level.width();
			Dungeon.level.map[cell]=Terrain.DOOR;
			GameScene.updateMap(cell);
			Level.setPassable(cell,true);
		}
		GameScene.updateMap();
		Dungeon.level.buildFlagMaps();
		Dungeon.observe();
	}

	public void placeItems( Level level ){

		for (int i = left; i < right; i++) {
			for (int j = top; j < bottom; j++) {
				int cell = i + j*level.width();
				Blob.seed(cell, 1, ShopBlob.class, level);
			}
		}

		if (itemsToSpawn == null)
			itemsToSpawn = level.itemsToBlackJackSpawn;
		Item itm;
		itm = Generator.random(Generator.Category.BOMB);
		itemsToSpawn.add(Generator.random());

		Point itemPlacement = new Point(entrance());
		if (itemPlacement.y == top){
			itemPlacement.y++;
		} else if (itemPlacement.y == bottom) {
			itemPlacement.y--;
		} else if (itemPlacement.x == left){
			itemPlacement.x++;
		} else {
			itemPlacement.x--;
		}

		for (Item item : itemsToSpawn) {

			if (itemPlacement.x == left+1 && itemPlacement.y != top+1){
				itemPlacement.y--;
			} else if (itemPlacement.y == top+1 && itemPlacement.x != right-1){
				itemPlacement.x++;
			} else if (itemPlacement.x == right-1 && itemPlacement.y != bottom-1){
				itemPlacement.y++;
			} else {
				itemPlacement.x--;
			}

			int cell = level.pointToCell(itemPlacement);

			if (level.heaps.get( cell ) != null) {
				do {
					cell = level.pointToCell(random());
				} while (level.heaps.get( cell ) != null || level.findMob( cell ) != null);
			}
			item.random();
			Heap heap = level.drop( item, cell );
			heap.type = Heap.Type.FOR_SALE;
			heap.sprite.drop();
			CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
		}

	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("map",map);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		map = bundle.getIntArray("map");
	}
}
