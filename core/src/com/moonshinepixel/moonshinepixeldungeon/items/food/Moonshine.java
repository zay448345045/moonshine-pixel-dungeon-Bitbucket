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
package com.moonshinepixel.moonshinepixeldungeon.items.food;

import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Drunk;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Hunger;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Moonshine extends Food {

	{
		image = ItemSpriteSheet.MOONSHINE;
		energy = Hunger.HUNGRY/2f;
		hornValue = 2;
	}
	
	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return 10 * quantity;
	}


	@Override
	public void execute(Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_EAT )) {
			effect(hero);
		}
	}

	public static void effect(Hero hero){
		Drunk dr = hero.buff(Drunk.class);
		if (dr!=null) {
			dr.drunk+=Random.NormalIntRange(30,55);
			dr.drunkCap*=0.8f;
		} else {
			dr = new Drunk();
			dr.drunk=Random.NormalIntRange(30,55);
			dr.attachTo(hero);
		}
	}
}
