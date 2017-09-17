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
package com.moonshinepixel.moonshinepixeldungeon.items.keys;

import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;

public class GoldenKey extends Key {
	
	{
		image = ItemSpriteSheet.GOLDEN_KEY;
	}

	@Override
	public boolean doPickUp(Hero hero) {
		Dungeon.hero.belongings.specialKeys[depth] += quantity();
		return super.doPickUp(hero);
	}

	public GoldenKey() {
		this( 0 );
	}
	
	public GoldenKey( int depth ) {
		super();
		this.depth = depth;
	}

	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return 0;
	}
}
