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

public class Challenges {

	public static final int NO_FOOD				= 1;
	public static final int NO_ARMOR			= 2;
	public static final int NO_HEALING			= 4;
	public static final int NO_HERBALISM		= 8;
	public static final int SWARM_INTELLIGENCE	= 16;
	public static final int DARKNESS			= 32;
	public static final int NO_SCROLLS		    = 64;
	public static final int AMNESIA				= 128;
	public static final int CURSE				= 256;
	public static final int BLACKJACK			= 512;
	public static final int HORDE				= 1024;
	public static final int COUNTDOWN			= 2048;
	public static final int ARROWHEAD			= 4096;
	public static final int ANALGESIA			= 8192;
	public static final int SPROUT				= 16384;
	public static final int BESTIARY			= 32768;
	public static final int RAPID				= 65536;
	public static final int TRAPS				= 131072;
	public static final int EXTERMINATION		= 262144;
	public static final int LOCKED				= 524288;

	public static final int MAX_VALUE           = 1048576;

	public static final String[] NAME_IDS = {
			"no_food",
			"no_armor",
			"no_healing",
			"no_herbalism",
			"swarm_intelligence",
			"darkness",
			"no_scrolls",
			"amnesia",
			"curse",
			"blackjack",
			"horde",
			"countdown",
			"arrowhead",
			"analgesia",
			"sprout",
			"bestiary",
			"rapid",
			"traps",
			"extermination",
			"roomlock"
	};

	public static final float[] SCORE_MODIFIERS = {
			1.1f,
			1.15f,
			1.2f,
			1.05f,
			1.05f,
			1.07f,
			1.1f,
			1.1f,
			1.2f,
			1.1f,
			1.2f,
			1.1f,
			1.2f,
			1.2f,
			1.2f,
			1.2f,
			1.2f,
			1.1f,
			1.15f,
			1.15f
	};
	public static final int[] MOONSTONES = {
			2,
			3,
			4,
			1,
			2,
			1,
			3,
			2,
			3,
			2,
			4,
			4,
			4,
			4,
			3,
			4,
			3,
			3,
			3,
			3
	};

	public static final int[] MASKS = {
			NO_FOOD, NO_ARMOR, NO_HEALING, NO_HERBALISM, SWARM_INTELLIGENCE, DARKNESS, NO_SCROLLS, AMNESIA, CURSE, BLACKJACK, HORDE, COUNTDOWN, ARROWHEAD, ANALGESIA, SPROUT, BESTIARY, RAPID, TRAPS, EXTERMINATION, LOCKED
	};

	public static float score(int challenges){
		float mod = 1;
		for (int i = 0; i < MASKS.length; i++) {
			if ((challenges & MASKS[i]) != 0) {
				mod *= SCORE_MODIFIERS[i];
			}
		}
		return mod;
	}
	public static int moonstones(int challenges){
		int mod = 0;
		for (int i = 0; i < MASKS.length; i++) {
			if ((challenges & MASKS[i]) != 0) {
				mod += MOONSTONES[i];
			}
		}
		return mod;
	}

	public static float hiveMobsMod(){
		return (Statistics.amuletObtained?3f:2f)*(Dungeon.isChallenged(SPROUT)?2:1);
	}
	public static float noHiveMobsMod(){
		return (Statistics.amuletObtained?2f:1.5f)*(Dungeon.isChallenged(SPROUT)?2:1);
	}

	public static float countdownTurns(){
		return (Statistics.amuletObtained?300f:400f)*(Dungeon.isChallenged(SPROUT)?4:1);
	}

}