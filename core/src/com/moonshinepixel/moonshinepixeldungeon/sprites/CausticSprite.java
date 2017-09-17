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

import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.TextureFilm;

public class CausticSprite extends MobSprite {

	public CausticSprite() {
		super();
		
		texture( Assets.SLIME );
		
		TextureFilm frames = new TextureFilm( texture, 16, 16 );
		
		idle = new Animation( 12, true );
		idle.frames( frames, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111 );
		
		run = new Animation( 12, true );
		run.frames( frames, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131 );
		
		attack = new Animation( 16, false );
		attack.frames( frames, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141 );
		
		die = new Animation( 12, false );
		die.frames( frames, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151  );
		
		play( idle );
	}

	@Override
	public void die() {
		super.die();
		if (Dungeon.visible[ch.pos]) {
			emitter().burst( Speck.factory( Speck.TOXIC ), 6 );
		}
	}
}
