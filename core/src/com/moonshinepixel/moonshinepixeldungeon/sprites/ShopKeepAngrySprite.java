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
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.CurareDart;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class ShopKeepAngrySprite extends MobSprite {

	private Animation cast;

	public ShopKeepAngrySprite() {
		super();
		
		texture( Assets.ANGRYSK );
		TextureFilm film = new TextureFilm( texture, 14, 14 );
		
		idle = new Animation( 1, true );
		idle.frames( film, 1 );
		
		run = new Animation( 1, true );
		run.frames( film, 1 );
		
		die = new Animation( 1, false );
		die.frames( film, 4 );

		attack = new Animation( 12, false );
		attack.frames( film, 1,2,3,2 );

		cast = attack.clone();

		idle();
	}

	@Override
	public void attack( int cell ) {
		if (!Dungeon.level.adjacent(cell, ch.pos)) {

			((MissileSprite)parent.recycle( MissileSprite.class )).
					reset( ch.pos, cell, new Bullet(), new Callback() {
						@Override
						public void call() {
							ch.onAttackComplete();
							play(idle);
						}
					} );

			play( cast );
			turnTo( ch.pos , cell );

		} else {

			super.attack( cell );

		}
	}

	@Override
	public void play(Animation anim, boolean force) {
		super.play(anim, force);
		if (anim==die){
			emitter().burst(ElmoParticle.FACTORY, 8);
		}
	}
}
