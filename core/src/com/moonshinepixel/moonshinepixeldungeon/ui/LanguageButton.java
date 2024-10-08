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

import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndMessage;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class LanguageButton extends Button {

	private Image image;

	public LanguageButton() {
		super();

		width = image.width;
		height = image.height;
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		image = Icons.get(Icons.LANGS);
		add( image );
		updateIcon();
	}

	private void updateIcon(){
		switch(Messages.lang().status()){
			case INCOMPLETE:
				image.tint(1, 0, 0, .5f);
				break;
			case UNREVIEWED:
				image.tint(1, .5f, 0, .5f);
				break;
		}
	}

	@Override
	protected void layout() {
		super.layout();

		image.x = x;
		image.y = y;
	}

	@Override
	protected void onTouchDown() {
		image.brightness( 1.5f );
		Sample.INSTANCE.play( Assets.SND_CLICK );
	}

	@Override
	protected void onTouchUp() {
		image.resetColor();
		updateIcon();
	}

	@Override
	protected void onClick() {
		//TODO fix this when language support is added
		parent.add(new WndMessage("Language support is not currently functional due to issues porting the new text system.\n\n" +
				"The desktop version will remain updated for the time being, but in English only.\n\n" +
				"A fix will be released for this in time, thanks for your patience."));
	}

}
