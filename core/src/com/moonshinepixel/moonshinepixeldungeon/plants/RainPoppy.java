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
package com.moonshinepixel.moonshinepixeldungeon.plants;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Blandfruit;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfStorm;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;

public class RainPoppy extends Plant {

	{
		image = 14;
	}

	@Override
	public void activate() {
		Dungeon.level.drop( new Blandfruit(), pos ).sprite.drop();
	}

	public static class Seed extends Plant.Seed {
		{
			image = ItemSpriteSheet.SEED_RAINPOPPY;

			plantClass = RainPoppy.class;
			alchemyClass = PotionOfStorm.class;
		}

	}
}
