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

import java.util.Arrays;

public class CorridorLevel extends Level {
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
		
		setSize(5, 10);
		Arrays.fill(map,Terrain.WALL);
		for (int i = 0; i<length();i++){
		    if (i%width()==1||i%width()==2||i%width()==3){
		        if (i>width()&&i<length()-width()){
		            map[i]=Terrain.EMPTY;
                }
            }
        }
        map[(int)(length-width()-Math.floor(width()/2))]=Terrain.ENTRANCE;
        entrance = (int)(length-width()-Math.floor(width()/2));
        map[(int)(width()+Math.ceil(width()/2))]=Terrain.EXIT;
        exit = (int)(width()+Math.ceil(width()/2));
		return true;
	}

	@Override
	protected void createMobs() {
	}

	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
	}
	
	@Override
	public int randomRespawnCell(boolean notvisible) {
		return entrance;
	}

}
