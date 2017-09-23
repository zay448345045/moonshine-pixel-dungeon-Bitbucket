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
package com.moonshinepixel.moonshinepixeldungeon.levels;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Bomberman;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.watabou.utils.PathFinder;

public class TestLevel extends Level {

	private static final int SIZE = 30;
	
	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_CAVES;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_HALLS;
	}
	
	@Override
	protected boolean build() {
		
		setSize(32, 32);
		PathFinder.setMapSize(32, 32);
		
		for (int i=2; i < SIZE; i++) {
			for (int j=2; j < SIZE; j++) {
				map[i * width() + j] = Terrain.EMPTY;
			}
		}
		
		for (int i=1; i <= SIZE; i++) {
			map[width() + i] =
			map[width() * SIZE + i] =
			map[width() * i + 1] =
			map[width() * i + SIZE] =
				Terrain.WATER;
		}
		int center = (SIZE / 2 + 1) * (width() + 1);
		Painter.fill(this,center%width()-3,center/width()-2,7,5,Terrain.WALL);
		Painter.fill(this,center%width()-2,center/width()-1,5,3,Terrain.EMPTY);
		Painter.fill(this,center%width(),center/width()+2,1,1,Terrain.DOOR);

		entrance = SIZE * width() + SIZE / 2 + 1;
		map[entrance] = Terrain.ENTRANCE;
		
		map[(SIZE / 2 + 1) * (width() + 1)] = Terrain.SIGN;
		
		exit = 0;
		
		return true;
	}

	@Override
	protected void createMobs() {
		Mob mob = new Bomberman();
		mob.pos=(SIZE / 2 + 1) * (width() + 1);
		mobs.add(mob);
	}

	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
	}
	
	@Override
	public int randomRespawnCell() {
		return entrance-width();
	}

}
