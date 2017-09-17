/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
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
import com.watabou.noosa.TextureFilm;

public class SlimeSprite extends MobSprite {

	public SlimeSprite() {
		super();
		
		texture( Assets.SLIME );
		
		TextureFilm frames = new TextureFilm( texture, 16, 16 );
		
		idle = new Animation( 12, true );
		idle.frames( frames, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 );
		
		run = new Animation( 12, true );
		run.frames( frames, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29 );
		
		attack = new Animation( 16, false );
		attack.frames( frames, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39 );
		
		die = new Animation( 12, false );
		die.frames( frames, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49  );
		
		play( idle );
	}
}
