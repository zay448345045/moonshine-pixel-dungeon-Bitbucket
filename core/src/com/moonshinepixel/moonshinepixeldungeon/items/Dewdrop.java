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
package com.moonshinepixel.moonshinepixeldungeon.items;

import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Hunger;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroSubClass;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.audio.Sample;

public class Dewdrop extends Item {
	
	{
		image = ItemSpriteSheet.DEWDROP;
		
		stackable = true;
	}
	
	@Override
	public boolean doPickUp( Hero hero ) {
		
		DewVial vial = hero.belongings.getItem( DewVial.class );

		if (vial == null) {

			int value = 1 + (Dungeon.fakedepth[Dungeon.depth] - 1) / 5;
			if (hero.subClass == HeroSubClass.WARDEN) {
				value+=2;
			}

			int effect = Math.min( hero.HT - hero.HP, value * quantity );
			if (effect > 0) {
				hero.HP += effect;
				hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
				hero.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "value", effect) );
			} else {
			}

		} else {
			vial.collectDew( this );
		}
		
		Sample.INSTANCE.play( Assets.SND_DEWDROP );
		hero.spendAndNext( TIME_TO_PICK_UP );

		return true;
	}

	@Override
	//max of one dew in a stack
	public Dewdrop quantity(int value) {
		return (Dewdrop) super.quantity(value);
	}

}
