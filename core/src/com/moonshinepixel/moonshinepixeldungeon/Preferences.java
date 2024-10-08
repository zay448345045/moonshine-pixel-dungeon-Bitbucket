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
package com.moonshinepixel.moonshinepixeldungeon;

import com.badlogic.gdx.Gdx;
import com.watabou.utils.GameMath;

public enum Preferences {

	INSTANCE;

	public static final String KEY_LANDSCAPE	= "landscape";
	public static final String KEY_IMMERSIVE	= "immersive";
	public static final String KEY_POWER_SAVER 	= "power_saver";
	public static final String KEY_SCALE		= "scale";
	public static final String KEY_MUSIC		= "music";
	public static final String KEY_MUSIC_VOL    = "music_vol";
	public static final String KEY_SOUND_FX		= "soundfx";
	public static final String KEY_SFX_VOL      = "sfx_vol";
	public static final String KEY_ZOOM			= "zoom";
	public static final String KEY_LAST_CLASS	= "last_class";
	public static final String KEY_LAST_SLOT	= "last_slot";
	public static final String KEY_CHALLENGES	= "challenges";
	public static final String KEY_QUICKSLOTS	= "quickslots";
	public static final String KEY_FLIPTOOLBAR	= "flipped_ui";
	public static final String KEY_FLIPTAGS 	= "flip_tags";
	public static final String KEY_BARMODE		= "toolbar_mode";
	public static final String KEY_LANG         = "language";
	public static final String KEY_CLASSICFONT	= "classic_font";
	public static final String KEY_INTRO		= "intro";
	public static final String KEY_BRIGHTNESS	= "brightness";
	public static final String KEY_GRID 	    = "visual_grid";
	public static final String KEY_VERSION      = "version";
	public static final String KEY_LASTGENDER   = "lastgender";
	public static final String KEY_DEVOPTIONS   = "devoptions";
	public static final String KEY_STORYLINE   	= "storyline";
	public static final String KEY_NAME   		= "heroname";
	public static final String KEY_ORIENTATION	= "orient";
	public static final String KEY_UPDATECHECK	= "updatechecker";
	public static final String KEY_UPDATECVIS	= "ipdatevisible";
	public static final String KEY_INFOSHOWN	= "infoshown";
	public static final String KEY_STARTINGAME	= "startingame";
	public static final String KEY_HUDTYPE		= "hudtype";
	public static final String KEY_BUTTONTYPE	= "buttontype";
	public static final String KEY_UNLOCKS		= "unlocks";
	public static final String KEY_MOONSTONES	= "moonstones";
	public static final String KEY_SEED			= "seed";
	public static final String KEY_CUSTOMSEED	= "custseed";
	public static final String KEY_DYNASTY		= "dynasty";

	public static final String KEY_WINDOW_FULLSCREEN	= "windowFullscreen";
	public static final String KEY_WINDOW_WIDTH			= "windowWidth";
	public static final String KEY_WINDOW_HEIGHT		= "windowHeight";

	public static final int DEFAULT_WINDOW_WIDTH = 480;
	public static final int DEFAULT_WINDOW_HEIGHT = 800;

	public static final String FILE_NAME = "pd-prefs";

	private com.badlogic.gdx.Preferences prefs;
	
	private com.badlogic.gdx.Preferences get() {
		if (prefs == null) {
			prefs = Gdx.app.getPreferences(FILE_NAME);
		}
		return prefs;
	}

	boolean contains( String key ){
		return get().contains( key );
	}

	public void remove( String key ){
		get().remove(key);
	}

	public int getInt( String key, int defValue ) {
		return getInt(key, defValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public int getInt( String key, int defValue, int min, int max ) {
		try {
			int i = get().getInteger(key, defValue);
			if (i < min || i > max){
				int val = (int) GameMath.gate(min, i, max);
				put(key, val);
				return val;
			} else {
				return i;
			}
		} catch (ClassCastException | NumberFormatException e) {
			MoonshinePixelDungeon.reportException(e);
			put(key, defValue);
			return defValue;
		}
	}
	
	public boolean getBoolean( String key, boolean defValue ) {
		try {
			return get().getBoolean(key, defValue);
		} catch (ClassCastException | NumberFormatException e) {
			MoonshinePixelDungeon.reportException(e);
			put(key, defValue);
			return defValue;
		}
	}

	public String getString( String key, String defValue ) {
		return getString(key, defValue, Integer.MAX_VALUE);
	}

	public String getString( String key, String defValue, int maxLength ) {
		try {
			String s = get().getString( key, defValue );
			if (s != null && s.length() > maxLength) {
				put(key, defValue);
				return defValue;
			} else {
				return s;
			}
		} catch (ClassCastException | NumberFormatException e) {
			MoonshinePixelDungeon.reportException(e);
			put(key, defValue);
			return defValue;
		}
	}
	
	public void put( String key, int value ) {
		get().putInteger(key, value).flush();
	}
	
	public void put( String key, boolean value ) {
		get().putBoolean( key, value ).flush();
	}
	
	public void put( String key, String value ) {
		get().putString(key, value).flush();
	}
}
