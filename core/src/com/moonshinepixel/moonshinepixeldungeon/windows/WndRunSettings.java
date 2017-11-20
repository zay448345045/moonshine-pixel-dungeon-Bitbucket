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
import com.moonshinepixel.moonshinepixeldungeon.Unlocks;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.MoonshopScene;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.*;
import com.moonshinepixel.moonshinepixeldungeon.scenes.StartScene;
import com.moonshinepixel.moonshinepixeldungeon.utils.DungeonSeed;
import com.moonshinepixel.moonshinepixeldungeon.utils.HeroNames;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class WndRunSettings extends WndTabbed {

	private static final String TXT_SWITCH_FULL = "Switch to fullscreen";
	private static final String TXT_SWITCH_WIN = "Switch to windowed";

	private static final String TXT_BINDINGS	= "Key bindings";

	private static int WIDTH		    	= 150;
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
	private boolean editable = Unlocks.isUnlocked(Unlocks.CHALLENGES);
//	private boolean editable=true;

	private static int last_index = 0;

	private boolean fade = false;

	public WndRunSettings() {
		super();
//		editable=true;

		WIDTH=Math.min(Camera.main.width-Camera.main.width/4,150);

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
			float top = 0;
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

			top+=genderSlider.height()+9;

			OptionSlider storylineSlider = new OptionSlider(Messages.get(this, "storyline"),
					Messages.get(this, "classic"), Messages.get(this, "moonshine"), 0, 1) {
				@Override
				protected void onChange() {
					MoonshinePixelDungeon.storyline(getSelectedValue());
				}
			};
			storylineSlider.setSelectedValue(MoonshinePixelDungeon.storyline());
			storylineSlider.setRect(0,top,  WIDTH, SLIDER_HEIGHT);
			storylineSlider.enabled(true);
			add(storylineSlider);

			top+=storylineSlider.height()+9;

			final Caller caller = new Caller() {
				public RedButton butt;
				@Override
				public void call() {
					butt.enable(true);
				}

				@Override
				public void func1(Object obj) {
					butt=(RedButton)obj;
				}
			};

			final TextField name = new TextField("Name", MoonshinePixelDungeon.heroName()){
				@Override
				public void onTextChange() {
					if (text().equals("")){
						MoonshinePixelDungeon.heroName(Messages.get(Hero.class, "name"));
					} else {
						MoonshinePixelDungeon.heroName(text());
					}
					if (!HeroNames.hasTitle(text()))
						caller.call();
				}
			};
			name.setRect(0,top, WIDTH-GAP_TINY-SLIDER_HEIGHT/2, SLIDER_HEIGHT);
			add(name);

			RedButton rndName = new RedButton("?", 9){
				@Override
				protected void onClick() {
					name.text(HeroNames.getName(MoonshinePixelDungeon.lastGender()==0?HeroNames.MALE:HeroNames.FEMALE,.0f));
					MoonshinePixelDungeon.heroName(name.text());
					if (!HeroNames.hasTitle(name.text()))
						caller.call();
				}
			};

			rndName.setRect(name.right()+GAP_TINY,name.top(),SLIDER_HEIGHT/2,SLIDER_HEIGHT/2);
			add(rndName);
			top=rndName.bottom()+GAP_SML;
			RedButton rndTitle = new RedButton("?", 9){
				@Override
				protected void onClick() {
					name.text(HeroNames.titledName(name.text()));
					MoonshinePixelDungeon.heroName(name.text());
//					enable(false);
				}
			};

			rndTitle.setRect(name.right()+GAP_TINY,rndName.bottom(),SLIDER_HEIGHT/2,SLIDER_HEIGHT/2);

			caller.func1(rndTitle);

			add(rndTitle);

			top = name.bottom()+GAP_SML;

			RedButton shop = new RedButton(Messages.get(this,"shop"), 9){
				@Override
				protected void onClick() {
//					WndRunSettings.this.add(new WndMoonShop());
					MoonshinePixelDungeon.switchScene(MoonshopScene.class);
				}
			};

			shop.setRect(0,top,WIDTH,BTN_HEIGHT);
			add(shop);

//			rndTitle.enable(false);
		}
	}

	private class DevTab extends Group{

		public TextField tf;
		public int devModeNum = MoonshinePixelDungeon.devOptions();
		public DevTab() {
			super();
			//System.out.println(devModeNum);
			float bottom = 0;

//			final CheckBox dev1 = new CheckBox(Messages.get(this, "dev1")){
//				@Override
//				protected void onClick() {
//					super.onClick();
//					if (checked()){
//						devModeNum|=1;
//						editable=true;
//					} else {
//						devModeNum ^= 1;
//						editable=Badges.isUnlocked( Badges.Badge.VICTORY );
//					}
//					for (CheckBox cb : challenges.boxes){
//						cb.enable(editable);
//					}
//					//System.out.println(devModeNum);
//				}
//			};
//			dev1.checked((devModeNum&1)!=0);
//			dev1.setRect(0,bottom,WIDTH,BTN_HEIGHT);
//			add(dev1);

			RenderedTextMultiline title = PixelScene.renderMultiline(Messages.get(this, "title"),9);
			title.maxWidth(WIDTH);
			title.setRect(0,0,WIDTH,SLIDER_HEIGHT*1.5f);
			add(title);
			bottom=title.bottom()+GAP_TINY;

//			bottom+=BTN_HEIGHT+GAP_SML;
			CheckBox dev2 = new CheckBox(Messages.get(this, "dev2")){
				@Override
				protected void onClick() {
					super.onClick();
					if (checked()){
						devModeNum|=2;
					} else {
						devModeNum ^= 2;
					}
					//System.out.println(devModeNum);
				}
			};
			dev2.checked((devModeNum&2)!=0);
			dev2.setRect(0,bottom,WIDTH,BTN_HEIGHT);
			dev2.enable(Unlocks.isUnlocked(Unlocks.TOMSTART));
			dev2.lock(!Unlocks.isUnlocked(Unlocks.TOMSTART));
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
					//System.out.println(devModeNum);
				}
			};
			dev3.checked((devModeNum&4)!=0);
			dev3.setRect(0,bottom,WIDTH,BTN_HEIGHT);
			dev3.enable(Unlocks.isUnlocked(Unlocks.INVULNERABILITY));
			dev3.lock(!Unlocks.isUnlocked(Unlocks.INVULNERABILITY));
			add(dev3);

			bottom=dev3.bottom()+GAP_SML;

			CheckBox seed = new CheckBox(Messages.get(this,"seed1")){
				@Override
				protected void onClick() {
					super.onClick();
					if (checked()){
						MoonshinePixelDungeon.customSeed(true);
						tf.enable(true);
					} else {
						MoonshinePixelDungeon.customSeed(false);
						tf.enable(false);
					}
				}
			};
			seed.checked(MoonshinePixelDungeon.customSeed());
			seed.setRect(0,bottom,WIDTH,BTN_HEIGHT);
			add(seed);

			bottom=seed.bottom()+GAP_TINY;

			tf = new TextField(Messages.get(this,"seed2"),MoonshinePixelDungeon.seed()){
				@Override
				public void onTextChange() {
					super.onTextChange();
					MoonshinePixelDungeon.seed(text());
					text(MoonshinePixelDungeon.seed());
				}
			};
			tf.enable(MoonshinePixelDungeon.customSeed());
			tf.setRect(0,bottom,WIDTH,SLIDER_HEIGHT);
			add(tf);
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

				challenges.add( cb );
				boxes.add( cb );
			}

			camera=new Camera(0,0,WIDTH,HEIGHT,CAM.zoom);
			camera.x = (int)(Game.width - camera.width * camera.zoom) / 2;
			camera.y = (int)(Game.height - camera.height * camera.zoom) / 2;
			camera.y -= yOffset * camera.zoom;
			camera.scroll.set( chrome.x, chrome.y );
			challenges.setRect(0,0,WIDTH,pos);
			list = new ScrollPane( challenges ){
				ChallengessCheckBox lastClicked;
			@Override
			public void onClick( float x, float y ) {
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
		}

	}

	private class ChallengessCheckBox extends CheckBox {
		public ChallengessCheckBox( String label ) {
			super( label );

			icon( Icons.get( Icons.UNCHECKED ) );
			enable(editable);
			lock(!editable);
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

	private abstract class Caller{

		public abstract void call();

		public void func1(Object obj){

		}

		public void func2(Object obj){

		}
	}
}
