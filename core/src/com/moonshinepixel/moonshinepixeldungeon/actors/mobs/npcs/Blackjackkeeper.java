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
package com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs;

import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ShopBlob;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.levels.RegularLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.BlackjackShopRoom;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.BlackjackkeeperSprite;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndTradeItem;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndBag;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Blackjackkeeper extends NPC {

	{
		spriteClass = BlackjackkeeperSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	private boolean die = false;

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		die=true;
	}

	@Override
	protected boolean act() {

		throwItem();
		
		sprite.turnTo( pos, Dungeon.hero.pos );
		spend( TICK );
		if (die){
			destroy();
			sprite.destroy();
			sprite.killAndErase();
		}
		return true;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
		GLog.w(Messages.titleCase(name)+": "+Messages.get(this, Random.oneOf("dmg1","dmg2","dmg3","dmg4")));
	}
	
	@Override
	public void add( Buff buff ) {
		GLog.w(Messages.titleCase(name)+": "+Messages.get(this, Random.oneOf("dmg1","dmg2","dmg3","dmg4")));
	}
	
	public void flee() {
		for (Heap heap: Dungeon.level.heaps.values()) {
			if (heap.type == Heap.Type.FOR_SALE) {
				CellEmitter.get( heap.pos ).burst( ElmoParticle.FACTORY, 4 );
				heap.destroy();
			}
		}
		
		destroy();
		
		sprite.killAndErase();
		CellEmitter.get( pos ).burst( ElmoParticle.FACTORY, 6 );
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	public static WndBag sell() {
		return GameScene.selectItem( itemSelector, WndBag.Mode.FOR_SALE, Messages.get(Blackjackkeeper.class, "sell"));
	}
	
	private static WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				WndBag parentWnd = sell();
				GameScene.show( new WndTradeItem( item, parentWnd ) );
			}
		}
	};

	@Override
	public boolean interact() {
		if (Blob.volumeAt(pos, ShopBlob.class)<=0) {
			GLog.n(Messages.titleCase(name) + ": " + Messages.get(this, "welcome"));
			((BlackjackShopRoom)((RegularLevel)Dungeon.level).room(pos)).placeItems(Dungeon.level);
			((BlackjackShopRoom)((RegularLevel)Dungeon.level).room(pos)).seal();
		} else {
			((BlackjackShopRoom)((RegularLevel)Dungeon.level).room(pos)).unseal();
			GLog.n(Messages.titleCase(name) + ": " + Messages.get(this, "goodbye"));
			Dungeon.level.blobs.get( ShopBlob.class ).explode(pos);
			CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 8);
			destroy();
			sprite.destroy();
			sprite.killAndErase();
		}

		return false;
	}
}
