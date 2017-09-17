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

import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.craftingitems.Scrap;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments.*;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments.*;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.ItemSlot;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments.*;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.RenderedTextMultiline;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

public class WndModify extends Window {

	private static final int BTN_SIZE	= 36;
	private static final float GAP		= 2;
	private static final float BTN_GAP	= 10;
	private static final int WIDTH		= 116;

	private ItemButton btnPressed;

	private ItemButton btnItem1;
	private ItemButton btnItem2;
	private RedButton btnReforge;

	public WndModify(Hero hero ) {
		
		super();
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite((new Scrap().image()),null) );
		titlebar.label( Messages.titleCase( "Gun modifying kit" ) );
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
				GameScene.selectItem( itemSelector, WndBag.Mode.GUN, Messages.get(WndModify.class, "select") );
			}
		};
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

		btnReforge = new RedButton( Messages.get(this, "select_gun") ) {
			@Override
			protected void onClick() {
				disassemble((Gun)btnItem1.item);
				hide();
			}
		};
		btnReforge.enable( false );
		btnReforge.setRect( 0, btnItem1.bottom() + BTN_GAP, WIDTH, 20 );
		add( btnReforge );
		
		
		resize( WIDTH, (int)btnReforge.bottom() );
	}
	
	protected WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null) {
				btnPressed.item( item );
				
				if (btnItem1.item != null) {
				    Scrap scrap = Dungeon.hero.belongings.getItem(Scrap.class);
				    if (scrap!=null) {
                        if (scrap.quantity() >= 2) {
                            btnReforge.text(Messages.get(WndModify.class, "reforge"));
                            btnReforge.enable(true);
                        } else {
                            btnReforge.text(Messages.get(WndModify.class, "morescrap"));
                        }
                    } else {
                        btnReforge.text(Messages.get(WndModify.class, "morescrap"));
                    }
				} else {
                    btnReforge.text(Messages.get(WndModify.class, "select_gun"));
                }
			} else {
                btnReforge.text(Messages.get(WndModify.class, "select_gun"));
			}
		}
	};

	public void disassemble(final Gun gun){
        final Gun.Attachment[] arr = new Gun.Attachment[]{new LongBarrel(), new ShortBarrel(), new SteelBayonet(), new MetalButt(), new FlintLock(), new WhellLock()};
        final Gun.Attachment attach;
        GameScene.show( new WndOptions(
                Messages.get(this, "choose"),
                "",
                arr[0].name(),
                arr[1].name(),
                arr[2].name(),
                arr[3].name(),
                arr[4].name(),
                arr[5].name()
        ) {
            @Override
            protected void onSelect( int index ) {
                if (index >=0 && index <=5) {
                    tryToAttach(gun, arr[index]);
                }
            }
        } );
    }

    public void tryToAttach(Gun gun, Gun.Attachment attach){
	    if (gun.isIdentified()){
	        int roll = Random.Int(100);
	        if (gun.cursed){
	            roll/=2;
            }
            if (roll>25){
	            gun.enchant(attach);
	            GLog.p(Messages.get(this, "success", gun.name(), attach.name()));
            }
            else {
                GLog.n(Messages.get(this, "fail"));
            }
            Dungeon.hero.busy();
            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
            Dungeon.hero.spendAndNext(3);
            Scrap scrap = Dungeon.hero.belongings.getItem(Scrap.class);
            scrap.detach(Dungeon.hero.belongings.backpack, 2);
        } else {
            GLog.h(Messages.get(this, "unknown"));
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
