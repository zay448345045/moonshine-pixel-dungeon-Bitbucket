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
package com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.GameArrays;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Lucky extends Weapon.Enchantment {

	private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing( 0x00FF00 );
	
	@Override
	public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {
		int level = Math.max( 0, weapon.level() );
		if (Random.Float()<(level+1)/(level+3)) {
			HashSet<Integer> rolls = new HashSet<>();
			rolls.add(damage);
			for (int i = 0; i < (level / 2) + 1; i++) {
				rolls.add(attacker.damageRoll()-defender.drRoll());
			}
			damage = GameArrays.findMax(rolls);
		}
		return damage;
	}

	@Override
	public Glowing glowing() {
		return GREEN;
	}
}
