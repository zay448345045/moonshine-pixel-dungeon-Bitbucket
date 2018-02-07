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

import com.moonshinepixel.moonshinepixeldungeon.ui.ScrollPane;
import com.moonshinepixel.moonshinepixeldungeon.Chrome;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.ui.Archs;
import com.moonshinepixel.moonshinepixeldungeon.ui.ExitButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.RenderedTextMultiline;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.utils.Holidays;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

//TODO: update this class with relevant info as new versions come out.
public class ChangesScene extends PixelScene {

	private static final String TXT_Update = (Holidays.getHoliday()==Holidays.HWEEN?"_Happy Halloween!_\n\n":"")+
			"_NOTE:_ Moonshine PD does not currently support translations.\n" +
					"\n" +
					"_v0.1.32(33) - alpha:_\n" +
					"_-_ Fixed some crashes\nImplemented new save system" +
					"\n" +
					"\n" +
					"_v0.1.31 - alpha:_\n" +
					"_-_ Fixed lot of crashes (again)\nFixed some bugs\nSlightly buffed _Amnesia_ and _Analgesia_ challenges\nFixed challenges window" +
					"\n" +
					"\n" +
					"_v0.1.30 - alpha:_\n" +
					"_-_ Fixed lot of crushes\nFixed badge validating\nFixed Wand of Corruption (again...)\nUpdated about menu\nReplaced ash bomb with Stun bomb"+
					"\n" +
					"\n" +
					"_v0.1.29 - alpha:_\n" +
					"_-_ Lots of bugfixes\nscroll of magic mapping can no longer be used in garden boss level"+
					"\n" +
					"\n" +
					"_v0.1.28 - alpha:_\n" +
					"_-_ Bugfixes"+
					"\n" +
					"\n" +
					"_v0.1.27 - alpha:_\n" +
					"_-_ Fixed loading pre-0.1.25 saves\nFixed null-items in inventory\nFixed 0-cell FOV\nFixed all items were broken on save loading\nFixed !!NO_TEXT_FOUND!!! named challenge"+
					"\n" +
					"\n" +
					"_v0.1.26 - alpha (android):_\n" +
					"_-_ Fixed font errors"+
					"\n" +
					"\n" +
					"_v0.1.25 - alpha:_\n" +
					"_-_ Added new Challenge: _Rust_\nPorted _Ring of Wealth_ and _Wand of Corruption_ from newest Shattered PD\nFixed some crashes\nAdded new enchantment - _Greedy_"+
					"\n" +
					"\n" +
					"_v0.1.24 - alpha:_\n" +
					"_-_ Bugfix"+
					"\n" +
					"\n" +
					"_v0.1.23 - alpha:_\n" +
					"_-_ Added 2 new Challenges: _Extermination_ and _Facie ad faciem_\nRain poppy seeds can now spawn when trampling grass\nFixed Rat Kins sleep animation\nFixed Map seed button in rankings window\nSlightly changed Tengu bossfight"+
					"\n" +
					"\n" +
					"_v0.1.22 - alpha:_\n" +
					"_-_ Added 4 new Challenges: _Plan overfulfillment_, _Mutagen_, _Resurrection_ and _Extreme caution_\nPorted _Potion of Healing_ and _Dew Vial_ logic from newest Shattered PD\nAdded new Plant - _Rain Poppy_ used to brew _Potion of Storm_\nFixed bugs with upgrading cursed weapon, armor, etc.\nFixed Disassembling Rings to scrap with Gunslinger's Subbag"+
					"\n" +
					"\n" +
					"_v0.1.21 - alpha:_\n" +
					"_-_ Skipped"+
					"\n" +
					"\n" +
					"_v0.1.20 - alpha:_\n" +
					"_-_ Some mobs now have sleeping animation\nNew challenge: _Analgesia_\nFixed bug with rankings saving"+
					"\n" +
					"\n" +
					"_v0.1.19 - alpha:_\n" +
					"_-_ Quickfix: removed infinite moonstones given on game start\nFixed drunk buff icon"+
					"\n" +
					"\n" +
					"_v0.1.18 - alpha:_\n" +
					"_-_ You now can have unlimited records in rankings\nAdded some new items to Moonstone shop\nFixed some bugs from 0.1.17"+
					"\n" +
					"\n" +
					"_v0.1.17 - alpha:_\n" +
					"_-_ Update checker now available on android\nFixed scythe\nAdded item renaming, you can buy it in new moonstone shop using moonstones that drops from bosses."+
					"\n" +
					"\n" +
					"_v0.1.16 - alpha:_\n" +
					"_-_ New item - _Bottle of moonshine_, new _Drunk_ buff\nRandom nicknames generator\nFixed screen orientation and back buttons on android\nLots of bugfixes"+
					"\n" +
					"\n" +
					"_v0.1.15 - alpha:_\n" +
					"_-_ Now you can change name of your hero. Some bugfixes"+
					"\n" +
					"\n" +
					"_v0.1.14 - alpha (android only):_\n" +
					"_-_ Fixed game not saving, fixed lots of crashes"+
					"\n" +
					"\n" +
					"_v0.1.13 - alpha:_\n" +
					"_-_ Not quite gameplay changing but some bugfixes and first android release"+
					"\n" +
					"\n" +
					"_v0.1.12 - alpha:_\n" +
					"_-_ All weapons now have 5% chance to spawn with +-1 tier, Shopkeeper now will attack hero instead of fleeing, Goo now drops weapon upgrade kit instead of Lloyds Beacon. Lloyds Beacon now generates as regular artifact. Improved guns stats description"+
					"\n" +
					"\n" +
					"_v0.1.11 - alpha:_\n" +
					"_-_ Added new weapon - bamboo spear. Reworked Lucky enchantment to make it more useful. Switch hook, Giant shuriken, Steel claw and Bamboo spear now have random tier from 2 to 5"+
					"\n" +
					"\n" +
					"_v0.1.10 - alpha:_\n" +
					"_-_ Added new weapon - switch hook. Some bugfixes"+
					"\n" +
					"\n" +
					"_v0.1.9 - alpha:_\n" +
					"_-_ Nerfed blunderbuss(again), some bugfixes, now you can test new boss(WIP)(coming with cool intro)"+
					"\n" +
					"\n" +
					"_v0.1.8 - alpha:_\n" +
					"_-_ Fixed some bugs, Added cluster bomb, added new enemy - bombergnoll"+
					"\n" +
					"\n" +
					"_v0.1.7 - alpha:_\n" +
					"_-_ Fixed crash on leaving tengu's floor, Fixed bugs with gunslinger's subbag, some other guns-related bugfixes"+
					"\n" +
					"\n" +
					"_v0.1.6 - alpha:_\n" +
					"_-_ Bugfixes for room types, lowered bullet's debuff chance and damage, some bugfixes."+
					"\n" +
					"\n" +
					"_v0.1.5 - alpha:_\n" +
					"_-_ Some new room types, buffed guns, some bugfixes.\nRemoved all non-default bullets.\nBullets now have a chance to affect debuffs on target"+
					"\n" +
					"\n" +
					"_v0.1.4 - alpha:_\n" +
					"_-_ LOTS of bugfixes with levelgen and especially with blackjack challenge\nOld saves no longer openable"+
					"\n" +
					"\n" +
					"_v0.1.3 - alpha:_\n" +
					"_-_ Again bugfixes"+
					"\n" +
					"\n" +
					"_v0.1.1 - alpha:_\n" +
					"_-_ Some bugfixes"+
					"\n" +
					"\n" +
					"_v0.1.0 - alpha:_\n" +
					"_-_ First public alpha";

	@Override
	public void create() {
		super.create();

		int w = Camera.main.width;
		int h = Camera.main.height;

		RenderedText title = renderText( Messages.get(this, "title"), 9 );
		title.hardlight(Window.TITLE_COLOR);
		title.x = (w - title.width()) / 2 ;
		title.y = 4;
		align(title);
		add(title);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		RenderedTextMultiline text = renderMultiline(TXT_Update, 6 );

		NinePatch panel = Chrome.get(Chrome.Type.TOAST);

		int pw = 145 + panel.marginLeft() + panel.marginRight() - 2;
		int ph = h - 16;

		panel.size( pw, ph );
		panel.x = (w - pw) / 2f;
		panel.y = title.y + title.height();
		align( panel );
		add( panel );

		ScrollPane list = new ScrollPane( new Component() );
		add( list );

		Component content = list.content();
		content.clear();

		text.maxWidth((int) panel.innerWidth());

		content.add(text);

		content.setSize( panel.innerWidth(), (int)Math.ceil(text.height()) );

		list.setRect(
				panel.x + panel.marginLeft(),
				panel.y + panel.marginTop() - 1,
				panel.innerWidth(),
				panel.innerHeight() + 2);
		list.scrollTo(0, 0);

		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		fadeIn();
	}

	@Override
	protected void onBackPressed() {
		MoonshinePixelDungeon.switchNoFade(TitleScene.class);
	}
}


