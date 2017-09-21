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
import com.moonshinepixel.moonshinepixeldungeon.effects.Ripple;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.SewerPainter;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.VolcanoPainter;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.*;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class VolcanoLevel extends RegularLevel {

	{
		color1 = 0x801500;
		color2 = 0xa68521;
	}

	@Override
	protected int standardRooms() {
		//6 to 9, average 7.333
		return 6+Random.chances(new float[]{2, 3, 3, 1});
	}

	@Override
	protected int specialRooms() {
		//1 to 3, average 2.2
		return 1+Random.chances(new float[]{2, 4, 4});
	}

	@Override
	protected Painter painter() {
		return new VolcanoPainter()
				.setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 5)
				.setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 4)
				.setTraps(nTraps(), trapClasses(), trapChances());
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_VOLCANO;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}


	@Override
	protected Class<?>[] trapClasses() {
		return new Class[]{ FireTrap.class, FrostTrap.class, PoisonTrap.class, SpearTrap.class, VenomTrap.class,
				ExplosiveTrap.class, FlashingTrap.class, GrippingTrap.class, ParalyticTrap.class, LightningTrap.class, RockfallTrap.class, OozeTrap.class,
				ConfusionTrap.class, FlockTrap.class, GuardianTrap.class, PitfallTrap.class, SummoningTrap.class, TeleportationTrap.class/*,
				WarpingTrap.class*/};
	}

	@Override
	protected float[] trapChances() {
		return new float[]{ 8, 8, 8, 8, 8,
				4, 4, 4, 4, 4, 4, 4,
				2, 2, 2, 2, 2, 2/*,
				1*/ };
	}

	@Override
	protected void createItems() {
		super.createItems();
	}

	@Override
	public Group addVisuals() {
		super.addVisuals();
		addVolcanoVisuals(this, visuals);
		return visuals;
	}

	public static void addVolcanoVisuals(Level level, Group group ) {
		for (int i=0; i < level.length(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				group.add( new Sink( i ) );
			}
		}
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(VolcanoLevel.class, "water_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(VolcanoLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(VolcanoLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
		}
	}

	private static class Sink extends Emitter {

		private int pos;
		private float rippleDelay = 0;

		private static final Factory factory = new Factory() {
			
			@Override
			public void emit( Emitter emitter, int index, float x, float y ) {
				LavaParticle p = (LavaParticle)emitter.recycle( LavaParticle.class );
				p.reset( x, y );
			}
		};
		
		public Sink( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 2, p.y + 3, 4, 0 );
			
			pour( factory, 0.1f );
		}
		
		@Override
		public void update() {
			if (visible = Dungeon.visible[pos]) {
				
				super.update();
				
				if ((rippleDelay -= Game.elapsed) <= 0) {
					Ripple ripple = GameScene.ripple( pos + Dungeon.level.width() );
					if (ripple != null) {
						ripple.y -= DungeonTilemap.SIZE / 2;
						rippleDelay = Random.Float(0.4f, 0.6f);
					}
				}
			}
		}
	}
	
	public static final class LavaParticle extends PixelParticle {
		
		public LavaParticle() {
			super();
			
			acc.y = 50;
			am = 0.5f;
			
			color( ColorMath.random( 0xca7373, 0x722727 ) );
			size( 2 );
		}
		
		public void reset( float x, float y ) {
			revive();
			
			this.x = x;
			this.y = y;
			
			speed.set( Random.Float( -2, +2 ), 0 );
			
			left = lifespan = 0.4f;
		}
	}
}
