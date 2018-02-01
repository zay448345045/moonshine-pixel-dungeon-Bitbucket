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
import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Moonshine;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.CheckBox;
import com.moonshinepixel.moonshinepixeldungeon.ui.ScrollPane;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndChallenges extends Window {

//	private final int WIDTH		= MoonshinePixelDungeon.landscape()?216:108;	//108
	private final int WIDTH		= 128;	//108
	private final int HEIGHT		= 216;	//108
//	private final int TTL_HEIGHT    = MoonshinePixelDungeon.landscape()?18:10;
//	private final int BTN_HEIGHT    = MoonshinePixelDungeon.landscape()?18:12;
	private final int TTL_HEIGHT    = 18;
	private final int BTN_HEIGHT    = 18;
	private final int GAP        = 1;

	private boolean editable;
	private ArrayList<CheckBox> boxes;

	public WndChallenges( int checked, boolean editable ) {
		super();

		this.editable = editable;

		resize(WIDTH,HEIGHT);

		RenderedText title = PixelScene.renderText( Messages.get(this, "title"), MoonshinePixelDungeon.landscape()?9:7 );
		title.hardlight( TITLE_COLOR );
		title.x = (WIDTH - title.width()) / 2;
		title.y = (TTL_HEIGHT - title.height()) / 2;
		PixelScene.align(title);
		add( title );

		boxes = new ArrayList<>();

		Component cp = new Component();

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);
		panel.x=0;
		panel.y=TTL_HEIGHT;
		panel.size(WIDTH,HEIGHT-TTL_HEIGHT);
		add(panel);

		float pos = 0;
		cp.setSize(WIDTH,pos);

		for (int i = 0; i < Challenges.NAME_IDS.length; i++) {

			CheckBox cb = new CheckBox(Messages.get(Challenges.class, Challenges.NAME_IDS[i]),7);
			cb.checked((checked & Challenges.MASKS[i]) != 0);
			cb.active = editable;

			if (i > 0) {
				pos += GAP;
			}
			cb.setRect(0, pos, panel.innerWidth()*5/6, BTN_HEIGHT);
			pos += cb.height();

			cp.add(cb);
			boxes.add(cb);
		}
		cp.setSize(panel.innerWidth(),pos);
		ScrollPane sp = new ScrollPane(cp);
		add(sp);
		sp.setRect(panel.marginLeft(),TTL_HEIGHT+panel.marginTop(),panel.innerWidth(),panel.innerHeight());
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