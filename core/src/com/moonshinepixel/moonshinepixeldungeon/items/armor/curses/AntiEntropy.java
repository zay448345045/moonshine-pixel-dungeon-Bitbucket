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
package com.moonshinepixel.moonshinepixeldungeon.items.armor.curses;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.FlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Burning;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Frost;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.Armor;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.SnowParticle;
import com.watabou.utils.Random;

public class AntiEntropy extends Armor.Glyph {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	
	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		if (Random.Int( 8 ) == 0) {
			if (Random.Boolean()==true) {
				if (Dungeon.level.adjacent(attacker.pos, defender.pos)) {
					Buff.prolong(attacker, Frost.class, Frost.duration(attacker) * Random.Float(0.5f, 1f));
					CellEmitter.get(attacker.pos).start(SnowParticle.FACTORY, 0.2f, 6);
				}

				Buff.affect(defender, Burning.class).reignite(defender);
				defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);
			} else {
				if (Dungeon.level.adjacent(attacker.pos, defender.pos)) {
					Buff.prolong(defender, Frost.class, Frost.duration(defender) * Random.Float(0.5f, 1f));
					CellEmitter.get(defender.pos).start(SnowParticle.FACTORY, 0.2f, 6);
				}

				Buff.affect(attacker, Burning.class).reignite(attacker);
				attacker.sprite.emitter().burst(FlameParticle.FACTORY, 5);
			}

		}
		
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	@Override
	public boolean curse() {
		return true;
	}
}
