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

import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Moonshine;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.CheckBox;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.noosa.RenderedText;

import java.util.ArrayList;

public class WndChallenges extends Window {

	private final int WIDTH		= MoonshinePixelDungeon.landscape()?216:108;	//108
	private static final int TTL_HEIGHT    = 12;
	private static final int BTN_HEIGHT    = 18;
	private static final int GAP        = 1;

	private boolean editable;
	private ArrayList<CheckBox> boxes;

	public WndChallenges( int checked, boolean editable ) {

		super();

		this.editable = editable;

		RenderedText title = PixelScene.renderText( Messages.get(this, "title"), 9 );
		title.hardlight( TITLE_COLOR );
		title.x = (WIDTH - title.width()) / 2;
		title.y = (TTL_HEIGHT - title.height()) / 2;
		PixelScene.align(title);
		add( title );

		boxes = new ArrayList<>();

		float pos = TTL_HEIGHT;
		if (!MoonshinePixelDungeon.landscape()) {
			for (int i = 0; i < Challenges.NAME_IDS.length; i++) {

				CheckBox cb = new CheckBox(Messages.get(Challenges.class, Challenges.NAME_IDS[i]));
				cb.checked((checked & Challenges.MASKS[i]) != 0);
				cb.active = editable;

				if (i > 0) {
					pos += GAP;
				}
				cb.setRect(0, pos, WIDTH, BTN_HEIGHT);
				pos = cb.bottom();

				add(cb);
				boxes.add(cb);
			}
		} else {
			float pos2 = TTL_HEIGHT;
			for (int i = 0; i < Challenges.NAME_IDS.length; i++) {
				int row = i-1<Challenges.NAME_IDS.length/2?0:1;
				CheckBox cb = new CheckBox( Messages.get(Challenges.class, Challenges.NAME_IDS[i]) );
				cb.checked( (checked & Challenges.MASKS[i]) != 0 );
				cb.active = editable;

				if (i > 0 && i-1!=Challenges.NAME_IDS.length/2) {
					if (row==0) {
						pos += GAP;
					} else{
						pos2 += GAP;
					}
				}
				cb.setRect( WIDTH/2*row, row==0?pos:pos2, WIDTH/2, BTN_HEIGHT );

				if (row==0) {
					pos = cb.bottom();
				} else {
					pos2 = cb.bottom();
				}

				add( cb );
				boxes.add( cb );
			}
		}
		resize( WIDTH, (int)pos );
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			int value = 0;
			for (int i=0; i < boxes.size(); i++) {
				if (boxes.get( i ).checked()) {
					value |= Challenges.MASKS[i];
				}
			}
			MoonshinePixelDungeon.challenges( value );
		}

		super.onBackPressed();
	}
}