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
package com.moonshinepixel.moonshinepixeldungeon.ui;

import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.watabou.input.NoosaInputProcessor;

public class ReloadIndicator extends Tag {

	private ItemSlot slot;

	private Gun lastItem = null;
    private int lastQuantity = 0;
    private int lastCharges = 0;

	public ReloadIndicator() {
        super(0x777c6f);
		
		setSize( 24, 24 );

		
		visible = false;
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();
		
		slot = new ItemSlot() {
			protected void onClick() {

				if (NoosaInputProcessor.modifier) {
					onLongClick();
					return;
				}

				Gun gun = (Gun) Dungeon.hero.belongings.weapon;
				if (Dungeon.hero.ready) {
					gun.reload();
				}

			}
			protected boolean onLongClick()
			{
                Gun gun = (Gun) Dungeon.hero.belongings.weapon;
                if (Dungeon.hero.ready) {
                    gun.chooseAmmoItem();
                }
				return true;
			}
		};
		slot.showParams( true, false, false );
        slot.hotKey = GameAction.TAG_RELOAD;
		add( slot );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		slot.setRect( x + 2, y + 3, width - 2, height - 6 );
	}
	
	@Override
	public void update() {
		
		if (Dungeon.hero.ready) {
			Item itm = Dungeon.hero.belongings.weapon;
			if (itm instanceof Gun){
			    Gun gun = (Gun)itm;
			    if (gun._load.curLoad()<gun._load.maxLoad()) {
//                    lastItem = gun;
//                    lastCharges=gun.curCharges;
//                    lastQuantity = itm.quantity();
                    slot.item(itm);
                    if (itm != lastItem || itm.quantity() != lastQuantity || gun._load.curLoad()!=lastCharges) {
                        lastItem = gun;
                        lastQuantity = itm.quantity();
                        lastCharges=gun._load.curLoad();

                        slot.item( itm );
                        flash();
                    }
                    visible = true;
                }
                else {
                    lastItem = null;
                    visible = false;
                }
            }else {
                lastItem = null;
                visible = false;
            }

		}
		
		slot.enable( visible && Dungeon.hero.ready );
		
		super.update();
	}
}
