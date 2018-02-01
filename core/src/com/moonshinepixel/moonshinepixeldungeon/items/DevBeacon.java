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
package com.moonshinepixel.moonshinepixeldungeon.items;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.DriedRose;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.TimekeepersHourglass;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.scenes.InterlevelScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.utils.TextInput;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndError;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class DevBeacon extends Item {

	private static final String AC_USE = "USE";

	{
		image = ItemSpriteSheet.BEACON;

		stackable = true;

		defaultAction = AC_USE;

		bones = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_USE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_USE)) {
			Input.TextInputListener listener = new Input.TextInputListener() {
				@Override
				public void input(String text) {
					parseCMD(text);
				}

				@Override
				public void canceled() {

				}
			};
			TextInput.getTextInput(listener,"CMD","","");
		}
	}

	public static void parseCMD(String rawCMD){
		rawCMD=rawCMD.replaceAll("/","");
		String[] cmd = rawCMD.split(" ");
		switch (cmd[0]){
			case "d":
			case "depth":
				descend(cmd);
				return;
			case "g":
			case "give":
				give(cmd);
				return;
			case "s":
			case "str":
				str(cmd);
				return;
			case "e":
			case "exp":
				exp(cmd);
				return;
			case "l":
			case "lvl":
				lvl(cmd);
				return;
			case "p":
			case "pause":
				//TODO: place breakpoint here ↓↓↓
				boolean placeBreakpointHere = true;
				return;
			default:
				MoonshinePixelDungeon.scene().add(new WndError("Command not found"));
		}
	}


	public static void lvl(String[] cmd){
		try {
			int repeats = Integer.parseInt(cmd[1])-1;
			Dungeon.hero.exp=0;
			Dungeon.hero.lvl=1;
			Dungeon.hero.earnExp(0);
			for (int i = 0; i<repeats;i++)
				Dungeon.hero.earnExp(Dungeon.hero.maxExp());
		} catch (NumberFormatException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Value is not a number"));
		} catch (ArrayIndexOutOfBoundsException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Not enough args"));
		}
	}
	public static void exp(String[] cmd){
		try {
			Dungeon.hero.earnExp(Integer.parseInt(cmd[1]));
		} catch (NumberFormatException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Value is not a number"));
		} catch (ArrayIndexOutOfBoundsException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Not enough args"));
		}
	}
	public static void str(String[] cmd){
		try {
			Dungeon.hero.STR=Integer.parseInt(cmd[1]);
		} catch (NumberFormatException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Value is not a number"));
		} catch (ArrayIndexOutOfBoundsException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Not enough args"));
		}
	}

	public static void give(String[] cmd){

		cmd = Arrays.copyOf(cmd,5);
		Class itemCl = Gold.class;
		Item item = new Gold();
		try {
			 itemCl = ClassReflection.forName("com.moonshinepixel.moonshinepixeldungeon.items."+cmd[1]);
			 item = (Item) itemCl.newInstance();
		} catch (ReflectionException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Class not found"));
			return;
		} catch (IllegalAccessException | InstantiationException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Can't Instantiate this class"));
			return;
		} catch (ClassCastException e) {
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Class is not an item"));
			return;
		}
		if (cmd[2]!=null){
			try {
				item.quantity(Integer.parseInt(cmd[2]));
			} catch (NumberFormatException e) {
				MoonshinePixelDungeon.reportException(e);
				MoonshinePixelDungeon.scene().add(new WndError("Quantity is not a number"));
				return;
			}
		}
		if (cmd[3]!=null){
			try {
				item.level(Integer.parseInt(cmd[3]));
			} catch (NumberFormatException e) {
				MoonshinePixelDungeon.reportException(e);
				MoonshinePixelDungeon.scene().add(new WndError("Level is not a number"));
				return;
			}
		}
		if (cmd[4]!=null){
			try {
				item.setTier(Integer.parseInt(cmd[4]));
			} catch (NumberFormatException e) {
				MoonshinePixelDungeon.reportException(e);
				MoonshinePixelDungeon.scene().add(new WndError("Tier is not a number"));
				return;
			}
		}

		item.give();

	}

	public static void descend(String[] cmd){
		try {
			Dungeon.depth = Integer.parseInt(cmd[1]) - 1;
			Buff buff = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
			if (buff != null) buff.detach();

			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				if (mob instanceof DriedRose.GhostHero) mob.destroy();

			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
			Game.switchScene(InterlevelScene.class);
		} catch (NumberFormatException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Depth is not a number"));
		} catch (ArrayIndexOutOfBoundsException e){
			MoonshinePixelDungeon.reportException(e);
			MoonshinePixelDungeon.scene().add(new WndError("Not enough args"));
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return 0;
	}

}
