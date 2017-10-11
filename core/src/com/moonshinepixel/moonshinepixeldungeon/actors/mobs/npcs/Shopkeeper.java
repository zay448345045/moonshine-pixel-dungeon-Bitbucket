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

import com.moonshinepixel.moonshinepixeldungeon.QuickSlot;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ShopBlob;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.AngryShopKeeper;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.ShopThief;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.QuickSlotButton;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndTradeItem;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndBag;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ShopkeeperSprite;
import com.watabou.utils.Random;

public class Shopkeeper extends NPC {

	{
		spriteClass = ShopkeeperSprite.class;

		properties.add(Property.IMMOVABLE);
	}
	
	@Override
	protected boolean act() {

		throwItem();
		
		sprite.turnTo( pos, Dungeon.hero.pos );
		spend( TICK );
		return true;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
		flee();
	}
	
	@Override
	public void add( Buff buff ) {
		flee();
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	public void flee() {
		for (Heap heap: Dungeon.level.heaps.values()) {
			if (heap.type == Heap.Type.FOR_SALE) {
				if (Blob.volumeAt(heap.pos, ShopBlob.class)<=0) {
					switch (Random.Int(5)){
						case 0:
						case 1:
							ShopThief.spawnAt(heap.pos, heap);
							break;
						case 2:
						case 3:
						case 4:
							CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
							heap.destroy();
					}
				}
			}
		}

		destroy();
		sprite.killAndErase();
		AngryShopKeeper ask = new AngryShopKeeper();
		ask.pos=pos;
		GameScene.add(ask);
		ask.state = ask.HUNTING;
		ask.sprite.turnTo(pos, Dungeon.hero.pos);
		yell(Messages.get(this,"thief"));
		QuickSlotButton.target(ask);
//		CellEmitter.get( pos ).burst( ElmoParticle.FACTORY, 6 );
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	public static WndBag sell() {
		return GameScene.selectItem( itemSelector, WndBag.Mode.FOR_SALE, Messages.get(Shopkeeper.class, "sell"));
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
	public void die(Object cause) {
		destroy();
		sprite.die();
	}

	@Override
	public boolean interact() {
		sell();
		return false;
	}
}
