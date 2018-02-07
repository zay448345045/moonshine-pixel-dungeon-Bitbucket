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
package com.moonshinepixel.moonshinepixeldungeon.scenes;

import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.Unlocks;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.*;
import com.moonshinepixel.moonshinepixeldungeon.utils.Holidays;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndOptions;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndRunSettings;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.*;
import com.watabou.noosa.ui.Component;

//TODO: update this class with relevant info as new versions come out.
public class MoonshopScene extends PixelScene {
	RenderedTextMultiline stones;
	Image moon;
	NinePatch pn1;
	public static Class<? extends PixelScene> lastScene;
	@Override
	public void create() {
		super.create();

		int w = Camera.main.width;
		int h = Camera.main.height;

		RenderedText title = renderText( Messages.get(this, "title"), 12 );
		title.hardlight(Window.TITLE_COLOR);
		title.x = (w - title.width()) / 2 ;
		title.y = 4;
		align(title);
		add(title);

		moon = Icons.get(Icons.MOON);
		pn1 = Chrome.get(Chrome.Type.TOAST);
		add(pn1);

		ExitButton btnExit = new ExitButton(lastScene);
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = 145 + panel.marginLeft() + panel.marginRight() - 2;
		int ph = h - 16;

		panel.size( pw, ph );
		panel.x = (w - pw) / 2f;

		panel.y = title.y + title.height();
		align( panel );
		add( panel );

		if(MoonshinePixelDungeon.landscape()) {
			pn1.x = 3;
			pn1.y = 3;
			pn1.size(moon.width + pn1.marginHor() + 2, moon.height + pn1.marginVer() + 2);
			moon.x = pn1.x + pn1.marginLeft() + 1;
			moon.y = pn1.y + pn1.marginTop() + 1;
			add(moon);
		} else{
			pn1.x = panel.x+panel.marginLeft();
			pn1.y = panel.y+panel.marginTop();
			pn1.size(moon.width + pn1.marginHor() + 2, moon.height + pn1.marginVer() + 2);
			moon.x = pn1.x + pn1.marginLeft() + 1;
			moon.y = pn1.y + pn1.marginTop() + 1;
			add(moon);
		}

		stones = new RenderedTextMultiline(9);
		stones.setPos(moon.x+moon.width+2,pn1.y+9);
		align(stones);
		add(stones);
		//System.out.println(stones.width()+"|"+stones.height());

		ScrollPane list = new ScrollPane( new Component() );
		add( list );

		Component content = list.content();
		content.clear();

		int pos = 0;
		for(int i = 0; i<Unlocks.unlockables().length;i++){
			ShopButton sb = new ShopButton(Unlocks.unlockables()[i]);
			content.add(sb);

			sb.setRect(0,pos,panel.innerWidth(),20);
			pos=(int)sb.bottom()+2;
		}

		content.setSize( panel.innerWidth(), (int)Math.ceil(pos) );

		if(MoonshinePixelDungeon.landscape()) {
			list.setRect(
					panel.x + panel.marginLeft(),
					panel.y + panel.marginTop() - 1,
					panel.innerWidth(),
					panel.innerHeight() + 2);
		} else {
			list.setRect(
					panel.x + panel.marginLeft(),
					pn1.y + pn1.height,
					panel.innerWidth(),
					panel.innerHeight() + 2-pn1.height);
		}
		list.scrollTo(0, 0);

		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		fadeIn();
	}

	@Override
	public void update() {
		super.update();
		stones.text(MoonshinePixelDungeon.moonstones()+"");
		stones.setRect(moon.x+moon.width+2,pn1.center().y-stones.height()/2,stones.textWidth()/1.5f,stones.texHeight());
		pn1.size(stones.right()-pn1.x,pn1.height);
	}

	@Override
	protected void onBackPressed() {
		MoonshinePixelDungeon.switchNoFade( lastScene );
	}

	public class ShopButton extends RedButton{
		public final int price;
		private boolean sold;
		private Image moon;
		private NinePatch whiteBG;
		private RenderedTextMultiline priceTXT;
		private Unlocks id;

		public ShopButton(Unlocks id){
			super(id.dispName(),id.textSize());
			price=Unlocks.price(id);
			sold=id.isUnlocked();
			this.id=id;
			priceTXT = new RenderedTextMultiline(9);
			add(priceTXT);
			if (sold){
				erase(moon);
				erase(whiteBG);
			}
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			whiteBG = Chrome.get(Chrome.Type.WHITEBG);
			moon = Icons.get(Icons.MOON);

			if(!sold) {
				add(whiteBG);
				add(moon);
			}
		}

		@Override
		protected void layout() {
			super.layout();
			text.x=x+2;
			if(!sold) {
				priceTXT.text(price + "");

				whiteBG.size(priceTXT.width() + 2, priceTXT.height() + 2);
				whiteBG.x = x + width - whiteBG.width - 3;
				whiteBG.y = centerY() - whiteBG.height / 2;

				priceTXT.setPos(whiteBG.x + 1,centerY() - priceTXT.height() / 2);

				moon.x = whiteBG.x - moon.width - 2;
				moon.y = centerY() - moon.height / 2;
			} else {
				priceTXT.text(Messages.get(this, "sold"));
//                priceTXT.measure();

				priceTXT.setPos(x+width -priceTXT.width()-2,centerY() - priceTXT.height() / 2);
			}
		}

		public void sold(){
			sold=true;
			erase(whiteBG);
			erase(moon);
			layout();
			Unlocks.unlock(id);
			MoonshinePixelDungeon.moonstones(MoonshinePixelDungeon.moonstones()-price);
			if (id==Unlocks.ITEMRENAMING) {
				WndTitledMessage wtm = new WndTitledMessage(Icons.get(Icons.INFO),"Item renaming","Press and hold on item to rename it");
				MoonshopScene.this.add(wtm);
			}
			MoonshinePixelDungeon.switchNoFade(MoonshopScene.class);
		}

		@Override
		protected void onClick() {
			if (!sold) {
				boolean canBuy = MoonshinePixelDungeon.moonstones() >= price;
				WndOptions wo = new WndOptions(
						id.dispName(),
						Messages.get(this, "buy",id.dispName(),price),
						canBuy?Messages.get(this, "yes"):Messages.get(this, "yes_1",price-MoonshinePixelDungeon.moonstones()),
						Messages.get(this, "no")) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							sold();
						}
					}
				};
				wo.setEnabled(canBuy, true);
				MoonshopScene.this.add(wo);
			} else if(id==Unlocks.ITEMRENAMING){
				WndTitledMessage wtm = new WndTitledMessage(Icons.get(Icons.INFO),"Item renaming","Press and hold on item to rename it");
				MoonshopScene.this.add(wtm);
			}
		}
	}
}


