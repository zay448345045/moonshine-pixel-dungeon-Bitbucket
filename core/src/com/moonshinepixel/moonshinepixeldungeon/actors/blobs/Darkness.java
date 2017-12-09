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
package com.moonshinepixel.moonshinepixeldungeon.actors.blobs;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.Journal;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Paralysis;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Shadows;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Venom;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.BlobEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.FlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ShadowParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ShaftParticle;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;

public class Darkness extends Blob {
	@Override
	protected void evolve() {
		super.evolve();

		Char ch;
		int cell;

		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				if (cur[cell] > 0 && (ch = Actor.findChar(cell)) != null) {
					if (!ch.immunities().contains(this.getClass())) {
						int penalty = Math.max(1, Math.round(ch.HT / 20));
						if (ch instanceof Hero) {
							Hero hero = (Hero) ch;
							hero.HTPENALTY += penalty;
							hero.updateHT();
						} else {
							ch.HT -= penalty;
							ch.updateHT();
						}
						ch.sprite.showStatus(0x990000, "-" + penalty);
					}
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
//		emitter.start( ShaftParticle.FACTORY.color(0x660066), 0.6f, 0 );
//		emitter.pour( Speck.factory( Speck.PARALYSIS ), 0.4f );
		emitter.start( ShadowParticle.CURSE, 0.06f, 0 );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
