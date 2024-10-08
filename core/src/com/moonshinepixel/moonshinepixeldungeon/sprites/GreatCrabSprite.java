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
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class GreatCrabSprite extends MobSprite {

	public GreatCrabSprite() {
		super();

		texture( Assets.CRAB );

		TextureFilm frames = new TextureFilm( texture, 16, 16 );

		idle = new MovieClip.Animation( 5, true );
		idle.frames( frames, 17, 18, 17, 19 );

		run = new MovieClip.Animation( 10, true );
		run.frames( frames, 20, 21, 22, 23 );

		attack = new MovieClip.Animation( 12, false );
		attack.frames( frames, 24, 25, 26 );

		die = new MovieClip.Animation( 12, false );
		die.frames( frames, 27, 28, 29, 30 );

		sleep = new Animation(1,true);
		sleep.frames( frames, 32,33);

		sleepStatusRaise =-6;

		play( idle );
	}

	@Override
	public int blood() {
		return 0xFFFFEA80;
	}
}
