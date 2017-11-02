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
package com.moonshinepixel.moonshinepixeldungeon.actors.buffs;

import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.ChaliceOfBlood;

public class ItemActor extends Buff {

	@Override
	public boolean attachTo(Char target) {
		if (!(target instanceof Hero))return false;
		return super.attachTo(target);
	}

	@Override
	public boolean act() {
		if (target instanceof Hero) {
			for (Item itn : ((Hero) target).belongings) itn.invAct();
			if (Dungeon.isChallenged(Challenges.AMNESIA)) {
				for (Item itm : ((Hero) target).belongings.backpack) {
					itm.unIdentifyTry(2);
				}
				if (((Hero) target).belongings.weapon != null) {
					((Hero) target).belongings.weapon.unIdentifyTry(1, true);
				}
				if (((Hero) target).belongings.armor != null) {
					((Hero) target).belongings.armor.unIdentifyTry(1, true);
				}
				if (((Hero) target).belongings.misc1 != null) {
					((Hero) target).belongings.misc1.unIdentifyTry(1, true);
				}
				if (((Hero) target).belongings.misc2 != null) {
					((Hero) target).belongings.misc2.unIdentifyTry(1, true);
				}
			}
		} else {
			detach();
		}
		spend(Actor.TICK);
		return true;
	}
}
