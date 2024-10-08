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
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.FlameParticle;
import com.watabou.noosa.TextureFilm;

public class StoneSnakeTailSprite extends MobSprite {

	public StoneSnakeTailSprite() {
		super();
		
		texture( Assets.STONESNAKETAIL );
		
		TextureFilm frames = new TextureFilm( texture, 16, 15 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0 );
		
		run = new Animation( 10, false );
		run.frames( frames, 0, 1, 2, 3 );
		
		attack = new Animation( 10, false );
		attack.frames( frames, 0, 1, 2, 3 );
		
		die = new Animation( 105, false );
		die.frames( frames, 5,6,7 );
		
		play( idle );
	}
	@Override
	public void onComplete( Animation anim ) {

		super.onComplete( anim );

		if (anim == die) {
			emitter().burst( FlameParticle.FACTORY, 15);
		}
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}
}
