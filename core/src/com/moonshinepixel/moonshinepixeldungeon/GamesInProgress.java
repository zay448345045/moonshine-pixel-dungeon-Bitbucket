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

import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.watabou.utils.Bundle;

import java.io.IOException;
import java.util.HashMap;

public class GamesInProgress {

	private static HashMap<Integer, Info> state = new HashMap<>();
	private static HashMap<HeroClass, Info> state_OLD = new HashMap<>();

	public static Info check( int gameslot ) {

		if (state.containsKey( gameslot )) {

			return state.get( gameslot );

		} else {

			Info info;
			try {

				Bundle bundle = Dungeon.gameBundle( Dungeon.gameFile( gameslot ) );
				info = new Info();
				Dungeon.preview( info, bundle );

				if (info.version < MoonshinePixelDungeon.v0_0_0){
					info = null;
				}

			} catch (IOException e) {
				info = null;
			}

			state.put( gameslot, info );
			return info;

		}
	}
	public static Info check_OLD( HeroClass cl ) {

		if (state_OLD.containsKey( cl )) {

			return state_OLD.get( cl );

		} else {

			Info info;
			try {

				Bundle bundle = Dungeon.gameBundle( Dungeon.gameFile_OLD( cl ) );
				info = new Info();
				Dungeon.preview( info, bundle );

				if (info.version < MoonshinePixelDungeon.v0_0_0){
					info = null;
				}

			} catch (IOException e) {
				info = null;
			}

			state_OLD.put( cl, info );
			return info;

		}
	}

	public static void set( int gameSlot, HeroClass hc, int depth, int level, boolean challenges ) {
		Info info = new Info();
		info.depth = depth;
		info.level = level;
		info.challenges = challenges;
		info.heroClass = hc;
		state.put( gameSlot, info );
	}

	public static void set_OLD( HeroClass cl, int depth, int level, boolean challenges ) {
		Info info = new Info();
		info.depth = depth;
		info.level = level;
		info.challenges = challenges;
		state_OLD.put( cl, info );
	}
	
	public static void setUnknown( HeroClass cl ) {
		state_OLD.remove( cl );
	}
	
	public static void delete( int saveSlot ) {
		state.put( saveSlot, null );
	}
	public static void delete_OLD( HeroClass cl ) {
		state_OLD.put( cl, null );
	}
	
	public static class Info {
		public int depth;
		public int level;
		public int version;
		public boolean challenges;
		public HeroClass heroClass;
	}
}
