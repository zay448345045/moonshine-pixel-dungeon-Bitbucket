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

import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.CheckBox;
import com.moonshinepixel.moonshinepixeldungeon.ui.Icons;
import com.moonshinepixel.moonshinepixeldungeon.ui.OptionSlider;
import com.moonshinepixel.moonshinepixeldungeon.ui.ScrollPane;
import com.moonshinepixel.moonshinepixeldungeon.scenes.StartScene;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndRunSettings extends WndTabbed {

	private static final String TXT_SWITCH_FULL = "Switch to fullscreen";
	private static final String TXT_SWITCH_WIN = "Switch to windowed";

	private static final String TXT_BINDINGS	= "Key bindings";

	private static final int WIDTH		    = 150;
	private static final int HEIGHT         = 138;
	private static final int SLIDER_HEIGHT	= 24;
	private static final int BTN_HEIGHT	    = 18;
	private static final int GAP_TINY 		= 2;
	private static final int GAP_SML 		= 6;
	private static final int GAP_LRG 		= 18;

	private MainTab main;
	private DevTab unknown;
	private ChallengesTab challenges;
	private Camera CAM;
	private ScrollPane list;
	private boolean editable = Badges.isUnlocked( Badges.Badge.VICTORY ) || (MoonshinePixelDungeon.devOptions()&1)!=0;
//	private boolean editable=true;

	private static int last_index = 0;

	private boolean fade = false;

	public WndRunSettings() {
		super();
//		editable=true;

		CAM=camera;

		main = new MainTab();
		add(main);

		unknown = new DevTab();
		add(unknown);

		challenges = new ChallengesTab();
		add(challenges);

		add( new LabeledTab(Messages.get(this, "main")){
			@Override
			protected void select(boolean value) {
				super.select(value);
				main.visible = main.active = value;
				if (value) last_index = 0;
			}
		});
		LabeledTab uiLabel = new LabeledTab(Messages.get(this, "gear")){
			@Override
			protected void select(boolean value) {
				super.select(value);
				unknown.visible = unknown.active = value;
				if (value) last_index = 1;
			}
		};
		add( uiLabel );
        uiLabel.setEnabled(MoonshinePixelDungeon.devlevel()>0);
//        uiLabel.setEnabled(true);

        LabeledTab chLabel = new LabeledTab(Messages.get(this, "challenges")){
            @Override
            protected void select(boolean value) {
					super.select(value);
					challenges.visible = challenges.active = value;
					if (value) {
						last_index = 2;
						if (!editable) {
							Game.scene().add(new WndMessage(Messages.get(StartScene.class, "need_to_win")));
						}
					}
            }
        };

		add( chLabel );
		chLabel.setEnabled(true);

		resize(WIDTH, HEIGHT);

		layoutTabs();

		select(last_index);

	}

	@Override
	public void onBackPressed() {

		if (editable) {
			int value = 0;
			for (int i = 0; i < challenges.boxes.size(); i++) {
				if (challenges.boxes.get( i ).checked()) {
					value |= Challenges.MASKS[i];
				}
			}
			MoonshinePixelDungeon.challenges( value );
		}
		MoonshinePixelDungeon.devOptions(unknown.devModeNum);

		super.onBackPressed();
		if (!fade)
			MoonshinePixelDungeon.switchNoFade( StartScene.class );
		else
			MoonshinePixelDungeon.switchScene( StartScene.class );
	}

	private class MainTab extends Group {

		public MainTab() {
			super();
			OptionSlider genderSlider = new OptionSlider(Messages.get(this, "gender"),
					Messages.get(this, "male"), Messages.get(this, "female"), 0, 1) {
				@Override
				protected void onChange() {
					MoonshinePixelDungeon.lastGender(getSelectedValue());
					fade=true;
				}
			};
			genderSlider.setSelectedValue(MoonshinePixelDungeon.lastGender());
			genderSlider.setRect(0, 0, WIDTH, SLIDER_HEIGHT);
			add(genderSlider);

			OptionSlider storylineSlider = new OptionSlider(Messages.get(this, "storyline"),
					Messages.get(this, "classic"), Messages.get(this, "moonshine"), 0, 1) {
				@Override
				protected void onChange() {
					MoonshinePixelDungeon.storyline(getSelectedValue());
				}
			};
			storylineSlider.setSelectedValue(MoonshinePixelDungeon.storyline());
			storylineSlider.setRect(0,genderSlider.height()+9,  WIDTH, SLIDER_HEIGHT);
//			storylineSlider.enabled(Game.previewmode);
			storylineSlider.enabled(true);
			add(storylineSlider);
		}
	}

	private class DevTab extends Group{
		public int devModeNum = MoonshinePixelDungeon.devOptions();
		public DevTab() {
			super();
			System.out.println(devModeNum);
			float bottom = 0;
			final CheckBox dev1 = new CheckBox(Messages.get(this, "dev1")){
				@Override
				protected void onClick() {
					super.onClick();
					if (checked()){
						devModeNum|=1;
						editable=true;
					} else {
						devModeNum ^= 1;
						editable=Badges.isUnlocked( Badges.Badge.VICTORY );
					}
					for (CheckBox cb : challenges.boxes){
						cb.enable(editable);
					}
					System.out.println(devModeNum);
				}
			};
			dev1.checked((devModeNum&1)!=0);
			dev1.setRect(0,bottom,WIDTH,BTN_HEIGHT);
			add(dev1);

			bottom+=BTN_HEIGHT+GAP_SML;
			CheckBox dev2 = new CheckBox(Messages.get(this, "dev2")){
				@Override
				protected void onClick() {
					super.onClick();
					if (checked()){
						devModeNum|=2;
					} else {
						devModeNum ^= 2;
					}
					System.out.println(devModeNum);
				}
			};
			dev2.checked((devModeNum&2)!=0);
			dev2.setRect(0,bottom,WIDTH,BTN_HEIGHT);
			dev2.enable(MoonshinePixelDungeon.devlevel()>1);
			add(dev2);

			bottom+=BTN_HEIGHT+GAP_SML;
			CheckBox dev3 = new CheckBox(Messages.get(this, "dev3")){
				@Override
				protected void onClick() {
					super.onClick();
					if (checked()){
						devModeNum |= 4;
					} else {
						devModeNum ^= 4;
					}
					System.out.println(devModeNum);
				}
			};
			dev3.checked((devModeNum&4)!=0);
			dev3.setRect(0,bottom,WIDTH,BTN_HEIGHT);
			dev3.enable(MoonshinePixelDungeon.devlevel()>2);
			add(dev3);
		}
	}

	private class ChallengesTab extends Group {


		private	final ArrayList<ChallengessCheckBox> boxes;
		public ChallengesTab() {
			int checked= MoonshinePixelDungeon.challenges();
			boxes = new ArrayList<>();
			float pos=0;

			Component challenges = new Component();
			for (int i = 0; i < Challenges.NAME_IDS.length; i++) {

				ChallengessCheckBox cb = new ChallengessCheckBox( Messages.get(Challenges.class, Challenges.NAME_IDS[i]) );
				cb.checked( (checked & Challenges.MASKS[i]) != 0 );
				cb.active = true;

				pos += 1;
				cb.setRect( 0, pos, WIDTH, BTN_HEIGHT );
				pos = cb.bottom();
//				if (i+1==Challenges.NAME_IDS.length){
//				    cb.enable(false);
//				    cb.active=false;
//                }

				challenges.add( cb );
				boxes.add( cb );
			}

			camera=new Camera(0,0,WIDTH,HEIGHT,CAM.zoom);
			camera.x = (int)(Game.width - camera.width * camera.zoom) / 2;
			camera.y = (int)(Game.height - camera.height * camera.zoom) / 2;
			camera.y -= yOffset * camera.zoom;
			camera.scroll.set( chrome.x, chrome.y );
			challenges.setRect(0,0,WIDTH,pos);
//			add(challenges);
			list = new ScrollPane( challenges ){
				ChallengessCheckBox lastClicked;
			@Override
			public void onClick( float x, float y ) {
//				for (ChallengessCheckBox item : boxes) {
//					if (lastClicked==item) {
//						item.onClick(x, y);
//					} else {
//						lastClicked.up();
//					}
//				}
				if (lastClicked!=null) {
					lastClicked.click();
					lastClicked=null;
				}
			}
			public void onTouchDown( float x, float y ) {
				for (ChallengessCheckBox item : boxes) {
					if (item.inside(x,y)) {
						item.onTouchDown(x, y);
						lastClicked=item;
					}
				}
			}

				@Override
				public void onDrag() {
				try {

					lastClicked.up();
					lastClicked = null;
				} catch (Exception e){
					MoonshinePixelDungeon.reportException(e);
				}
				}
			};
			add( list );
			list.setRect(-GAP_SML,-GAP_LRG,WIDTH,HEIGHT);
//			resize( WIDTH, lastY*10);
		}

	}

	private class ChallengessCheckBox extends CheckBox {
		public ChallengessCheckBox( String label ) {
			super( label );

			icon( Icons.get( Icons.UNCHECKED ) );
			enable(editable);
		}
		protected void up(){
			if (editable) if (this.active)
			onTouchUp();
		}
		protected void onClick(float x, float y) {
			if (editable) if (this.active)
			if (inside( x, y )){
				onClick();
				onTouchUp();
			}
		}
		protected void click() {
			if (editable) if (this.active)
			if (inside( x, y )){
				onClick();
				onTouchUp();
			}
		}
		protected void onTouchDown(float x, float y) {
			if (editable) if (this.active)
			if (inside( x, y )){
				onTouchDown();
			}
		}

	}
}
