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
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.effects.Splash;
import com.moonshinepixel.moonshinepixeldungeon.levels.GreatYogBossLevel;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;

public class BigYogSprite extends MobSprite {

	public BigYogSprite() {
		super();

		showHPBar=false;
		nofade=true;

		perspectiveRaise = 0f;

		texture( Assets.YOGBIG );
		
		TextureFilm frames = new TextureFilm( texture, 80, 71 );
		
		idle = new Animation( 1, true );
		idle.frames( frames, 70 );

		run = new Animation( 1, true );
		run.frames( frames, 70 );

		attack = new Animation( 1, false );
		attack.frames( frames, 70 );
		
		die = new Animation( 17, false );
		die.frames( frames, 70, 69,68,67,66,65,64,63,62,61,60,59,58,57,56,55,54,53,52,51,50,49,48,47,46,45,44,43,42,41,40,39,38,37,36,35,34,33,32,31,30,29,28,27,26,25,24,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0 );
		
		play( idle );
	}

	@Override
	public void link(Char ch) {
		super.link(ch);
		renderShadow = false;
	}

	@Override
	public PointF worldToCamera(int cell) {
		return super.worldToCamera(cell+Dungeon.level.width()*2);
	}
}
