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
import com.moonshinepixel.moonshinepixeldungeon.Bones;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.EverlastingFire;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ParalyticGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.SmokeGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Light;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.StoneSnake;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.EverFlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.craftingitems.EmptyItem;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.SkeletonKey;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.FireTrap;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.StoneSnakeHeadSprite;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.*;

public class VolcanoBossLevel extends Level {
	
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	HashSet<Integer> igniteTargs = new HashSet<>();
	int snake = 8*width()+CENTER;

	private static final int TOP			= 2;
	private static final int HALL_WIDTH		= 12;
	private static final int HALL_HEIGHT	= 24;
	private static final int CHAMBER_HEIGHT	= 4;

	private static final int WIDTH = 32;
	
	private static final int LEFT	= (WIDTH - HALL_WIDTH) / 2;
	private static final int CENTER	= LEFT + HALL_WIDTH / 2;
	
	private int arenaDoor;
	private boolean enteredArena = false;
	private boolean keyDropped = false;
	
	@Override
	public String tilesTex() {
		return Assets.TILES_VOLCANO;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}
	
	private static final String DOOR	= "door";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	private static final String IGNITECELLS	= "ignitecells";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DOOR, arenaDoor );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
		bundle.put( IGNITECELLS, igniteTargs.toArray(new Integer[0]) );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		arenaDoor = bundle.getInt( DOOR );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
		int[] targs = bundle.getIntArray(IGNITECELLS);
		for (int t: targs){
			igniteTargs.add(t);
		}
	}
	
	@Override
	protected boolean build() {
		
		setSize(32, 32);
		Arrays.fill(map,Terrain.WALL);
		Painter.fill( this, LEFT, TOP, HALL_WIDTH, HALL_HEIGHT, Terrain.EMPTY );
		Painter.fill( this, CENTER, TOP, 1, HALL_HEIGHT, Terrain.EMPTY_SP );
		
		int y = TOP + 1;
		while (y < TOP + HALL_HEIGHT) {
			map[y * width() + CENTER - (2+(HALL_WIDTH-7)/2)] = Terrain.INACTIVE_TRAP;
			map[y * width() + CENTER + (2+(HALL_WIDTH-7)/2)] = Terrain.INACTIVE_TRAP;
//			setTrap(new FireTrap(),y * width() + CENTER - (2+(HALL_WIDTH-7)/2)).reveal().active=false;
//			setTrap(new FireTrap(),y * width() + CENTER + (2+(HALL_WIDTH-7)/2)).reveal().active=false;
			igniteTargs.add(y * width() + CENTER - (2+(HALL_WIDTH-7)/2));
			igniteTargs.add(y * width() + CENTER + (2+(HALL_WIDTH-7)/2));
//			Blob.seed(y * width() + CENTER - (2+(HALL_WIDTH-7)/2),1, EverlastingFire.class,this);
//			Blob.seed(y * width() + CENTER + (2+(HALL_WIDTH-7)/2),1, EverlastingFire.class,this);
			y += 2;
		}
		
		int left = pedestal( true );
		int right = pedestal( false );
		map[left] = map[right] = Terrain.PEDESTAL;
		for (int i=left+1; i < right; i++) {
			map[i] = Terrain.EMPTY_SP;
		}
		
		exit = (TOP - 1) * width() + CENTER;
		map[exit] = Terrain.LOCKED_EXIT;
		
		arenaDoor = (TOP + HALL_HEIGHT) * width() + CENTER;
		map[arenaDoor] = Terrain.DOOR;
		
		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, CHAMBER_HEIGHT, Terrain.EMPTY );
		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, HALL_WIDTH, 1, Terrain.WALL);
		map[arenaDoor + width()] = Terrain.EMPTY;
		Painter.fill( this, LEFT, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.WALL );
		Painter.fill( this, LEFT + HALL_WIDTH - 1, TOP + HALL_HEIGHT + 1, 1, CHAMBER_HEIGHT, Terrain.WALL );

		Painter.fill(this,CENTER,2,1,8,Terrain.WALL);

		entrance = (TOP + HALL_HEIGHT + 3 + Random.Int( CHAMBER_HEIGHT - 2 )) * width() + LEFT + (/*1 +*/ Random.Int( HALL_WIDTH-2 ));
		map[entrance] = Terrain.EMPTY;
		
		for (int i=0; i < length() - width(); i++) {
			if (map[i] == Terrain.EMPTY && Random.Int( 10 ) == 0) {
				map[i] = Terrain.EMPTY_DECO;
			} else if (map[i] == Terrain.WALL
					&& DungeonTileSheet.floorTile(map[i + width()])
					&& Random.Int( 21 - Dungeon.depth ) == 0) {
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		int sign = arenaDoor + 2*width() + 1;
		map[sign] = Terrain.SIGN;
		
		return true;
	}
	
	public int pedestal( boolean left ) {
		if (left) {
			return (TOP + HALL_HEIGHT / 2) * width() + CENTER - (2+(HALL_WIDTH-7)/2);
		} else {
			return (TOP + HALL_HEIGHT / 2) * width() + CENTER + (2+(HALL_WIDTH-7)/2);
		}
	}
	
	@Override
	protected void createMobs() {
	}
	
	public Actor respawner() {
		return null;
	}
	
	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos =
					Random.IntRange( LEFT + 1, LEFT + HALL_WIDTH - 2 ) +
					Random.IntRange( TOP + HALL_HEIGHT + 1, TOP + HALL_HEIGHT  + CHAMBER_HEIGHT ) * width();
			} while (pos == entrance || map[pos] == Terrain.SIGN);
			drop( item, pos ).type = Heap.Type.REMAINS;
		}
	}
	
	@Override
	public int randomRespawnCell() {
		int cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		while (!getPassable(cell)){
			cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		}
		return cell;
	}

	@Override
	public void seal() {
		final Level level=this;
		super.seal();
		final Actor actor = new Actor() {
			boolean started = false;
			boolean finished = false;
			@Override
			protected boolean act() {
				if (!started) {
					final Actor aac = this;
					Dungeon.hero.viewDistance = 32;
					Dungeon.observe();
					Dungeon.hero.busy();
					Dungeon.hero.sprite.idle();
					Dungeon.hero.interrupt();
					Ballistica ball = new Ballistica(Dungeon.hero.pos, exit, Ballistica.STOP_TARGET);
					ArrayList<Integer> targs = (ArrayList<Integer>) ball.path.clone();
					targs.remove(targs.size() - 1);
					final Emitter emit = CellEmitter.get(Dungeon.hero.pos);
					final Item proto = new EmptyItem();
					for (final int cell : igniteTargs) {
						MissileSprite spr = ((MissileSprite) emit.recycle(MissileSprite.class));
						spr.reset(Dungeon.hero.pos, cell, proto, new Callback() {
							@Override
							public void call() {
								Actor.add(Blob.seed(cell, 1000, EverlastingFire.class));
								CellEmitter.get(cell).burst(EverFlameParticle.FACTORY, 15);
								CellEmitter.get(cell).start(EverFlameParticle.FACTORY, 0.03f, 0);
								;
							}
						});
					}
					MissileSprite spr = ((MissileSprite) emit.recycle(MissileSprite.class));
					spr.reset(Dungeon.hero.pos, PathFinder.xy2pos(CENTER, 6), proto, new Callback() {
						@Override
						public void call() {
							MissileSprite spr = ((MissileSprite) emit.recycle(MissileSprite.class));
							spr.reset(snake, snake + 5 * width(), proto, new Callback() {
								@Override
								public void call() {
									HashMap<Integer, Integer> cells = new HashMap<>();
									final HashMap<Integer, Integer> iters = new HashMap<>();
									final Mob[] tail = new Mob[7];
									final StoneSnake.Head hed = new StoneSnake.Head();
									int iter = 0;
									for (int i = 8; i >= 2; i--) {
										iters.put(PathFinder.xy2pos(CENTER, i), iter);
										cells.put(PathFinder.xy2pos(CENTER, i), PathFinder.xy2pos(CENTER, 36 - i * 2));
										iter++;
									}
									for (final Map.Entry entr : cells.entrySet()) {
										setLosBlocking((Integer) (entr.getKey()), false);
										setLosBlocking(PathFinder.xy2pos(CENTER, 9), false);
										Dungeon.observe();
										MissileSprite spr = ((MissileSprite) emit.recycle(MissileSprite.class));
										spr.reset((Integer) entr.getValue(), (Integer) (entr.getKey())/* + 5 * width()*/, proto, new Callback() {
											@Override
											public void call() {
												int targ = (Integer) entr.getKey();
												try {
													CellEmitter.get(targ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
													map[targ] = Terrain.EMPTY_SP;
													GameScene.updateMap(targ);
													Mob tailm = new StoneSnake.Tail();
													tailm.pos = targ;
													GameScene.add(tailm);
													tail[iters.get(targ)] = tailm;
												} catch (Exception e) {
													MoonshinePixelDungeon.reportException(e);
												}
											}
										});
									}
									MissileSprite spr = ((MissileSprite) emit.recycle(MissileSprite.class));
									spr.reset(PathFinder.xy2pos(CENTER, 18), PathFinder.xy2pos(CENTER, 9) /*+ 5 * width()*/, proto, new Callback() {
										@Override
										public void call() {
											int targ = PathFinder.xy2pos(CENTER, 9);
											try {
												CellEmitter.get(targ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
												map[targ] = Terrain.EMPTY_SP;
												GameScene.updateMap(targ);
												hed.pos = targ;
												GameScene.add(hed);
											} catch (Exception e) {
												MoonshinePixelDungeon.reportException(e);
											}
										}
									});
									MissileSprite spr2 = ((MissileSprite) emit.recycle(MissileSprite.class));
									spr2.reset(PathFinder.xy2pos(CENTER, 34), PathFinder.xy2pos(CENTER, 2) /*+ 5 * width()*/, proto, new Callback() {
										@Override
										public void call() {
											buildFlagMaps();
											hed.createTail(tail);
											hed.beckon(Dungeon.hero.pos);
											((StoneSnakeHeadSprite) hed.sprite).notice();
											Mob boss = new StoneSnake();
											boss.pos = exit - width() + 2;
											GameScene.add(boss);
											MissileSprite spr = ((MissileSprite) emit.recycle(MissileSprite.class));
											spr.reset(PathFinder.xy2pos(CENTER, 0), PathFinder.xy2pos(CENTER, 15) /*+ 5 * width()*/, proto, new Callback() {
												@Override
												public void call() {
													Camera.main.target = Dungeon.hero.sprite;
													Light light = Dungeon.hero.buff(Light.class);
													Dungeon.hero.viewDistance = light == null ? viewDistance : Math.max(Light.DISTANCE, viewDistance);
													Dungeon.observe();
													GameScene.updateFog();
													Dungeon.hero.spendAndNext(1);
													Dungeon.hero.enable();
													aac.next();
												}
											});
										}
									});
								}
							});
						}
					});
					Camera.main.target = spr;
					started=true;
				}

				return false;
			}

			@Override
			public void next() {
				super.next();
				Actor.remove(this);
			}
		};
		Actor.add(actor);
//		Dungeon.observe(-1);
	}

	@Override
	public void press( int cell, Char hero ) {
		
		super.press( cell, hero );
		
		if (!enteredArena && outsideEntraceRoom( cell ) && hero == Dungeon.hero) {
			
			enteredArena = true;
			seal();


//			Mob boss = Bestiary.mob( Dungeon.depth );
//			boss.state = boss.WANDERING;
//			int count = 0;
//			do {
//				boss.pos = Random.Int( length() );
//			} while (
//				!getPassable(boss.pos) ||
//				!outsideEntraceRoom( boss.pos ) ||
//				(Dungeon.visible[boss.pos] && count++ < 20));
//			GameScene.add( boss );
//
//			if (Dungeon.visible[boss.pos]) {
//				boss.notice();
//				boss.sprite.alpha( 0 );
//				boss.sprite.parent.add( new AlphaTweener( boss.sprite, 1, 0.1f ) );
//			}

			set( arenaDoor, Terrain.LOCKED_DOOR );
			GameScene.updateMap( arenaDoor );
			Dungeon.observe();
		}
	}
	
	@Override
	public Heap drop( Item item, int cell ) {
		
		if (!keyDropped && item instanceof SkeletonKey) {
			
			keyDropped = true;
			unseal();
			
			set( arenaDoor, Terrain.DOOR );
			GameScene.updateMap( arenaDoor );
			Dungeon.observe();
			return super.drop( item, PathFinder.xy2pos(CENTER,PathFinder.pos2y(pedestal(false))) );
		}
		return super.drop(item,cell);
	}
	
	private boolean outsideEntraceRoom( int cell ) {
		return cell / width() < arenaDoor / width();
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(VolcanoLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(VolcanoLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
				return Messages.get(VolcanoLevel.class, "entrance_desc");
			case Terrain.EXIT:
				return Messages.get(VolcanoLevel.class, "exit_desc");
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				return Messages.get(VolcanoLevel.class, "deco_desc");
			case Terrain.EMPTY_SP:
				return Messages.get(VolcanoLevel.class, "sp_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(VolcanoLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(VolcanoLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals( ) {
		super.addVisuals();
		VolcanoLevel.addVolcanoVisuals(this, visuals);
		return visuals;
	}
}
