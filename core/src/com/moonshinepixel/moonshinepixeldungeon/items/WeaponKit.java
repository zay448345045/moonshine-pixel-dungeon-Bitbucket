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

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class WeaponKit extends Item {

	private static final String TXT_UPGRADED		= "you applied the weapon kit to upgrade your %s";
	
	private static final float TIME_TO_UPGRADE = 2;
	
	private static final String AC_APPLY = "APPLY";
	
	{
		image = ItemSpriteSheet.WEPKIT;
		
		unique = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_APPLY );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action == AC_APPLY) {

			curUser = hero;
			GameScene.selectItem( itemSelector, WndBag.Mode.KITUPGRADEUBLEWEAPON, Messages.get(this, "prompt") );
			
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	private void upgrade( Weapon wep ) {
		
		detach( curUser.belongings.backpack );
		
		curUser.sprite.centerEmitter().start( Speck.factory( Speck.KIT ), 0.05f, 10 );
		curUser.spend( TIME_TO_UPGRADE );
		curUser.busy();
		
		GLog.w( Messages.get(this, "upgraded", wep.name()) );

		wep.tier++;
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.SND_EVOKE );
	}
	
	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				WeaponKit.this.upgrade( (Weapon) item );
			}
		}
	};
}
