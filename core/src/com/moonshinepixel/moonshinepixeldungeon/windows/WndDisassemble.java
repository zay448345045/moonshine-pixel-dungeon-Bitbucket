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
package com.moonshinepixel.moonshinepixeldungeon.windows;

import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.Armor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.GunslingerSubbag;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.IncendiaryBomb;
import com.moonshinepixel.moonshinepixeldungeon.items.craftingitems.Scrap;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.*;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.AshBomb;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.ShrapnelBomb;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.BulletGun;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.ItemSlot;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.MeleeWeapon;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.RenderedTextMultiline;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

public class WndDisassemble extends Window {

	private static final int BTN_SIZE	= 36;
	private static final float GAP		= 2;
	private static final float BTN_GAP	= 10;
	private static final int WIDTH		= 116;

	private ItemButton btnPressed;

	private ItemButton btnItem1;
	private ItemButton btnItem2;
	private RedButton btnReforge;
	private RedButton btnReforgeAll;
	private GunslingerSubbag subBag;

	public WndDisassemble(Hero hero ) {
		
		super();

		subBag = Dungeon.hero.belongings.getItem(GunslingerSubbag.class);
		System.out.println(subBag);

		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite((new Scrap().image()),null) );
		titlebar.label( Messages.titleCase( "Disassembling kit" ) );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		RenderedTextMultiline message = PixelScene.renderMultiline( Messages.get(this, "prompt"), 6 );
		message.maxWidth( WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );
		
		btnItem1 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem1;
				GameScene.selectItem( itemSelector, WndBag.Mode.DISASSEMBLEABLE, Messages.get(WndDisassemble.class, "select") );
			}
		};
		if (subBag!=null){
			if (subBag.lastItem!=null){
				if (subBag.lastItem.quantity()>0) {
					System.out.println(subBag.lastItem);
					btnItem1.item(subBag.lastItem);
					btnPressed = btnItem1;
				}
			}
		}
		btnItem1.setRect( WIDTH / 2 - BTN_SIZE/2, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		add( btnItem1 );

		/*
		btnItem2 = new ItemButton() {
			@Override
			protected void onClick() {
				btnPressed = btnItem2;
				GameScene.selectItem( itemSelector, WndBag.Mode.ALL, Messages.get(WndDisassemble.class, "select") );
			}
		};
		btnItem2.setRect( btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE, BTN_SIZE );
		add( btnItem2 );*/

		btnReforge = new RedButton( Messages.get(this, "reforge") ) {
			@Override
			protected void onClick() {
				disassemble(btnItem1.item);
				hide();
			}
		};
		float bottom = 0;
		btnReforge.enable( btnItem1.item!=null );
		btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
		add( btnReforge );
		bottom = btnReforge.bottom();

		btnReforgeAll = new RedButton( Messages.get(this, "reforgeall") ) {
			@Override
			protected void onClick() {
				disassemble(btnItem1.item, true);
				hide();
			}
		};
		btnReforgeAll.enable( false );

		if (btnItem1.item!=null){
			if (btnItem1.item.quantity()>1){
				btnReforgeAll.enable( true );
			}
		}
		btnReforgeAll.setRect( 0, bottom + BTN_GAP, WIDTH, 20 );
		add( btnReforgeAll );
		bottom = btnReforgeAll.bottom();
		

		resize( WIDTH, (int)bottom );
	}
	
	protected WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				btnPressed.item( item );
				
				if (btnItem1.item != null) {
					if (subBag!=null) {
						subBag.lastItem = item;
						System.out.println(subBag.lastItem);
					}
					btnReforge.enable( true );
					btnReforgeAll.enable( item.quantity()>1 );
				}
			}
		}
	};

	public void disassemble(Item item){
		disassemble(item,false);
	}
	public void disassemble(Item item, boolean all){
	    Scrap scrap = new Scrap();
	    int overAllQuanity = 0;
	    int repeats = all?item.quantity():1;
	    for (int i = 0; i<repeats;i++) {
			int quanity = 0;
			if (item instanceof MeleeWeapon) {
				MeleeWeapon w = (MeleeWeapon) item;
				quanity += Random.NormalIntRange((w.tier + 2) / 2, w.tier + 2);
				if (w == Dungeon.hero.belongings.weapon) {
					Dungeon.hero.belongings.weapon = null;
					Dungeon.quickslot.clearItem(w);
					w.updateQuickslot();
				}
			}
			if (item instanceof BulletGun) {
				BulletGun b = (BulletGun) item;
				quanity += Random.NormalIntRange((b.tier() + 2) / 2, b.tier() + 2);
				if (b.attachment != null) {
					quanity += Random.NormalIntRange(1, 2);
				}
				if (b == Dungeon.hero.belongings.weapon) {
					Dungeon.hero.belongings.weapon = null;
					Dungeon.quickslot.clearItem(b);
					b.updateQuickslot();
				}
			}
			if (item instanceof MissileWeapon) {
				MissileWeapon w = (MissileWeapon) item;
				if (w instanceof Dart) {
					quanity += Random.chances(new float[]{3, 1});
				}
				if (w instanceof CurareDart) {
					quanity += Random.chances(new float[]{3, 1});
				}
				if (w instanceof IncendiaryDart) {
					quanity += Random.chances(new float[]{3, 1});
				}
				if (w instanceof Javelin) {
					quanity += Random.chances(new float[]{2, 2, 1});
				}
				if (w instanceof Shuriken) {
					quanity += Random.chances(new float[]{4, 1});
				}
				if (w instanceof Tamahawk) {
					quanity += Random.chances(new float[]{1, 2, 1, 1});
				}
				if (w == Dungeon.hero.belongings.weapon) {
					if (w.quantity() <= 1) {
						Dungeon.hero.belongings.weapon = null;
						Dungeon.quickslot.clearItem(w);
						w.updateQuickslot();
					} else {
						w.quantity(w.quantity() - 1);
						w.updateQuickslot();
					}
				}
			}
			if (item instanceof Armor) {
				Armor a = (Armor) item;
				quanity += Random.NormalIntRange((a.tier + 2) / 2, a.tier + 2);
				if (a == Dungeon.hero.belongings.armor) {
					Dungeon.hero.belongings.armor = null;
					Dungeon.quickslot.clearItem(a);
					a.updateQuickslot();
				}
			}
			if (item instanceof Bomb) {
				if (item instanceof IncendiaryBomb) {
					quanity += Random.chances(new float[]{1, 1});
				} else if (item instanceof ShrapnelBomb) {
					quanity += Random.chances(new float[]{1, 2});
				} else if (item instanceof AshBomb) {
					quanity += Random.chances(new float[]{1, 1.5f});
				}
				quanity += 1;
			}
			if (item.isUpgradable() && item.level() > 0) {
				quanity += Random.NormalIntRange(1, Math.max((int) (item.level() * 0.75f), 1));
			}
			if (item.cursed) {
				quanity /= 2;
			}
			item.detach(Dungeon.hero.belongings.backpack);
			overAllQuanity+=quanity;
		}
		if (overAllQuanity>0) {
	    	if (!all) {
				GLog.p(Messages.get(this, "success", item.name(), overAllQuanity));
			} else
				GLog.p(Messages.get(this, "successall", repeats, item.name(), overAllQuanity));
			scrap.quantity(overAllQuanity);
			scrap.give();
		} else {
			GLog.n(Messages.get(this, "fail", item.name()));
		}
        try {
            Dungeon.hero.busy();
            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
            float wait = repeats<4?3:repeats<10?2:repeats<20?1:0.5f;
            Dungeon.hero.spendAndNext(wait*repeats);
        } catch (Exception e){
            MoonshinePixelDungeon.reportException(e);
        }
    }

	public static class ItemButton extends Component {
		
		protected NinePatch bg;
		protected ItemSlot slot;
		
		public Item item = null;
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			bg = Chrome.get( Chrome.Type.BUTTON );
			add( bg );
			
			slot = new ItemSlot() {
				@Override
				protected void onTouchDown() {
					bg.brightness( 1.2f );
					Sample.INSTANCE.play( Assets.SND_CLICK );
				};
				@Override
				protected void onTouchUp() {
					bg.resetColor();
				}
				@Override
				protected void onClick() {
					ItemButton.this.onClick();
				}
			};
			slot.enable(true);
			add( slot );
		}
		
		protected void onClick() {};
		
		@Override
		protected void layout() {
			super.layout();
			
			bg.x = x;
			bg.y = y;
			bg.size( width, height );
			
			slot.setRect( x + 2, y + 2, width - 4, height - 4 );
		};
		
		public void item( Item item ) {
			slot.item( this.item = item );
		}
	}
}
