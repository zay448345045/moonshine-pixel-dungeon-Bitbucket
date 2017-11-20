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

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.RepeatingImage;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.GameMath;

public class RedButton extends Button<GameAction> {
	
	protected NinePatch bg;
	protected RenderedText text;
	protected Image icon;
	protected Image lock;
	protected NinePatch black;
	protected RepeatingImage chain1;
	protected RepeatingImage chain2;

	public void invert(){
	    if (bg!=null) {
            bg.invert();
        }
        if (text!=null) {
            text.invert();
        }
        if (icon!=null) {
            icon.invert();
        }
	}

	public RedButton( String label ) {
		this(label, 9);
	}

	public RedButton( String label, int size ){
		super();

		text = PixelScene.renderText( size );
		text.text( label );
		add( text );
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();
		
		bg = Chrome.get( Chrome.Type.BUTTON );
		add( bg );
	}
	
	@Override
	protected void layout() {
		
		super.layout();
		
		bg.x = x;
		bg.y = y;
		bg.size( width, height );
		
		text.x = x + (width - text.width()) / 2;
		text.y = y + (height - text.baseLine()) / 2;
		PixelScene.align(text);
		
		if (icon != null) {
			icon.x = x + text.x - icon.width() - 2;
			icon.y = y + (height - icon.height()) / 2;
			PixelScene.align(icon);
		}
		if(black!=null) {
			black.x = x;
			black.y = y;
			black.size(width(), height());
		}
		if(chain1!=null) {
			chain1.x = x;
			chain1.y = y+bg.innerBottom();
			chain1.angle=-GameMath.diagonalAngle(width,height-bg.marginTop());
			//System.out.println("ANGLE: "+chain1.angle);
			chain1.size((int) GameMath.diagonal(width,height-bg.marginTop()),(int)chain1.height);
		}
		if(chain2!=null) {
			chain2.x = x;
			chain2.y = y;
			chain2.angle=GameMath.diagonalAngle(width,height-bg.marginTop());
			//System.out.println("ANGLE: "+chain1.angle);
			chain2.size((int) GameMath.diagonal(width,height-bg.marginTop()),(int)chain2.height);
		}
		if(lock!=null) {
			float x1 = chain1.x+(chain1.width/2)*(float)Math.cos(Math.toRadians(Math.abs(chain1.angle)));
			float y1 = chain1.y-(chain1.width/2)*(float)Math.sin(Math.toRadians(Math.abs(chain1.angle)));
			lock.x = x1 - lock.width / 2;
			lock.y = y1 - lock.height / 2;
		}
	}

	@Override
	protected void onTouchDown() {
		bg.brightness( 1.2f );
		Sample.INSTANCE.play( Assets.SND_CLICK );
	}
	
	@Override
	protected void onTouchUp() {
		bg.resetColor();
	}
	
	public void enable( boolean value ) {
		active = value;
		text.alpha( value ? 1.0f : 0.3f );
	}
	
	public void text( String value ) {
		text.text( value );
		layout();
	}

	public void textColor( int value ) {
		text.hardlight( value );
	}

	public void icon( Image icon ) {
		if (this.icon != null) {
			remove( this.icon );
		}
		this.icon = icon;
		if (this.icon != null) {
			add( this.icon );
			layout();
		}
	}

	public void lock(boolean value){
		erase(lock);
		erase(black);
		erase(chain1);
		erase(chain2);
		lock=null;
		black=null;
		chain1=null;
		chain2=null;
		if (value){
			black = Chrome.get(Chrome.Type.WHITEBG);
			black.color(0,0,0);
			black.alpha(.5f);
			add(black);

			chain1 = new RepeatingImage(Icons.get(Icons.CHAIN));
			add(chain1);
			chain2 = new RepeatingImage(Icons.get(Icons.CHAIN));
			add(chain2);

			lock = Icons.get(Icons.LOCK);
			add(lock);
		}
		layout();
	}
	
	public float reqWidth() {
		return text.width() + 2f;
	}
	
	public float reqHeight() {
		return text.baseLine() + 4;
	}
}
