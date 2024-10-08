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
package com.moonshinepixel.moonshinepixeldungeon.items.bags;

import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.plants.Plant;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;

public class SeedPouch extends Bag {

	{
		image = ItemSpriteSheet.POUCH;
		
		size = 13;
	}
	
	@Override
	public boolean grab( Item item ) {
		return item instanceof Plant.Seed;
	}
	
	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return 30;
	}

}
