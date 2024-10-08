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

import com.badlogic.gdx.Input;
import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

import java.util.ArrayList;

public class WndTabbed extends Window {

	protected ArrayList<Tab> tabs = new ArrayList<Tab>();
	protected Tab selected;
	
	public WndTabbed() {
		super( 0, 0, Chrome.get( Chrome.Type.TAB_SET ) );
	}
	
	protected Tab add( Tab tab ) {

		tab.setPos( tabs.size() == 0 ?
			-chrome.marginLeft() + 1 :
			tabs.get( tabs.size() - 1 ).right(), height );
		tab.select( false );
		super.add( tab );
		
		tabs.add( tab );
		
		return tab;
	}
	
	public void select( int index ) {
		select( tabs.get( index ) );
	}
	
	public void select( Tab tab ) {
		if (tab != selected && tab.enabled) {
			for (Tab t : tabs) {
				if (t == selected) {
					t.select( false );
				} else if (t == tab) {
					t.select( true );
				}
			}
			
			selected = tab;
		}
	}
	
	@Override
	public void resize( int w, int h ) {
		// -> super.resize(...)
		this.width = w;
		this.height = h;
		
		chrome.size(
			width + chrome.marginHor(),
			height + chrome.marginVer() );
		
		camera.resize( (int)chrome.width, chrome.marginTop() + height + tabHeight());
		camera.x = (int)(Game.width - camera.screenWidth()) / 2;
		camera.y = (int)(Game.height - camera.screenHeight()) / 2;
		camera.y += yOffset * camera.zoom;

		shadow.boxRect(
				camera.x / camera.zoom,
				camera.y / camera.zoom,
				chrome.width(), chrome.height );
		// <- super.resize(...)
		
		for (Tab tab : tabs) {
			remove( tab );
		}
		
		ArrayList<Tab> tabs = new ArrayList<Tab>( this.tabs );
		this.tabs.clear();
		
		for (Tab tab : tabs) {
			add( tab );
		}
	}

	public void layoutTabs(){
		//subract two as there's extra horizontal space for those nobs on the top.
		int fullWidth = width+chrome.marginHor()-2;
		int numTabs = tabs.size();

		if (numTabs == 0)
			return;
		if (numTabs == 1) {
			tabs.get(0).setSize(fullWidth, tabHeight());
			return;
		}

		int spaces = numTabs-1;
		int spacing = -1;

		while (spacing == -1) {
			for (int i = 0; i <= 3; i++){
				if ((fullWidth - i*(spaces)) % numTabs == 0) {
					spacing = i;
					break;
				}
			}
			if (spacing == -1) fullWidth--;
		}

		int tabWidth = (fullWidth - spacing*(numTabs-1)) / numTabs;

		for (int i = 0; i < tabs.size(); i++){
			tabs.get(i).setSize(tabWidth, tabHeight());
			tabs.get(i).setPos( i == 0 ?
					-chrome.marginLeft() + 1 :
					tabs.get( i - 1 ).right() + spacing, height );
		}

	}
	
	protected int tabHeight() {
		return 25;
	}
	
	protected void onClick( Tab tab ) {
		select( tab );
	}

	@Override
	protected void onKeyDown(NoosaInputProcessor.Key key) {
		if (key.code == Input.Keys.TAB) {
			int next = 0;
			for (int j = 0; j < tabs.size(); j++) {
				Tab t = tabs.get(j);
				if (t == selected) {
					next = (j + 1) % tabs.size();
					break;
				}
			}
			select(next);
		}
	}

	protected class Tab extends Button<GameAction> {
		
		protected final int CUT = 5;
		
		protected boolean selected;

		protected boolean enabled=true;
		
		protected NinePatch bg;

		@Override
		protected void layout() {
			super.layout();
			
			if (bg != null) {
				bg.x = x;
				bg.y = y;
				bg.size( width, height );
			}
		}

		protected void select( boolean value ) {
			
			active = !(selected = value);
			
			if (bg != null) {
				remove( bg );
			}
			
			bg = Chrome.get( selected ?
				Chrome.Type.TAB_SELECTED :
				Chrome.Type.TAB_UNSELECTED );
			addToBack( bg );
			
			layout();
		}
		
		@Override
		protected void onClick() {
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
            WndTabbed.this.onClick(this);
		}
	}
	
	protected class LabeledTab extends Tab {
		
		private RenderedText btLabel;
		
		public LabeledTab( String label ) {
			
			super();
			
			btLabel.text( label );
		}

        public void setEnabled(boolean enabled){
            if (enabled){
                this.enabled=true;
            } else {
                this.enabled=false;
            }
        }

		@Override
		protected void createChildren() {
			super.createChildren();
			
			btLabel = PixelScene.renderText( 9 );
			add( btLabel );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			btLabel.x = x + (width - btLabel.width()) / 2;
			btLabel.y = y + (height - btLabel.baseLine()) / 2 - 1;
			if (!selected) {
				btLabel.y -= 2;
			}
			if (!enabled){
                bg.am=1.0f;
                bg.tint(0,0,0, 0.5f);
                btLabel.tint(0,0,0, 0.5f);
            }
			PixelScene.align(btLabel);
		}
		
		@Override
		protected void select( boolean value ) {
			super.select( value );
			btLabel.am = selected ? 1.0f : 0.6f;
		}
	}

}
