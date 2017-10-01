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
package com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee;

import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Claw extends MeleeWeapon {

	{
		image = ItemSpriteSheet.CLAW;

		tier = 2;
		DLY = 0.25f; //4x speed
	}

	@Override
	public int max(int lvl) {
		return  Math.round(1.25f*(tier+1)) +
				Math.round(lvl*0.25f*(tier+1));
	}

	@Override
	public int min(int lvl) {
		return tier+(int)Math.floor(lvl*0.75f);
	}


	@Override
	public Item random() {
		tier= Random.NormalIntRange(2,5);
		return super.random();
	}
}
