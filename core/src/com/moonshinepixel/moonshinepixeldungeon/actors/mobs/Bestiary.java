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
package com.moonshinepixel.moonshinepixeldungeon.actors.mobs;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.watabou.utils.Random;

public class Bestiary {

	public static Mob mob( int depth ) {
		@SuppressWarnings("unchecked")
		Class<? extends Mob> cl = (Class<? extends Mob>)mobClass( depth );
		try {
			return ClassReflection.newInstance(cl);
		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}
	
	public static Mob mutable( int depth ) {
		@SuppressWarnings("unchecked")
		Class<? extends Mob> cl = (Class<? extends Mob>)mobClass( depth );
		
		if (Random.Int(Dungeon.isChallenged(Challenges.BESTIARY)?2:30) == 0) {
			if (cl == Rat.class) {
				cl = Albino.class;
			} else if (cl == Thief.class) {
				cl = Bandit.class;
			} else if (cl == Brute.class) {
				cl = Shielded.class;
			} else if (cl == Monk.class) {
				cl = Senior.class;
			} else if (cl == Scorpio.class) {
				cl = Acidic.class;
			} else if (cl == Slime.class) {
				cl = Caustic.class;
			}
		}
		if (Random.Int(Dungeon.isChallenged(Challenges.BESTIARY)?4:10)==0){
			if (cl==Gnoll.class){
				cl=Bomberman.class;
			}
		}
		
		try {
			return ClassReflection.newInstance(cl);
		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}
	
	private static Class<?> mobClass( int depth ) {
		
		float[] chances;
		Class<?>[] classes;
		
		switch (depth) {
		case 1:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Rat.class };
			break;
		case 2:
			chances = new float[]{ 1, 1 };
			classes = new Class<?>[]{ Rat.class, Gnoll.class };
			break;
		case 3:
			chances = new float[]{ 2, 4, 1, 1 };
			classes = new Class<?>[]{ Rat.class, Gnoll.class, Crab.class, Swarm.class };
			break;
		case 4:
			chances = new float[]{ 1, 2, 3, 1,   0.01f, 0.01f };
			classes = new Class<?>[]{ Rat.class, Gnoll.class, Crab.class, Swarm.class,    Skeleton.class, Thief.class };
			break;
			
		case 5:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Goo.class };
			break;
			
		case 6:
			chances = new float[]{ 3, 1, 1,     0.2f };
			classes = new Class<?>[]{ Skeleton.class, Thief.class, Swarm.class,   Shaman.class };
			break;
		case 7:
			chances = new float[]{ 3, 1, 1, 1 };
			classes = new Class<?>[]{ Skeleton.class, Shaman.class, Thief.class, Guard.class };
			break;
		case 8:
			chances = new float[]{ 3, 2, 2, 1,   0.02f };
			classes = new Class<?>[]{ Skeleton.class, Shaman.class, Guard.class, Thief.class,   Bat.class };
			break;
		case 9:
			chances = new float[]{ 3, 3, 2, 1,   0.02f, 0.01f };
			classes = new Class<?>[]{ Skeleton.class, Guard.class, Shaman.class, Thief.class,   Bat.class, Brute.class };
			break;
			
		case 10:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Tengu.class };
			break;
			
		case 11:
			chances = new float[]{ 1,   0.2f };
			classes = new Class<?>[]{ Bat.class,   Brute.class };
			break;
		case 12:
			chances = new float[]{ 1, 1,   0.2f };
			classes = new Class<?>[]{ Bat.class, Brute.class,   Spinner.class };
			break;
		case 13:
			chances = new float[]{ 1, 3, 1, 1,   0.02f };
			classes = new Class<?>[]{ Bat.class, Brute.class, Shaman.class, Spinner.class,    Elemental.class };
			break;
		case 14:
			chances = new float[]{ 1, 3, 1, 4,    0.02f, 0.01f };
			classes = new Class<?>[]{ Bat.class, Brute.class, Shaman.class, Spinner.class,    Elemental.class, Monk.class };
			break;
			
		case 15:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ DM300.class };
			break;
			
		case 16:
			chances = new float[]{ 1, 1,   0.2f };
			classes = new Class<?>[]{ Elemental.class, Warlock.class,    Monk.class };
			break;
		case 17:
			chances = new float[]{ 1, 1, 1 };
			classes = new Class<?>[]{ Elemental.class, Monk.class, Warlock.class };
			break;
		case 18:
			chances = new float[]{ 1, 2, 1, 1 };
			classes = new Class<?>[]{ Elemental.class, Monk.class, Golem.class, Warlock.class };
			break;
		case 19:
			chances = new float[]{ 1, 2, 3, 1,    0.02f };
			classes = new Class<?>[]{ Elemental.class, Monk.class, Golem.class, Warlock.class,    Succubus.class };
			break;
			
		case 20:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ King.class };
			break;
			
		case 22:
			chances = new float[]{ 1, 1 };
			classes = new Class<?>[]{ Succubus.class, Eye.class };
			break;
		case 23:
			chances = new float[]{ 1, 2, 1 };
			classes = new Class<?>[]{ Succubus.class, Eye.class, Scorpio.class };
			break;
		case 24:
			chances = new float[]{ 1, 2, 3 };
			classes = new Class<?>[]{ Succubus.class, Eye.class, Scorpio.class };
			break;
			
		case 25:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Yog.class };
			break;

		case 27:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Rat.class };
			break;

		case 31:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Slime.class };
			break;
		case 32:
			chances = new float[]{ 2, 1 };
			classes = new Class<?>[]{ Slime.class, Snake.class };
			break;
		case 33:
			chances = new float[]{ 2, 3 };
			classes = new Class<?>[]{ Slime.class, Snake.class };
			break;
		case 34:
			chances = new float[]{ 2, 3 };
			classes = new Class<?>[]{ Slime.class, Snake.class };
			break;
		case 35:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ StoneSnake.class };
			break;
			
		default:
			chances = new float[]{ 1 };
			classes = new Class<?>[]{ Eye.class };
		}
		
		return classes[ Random.chances( chances )];
	}
}
