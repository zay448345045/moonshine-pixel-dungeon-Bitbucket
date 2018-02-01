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

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.EverlastingFire;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.TriggerBlob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.triggers.VineTrigger;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.RatKing;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.EverFlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.craftingitems.EmptyItem;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.DarkVenomTrap;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.DeadlySpearTrap;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.SummoningTrap;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.BigYogSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTilemap;
import com.moonshinepixel.moonshinepixeldungeon.tiles.WallBlockingTilemap;
import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.moonshinepixel.moonshinepixeldungeon.utils.ImageToMap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GreatYogBossLevel extends Level {
	
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;

		viewDistance=64;
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_HALLS;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}

	@Override
	protected boolean build() {
		
		setSize(33, 33);
		PathFinder.setMapSize(33, 33);
		map= ImageToMap.mapFromImage(Assets.MAP_YOG2);
		exit= PathFinder.xy2pos(16,1);
		entrance=PathFinder.xy2pos(16,31);
		GameScene.updateMap();

		boolean[] patch = Patch.generate(width, height, 0.30f, 6, true);
		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && patch[i]) {
				map[i] = Terrain.WATER;
			}
		}
		map[entrance]=Terrain.ENTRANCE;
		map[exit]=Terrain.EXIT;

		for (int i = 0; i < length(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
				map[i] = Terrain.EMPTY_DECO;
			}
			if (map[i] == Terrain.WALL && Random.Int(10) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}

		return true;
	}

	@Override
	protected void createMobs() {
	}

	public boolean spawned = false;

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("sp",spawned);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		spawned=bundle.getBoolean("sp");
	}

	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
	}

	@Override
	public void press(int cell, Char ch) {
		if(!spawned) {
			GreatYog yog = new GreatYog();
			yog.spawn(GreatYogBossLevel.this, 16 + 16 * 33);
			yog.state = yog.SLEEPING;
			GameScene.add(yog);
			begin();
		}


		super.press(cell, ch);
	}

	public void begin(){
		spawned=true;
		map[entrance]=Terrain.EMPTY_SP;
		map[exit]=Terrain.EMPTY_SP;
		exit=-1;
		entrance=0;
		int[] newmap = ImageToMap.mapFromImage(Assets.MAP_YOG2);
		for (int i =10;i<23;i++) {
			for (int j = 10; j < 23; j++) {
				int cell = PathFinder.xy2pos(i,j);
				if (map[cell]==Terrain.WALL||map[cell]==Terrain.WALL_DECO){
					map[cell]=newmap[cell];
					CellEmitter.get( cell - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
					GameScene.updateMap(cell);
					buildFlagMaps(cell);
					setLosBlocking(cell,false);
				}
			}
		}
		buildFlagMaps();
		GameScene.updateMap();
		GameScene.updateFog();
		Dungeon.observe();
	}

	Actor a;
	public void win(){
		exit= PathFinder.xy2pos(16,1);
		entrance=PathFinder.xy2pos(16,31);
		Camera.main.shake(2,4.17f);
		int center=16+16*width;
		final PathFinder.Path p = PathFinder.find(Dungeon.hero.pos,center, BArray.or(Level.getPassable(),Level.getAvoid(),null));
		final PathFinder.Path p2 = PathFinder.find(exit,center, BArray.or(Level.getPassable(),Level.getAvoid(),null));
		final PathFinder.Path p3 = PathFinder.find(entrance,center, BArray.or(Level.getPassable(),Level.getAvoid(),null));
		final HashSet<Integer> cells = new HashSet<>();
		final HashSet<Integer> platform = new HashSet<>();
		if(p!=null)
			platform.addAll(p);
		if(p2!=null)
			platform.addAll(p2);
		if(p3!=null)
			platform.addAll(p3);
		platform.addAll(Arrays.asList(entrance,exit,Dungeon.hero.pos));
		platform.remove(0);
		cells.remove(0);
		for (int c=0;c<length;c++){
			if (c!=exit&&c!=Dungeon.hero.pos&&p!=null&&!p.contains(c)&&p2!=null&&!p2.contains(c)){
				if (map[c]==Terrain.EMPTY||map[c]==Terrain.EMPTY_DECO||map[c]==Terrain.EMBERS||map[c]==Terrain.GRASS||map[c]==Terrain.HIGH_GRASS || map[c]==Terrain.WATER)
					cells.add(c);
			}
			if (map[c]==Terrain.CHASM){
				platform.add(c);
			}
		}
		a = new Actor() {
			@Override
			protected boolean act() {
				final Emitter emit = CellEmitter.get(Dungeon.hero.pos);
				final Item proto = new EmptyItem();
				MissileSprite spr = ((MissileSprite) emit.recycle(MissileSprite.class));
				final int[] ci = new int[]{0};
				for (int i = 0; i<8;i++) {
					final int to =-20*width*i;
					spr.reset(0, to, proto, new Callback() {
						@Override
						public void call() {
							for (int cell : (HashSet<Integer>)cells.clone()){
								if (Random.Int(3)==0||ci[0]==3){
									CellEmitter.get( cell - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
									map[cell]=Terrain.CHASM;
									buildFlagMaps(cell);
									GameScene.updateMap(cell);
									cells.remove(cell);
								}
							}
							for (int cell : (HashSet<Integer>)platform.clone()) {
								if ((Random.Int(3)==0||ci[0]==6)&&ci[0]>=4){
									CellEmitter.get( cell - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
									map[cell]=Terrain.EMPTY_SP;
									buildFlagMaps(cell);
									GameScene.updateMap(cell);
									platform.remove(cell);
								}
							}
							if (ci[0]==6){
								map[entrance]=Terrain.ENTRANCE;
								map[exit]=Terrain.EXIT;
							}
							if (ci[0]==7){
								Actor.remove(a);
								next();
								Actor.fixTime();
								MoonshinePixelDungeon.switchNoFade(GameScene.class);
							}
							ci[0]++;
						}
					});
				}
				return false;
			}
		};
		Actor.addDelayed(a,-10);
		Dungeon.hero.spendAndNext(0);
	}
}
