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
package com.moonshinepixel.moonshinepixeldungeon.items.potions;

import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;

public class PotionOfHealing extends Potion {

	{
		initials = 2;

		bones = true;
	}

	@Override
	public void apply( Hero hero ) {
		setKnown();
		//starts out healing 30 hp, equalizes with hero health total at level 11
		Buff.affect( hero, Healing.class ).setHeal((int)(0.8f*hero.HT + 14), 0.333f, 0);
		cure( hero );
		GLog.p( Messages.get(this, "heal") );
	}

	public static void cure( Hero hero ) {
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, Weakness.class);
		Buff.detach(hero, Bleeding.class);

		hero.HTPENALTY=0;
		hero.updateHT(false);
	}


	public static void heal( Hero hero ) {

		hero.HP = hero.HT;

		cure(hero);

		hero.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
	}

	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return isKnown() ? 30 * quantity : super.price();
	}
}
