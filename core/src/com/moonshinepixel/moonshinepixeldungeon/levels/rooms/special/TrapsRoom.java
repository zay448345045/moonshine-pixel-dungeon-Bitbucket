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
package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special;

import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.*;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfLevitation;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.MissileWeapon;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class TrapsRoom extends SpecialRoom {

	public void paint( Level level ) {

		//System.out.println("Traps generated!");
		 
		Painter.fill( level, this, Terrain.WALL );

		Class<? extends Trap> trapClass;
		switch (Random.Int(5)){
			case 0: default:
				trapClass = SpearTrap.class;
				break;
			case 1:
				trapClass = !Dungeon.bossLevel(Dungeon.depth + 1)? null : SummoningTrap.class;
				break;
			case 2: case 3: case 4:
				trapClass = Random.oneOf(levelTraps[Dungeon.depth/5]);
				break;
		}

		if (trapClass == null){
			Painter.fill(level, this, 1, Terrain.CHASM);
		} else {
			Painter.fill(level, this, 1, Terrain.TRAP);
		}
		
		Door door = entrance();
		door.set( Door.Type.REGULAR );
		
		int lastRow = level.map[left + 1 + (top + 1) * level.width()] == Terrain.CHASM ? Terrain.CHASM : Terrain.EMPTY;

		int x = -1;
		int y = -1;
		if (door.x == left) {
			x = right - 1;
			y = top + height() / 2;
			Painter.fill( level, x, top + 1, 1, height() - 2 , lastRow );
		} else if (door.x == right) {
			x = left + 1;
			y = top + height() / 2;
			Painter.fill( level, x, top + 1, 1, height() - 2 , lastRow );
		} else if (door.y == top) {
			x = left + width() / 2;
			y = bottom - 1;
			Painter.fill( level, left + 1, y, width() - 2, 1 , lastRow );
		} else if (door.y == bottom) {
			x = left + width() / 2;
			y = top + 1;
			Painter.fill( level, left + 1, y, width() - 2, 1 , lastRow );
		}

		for(Point p : getPoints()) {
			int cell = level.pointToCell(p);
			if (level.map[cell] == Terrain.TRAP){
				try {
					level.setTrap(((Trap) trapClass.newInstance()).reveal(), cell);
				} catch (Exception e) {
					MoonshinePixelDungeon.reportException(e);
				}
			}
		}
		
		int pos = x + y * level.width();
		if (Random.Int( 3 ) == 0) {
			if (lastRow == Terrain.CHASM) {
				Painter.set( level, pos, Terrain.EMPTY );
			}
			level.drop( prize( level ), pos ).type = Heap.Type.CHEST;
		} else {
			Painter.set( level, pos, Terrain.PEDESTAL );
			level.drop( prize( level ), pos );
		}
		
		level.addItemToSpawn( new PotionOfLevitation() );
	}
	
	private static Item prize(Level level ) {

		Item prize;

		if (Random.Int(3) != 0){
			prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}
		
		//1 floor set higher in probability, never cursed
		if (Random.Int(2) == 0) {
			prize = Generator.randomWeapon((Dungeon.depth / 5) + 1);
		} else {
			prize = Generator.randomArmor((Dungeon.depth / 5) + 1);
		}
		if (!Dungeon.isChallenged(Challenges.CURSE)){
			prize.cursed=false;
		}
		boolean curse = prize.cursed;

		//33% chance for an extra update.
		if (!(prize instanceof MissileWeapon) && Random.Int(3) == 0){
			prize.upgrade();
		}
		if (curse){
			prize.cursed=true;
		}
		prize.invAct();
		return prize;
	}

	@SuppressWarnings("unchecked")
	private static Class<?extends Trap>[][] levelTraps = new Class[][]{
			//sewers
			{ToxicTrap.class, TeleportationTrap.class, FlockTrap.class},
			//prison
			{ConfusionTrap.class, ExplosiveTrap.class, ParalyticTrap.class},
			//caves
			{BlazingTrap.class, VenomTrap.class, ExplosiveTrap.class},
			//city
			{SummoningTrap.class, VenomTrap.class, DisintegrationTrap.class, DarkVenomTrap.class},
			//halls, muahahahaha
			{GrimTrap.class, DarkVenomTrap.class},
			//special levels (26-30)
            {WornTrap.class},
            //garden
            {ToxicTrap.class, TeleportationTrap.class, FlockTrap.class}
	};
}
