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
package com.moonshinepixel.moonshinepixeldungeon.sprites;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.effects.MagicMissile;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.CurareDart;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class ForestSpiritSprite extends MobSprite {

	private Animation cast;

	public ForestSpiritSprite() {
		super();
		
		texture( Assets.MOBDUMMY );
		
		TextureFilm frames = new TextureFilm( texture, 16, 15 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 1 );
		
		run = new Animation( 10, true );
		run.frames( frames, 6, 7, 8, 9, 10 );

		attack = new Animation( 15, false );
		attack.frames( frames, 2, 3, 4, 5, 0 );

		cast = attack.clone();

		die = new Animation( 10, false );
		die.frames( frames, 11, 12, 13, 14 );
		
		play( idle );
	}

	@Override
	public void attack( int cell ) {
		attack(cell,true);
	}

	public void attack(int cell, final boolean complete){

		MagicMissile.boltFromChar( parent,
				MagicMissile.FORCE,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						try {
							if (complete)
								ch.onAttackComplete();
						}catch (Exception e){

						}
					}
				} );
		Sample.INSTANCE.play( Assets.SND_ZAP );
		play( cast );
		turnTo( ch.pos , cell );
	}
}
