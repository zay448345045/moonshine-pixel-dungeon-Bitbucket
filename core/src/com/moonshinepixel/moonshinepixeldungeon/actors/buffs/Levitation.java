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

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;

public class Levitation extends FlavourBuff {

	public static final float DURATION	= 20f;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
//			target.flying = true;
			target.updateFlying();
			detach( target, Roots.class );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
//		target.flying = false;
		target.updateFlying();
		Dungeon.level.press( target.pos, target );
		super.detach();
	}

	@Override
	public void afterDetach() {
		super.afterDetach();
		target.updateFlying();
	}

	@Override
	public int icon() {
		return BuffIndicator.LEVITATION;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.LEVITATING);
		else target.sprite.remove(CharSprite.State.LEVITATING);
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
