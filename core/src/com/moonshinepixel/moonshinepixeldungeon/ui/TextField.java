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

import com.badlogic.gdx.Gdx;
import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.moonshinepixel.moonshinepixeldungeon.utils.TextInput;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.noosa.*;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

public abstract class TextField extends RedButton {

	private NinePatch white;

	private RenderedText inputText;

	public TextField( String label ) {
		this(label,"");
	}

	public TextField( String label, String txt ){
		super(label, 9);

		inputText = PixelScene.renderText( 7 );
		inputText.text( txt );
		add( inputText );
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		white = Chrome.get(Chrome.Type.WHITEBG);
		add( white );
	}

	@Override
	protected void onClick() {
		TextInput tinp = new TextInput(){
			@Override
			public void input(String text) {
				text( text );
				onTextChange();
			}

			@Override
			public void canceled() {
				onTextCancel();
			}
		};
		if (Game.isAndroid()){
			Gdx.input.getTextInput(tinp, text.text(), inputText.text(), "");
		} else {
			TextInput.getTextInput(tinp, text.text(), inputText.text(), "");
		}
	}

	public abstract void onTextChange();

	public abstract void onTextCancel();

	@Override
	public void text(String value) {
		inputText.text(value);
		layout();
	}

	public void label(String value){
		text.text(value);
		layout();
	}

	public String text(){
		return inputText.text();
	}

	@Override
	public void enable(boolean value) {
		super.enable(value);
		inputText.alpha( value ? 1.0f : 0.3f );
		white.alpha( value ? 1.0f : 0.3f );
	}

	@Override
	protected void layout() {
		super.layout();

		float right = x;

		bg.x = x;
		bg.y = y;
		bg.size( width, height );

		text.x = right + 2;
		text.y = y + (height - text.baseLine()) / 2;
		PixelScene.align(text);

		right=text.x+text.width()+2;

		if (icon != null) {
			icon.x = right;
			icon.y = y + (height - icon.height()) / 2;
			PixelScene.align(icon);
			right=icon.x+icon.width()+2;
		}

		white.x=right;
		white.y=y+height()/8;
		float mod = parent instanceof PixelScene?-1:1;
		white.size((width-4-right)*mod,height-height/4);
//		white.hardlight(0xFF0000);
		right+=2;

		inputText.x=right+1;
		inputText.y = y + (height - inputText.baseLine()) / 2;
		PixelScene.align(inputText);
	}
}
