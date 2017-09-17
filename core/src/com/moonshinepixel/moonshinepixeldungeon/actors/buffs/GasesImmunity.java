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

import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ConfusionGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.StenchGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.VenomGas;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ParalyticGas;

public class GasesImmunity extends FlavourBuff {
	
	public static final float DURATION	= 15f;
	
	@Override
	public int icon() {
		return BuffIndicator.IMMUNITY;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	{
		immunities.add( ParalyticGas.class );
		immunities.add( ToxicGas.class );
		immunities.add( ConfusionGas.class );
		immunities.add( StenchGas.class );
		immunities.add( VenomGas.class );
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}
