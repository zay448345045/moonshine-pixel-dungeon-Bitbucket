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
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.EverFlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.FlameParticle;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class StoneSnakeHeadSprite extends MobSprite {

	private Animation notice;

	public StoneSnakeHeadSprite() {
		super();
		
		texture( Assets.STONESNAKEHEAD );
		
		TextureFilm frames = new TextureFilm( texture, 16, 15 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0 );
		
		run = new Animation( 10, false );
		run.frames( frames, 0, 1, 2, 3 );
		
		attack = new Animation( 10, false );
		attack.frames( frames, 4, 5 );
		
		die = new Animation( 105, false );
		die.frames( frames, 8,9,10 );

		notice = new Animation( 5, false );
		notice.frames( frames, 6,7,7,6,6,7,7,6 );
		
		play( idle );
	}

	public void notice(){
		play(notice);
	}
	@Override
	public void onComplete( Animation anim ) {

		super.onComplete( anim );

		if (anim == die) {
			emitter().burst( FlameParticle.FACTORY, 25);
		}
	}

	@Override
	public int blood() {
		return 0xFFFFFF88;
	}
}
