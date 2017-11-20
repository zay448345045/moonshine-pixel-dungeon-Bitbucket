/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2017 Evan Debenham
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

package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard;

import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.DevMarkerBlob;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.Armor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.Artifact;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.Wand;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.MissileWeapon;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.HashSet;

public class PitRoom extends StandardRoom {

	private int platform = Terrain.HIGH_GRASS;
	private int wall = Terrain.CHASM;

	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 10);
	}

	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 10);
	}

	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 0, 1};
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1 , platform );

		int minDim = Math.min(width(), height());
		int maxsize = minDim;
		int size = minDim;

		while (size>=3) {
			Rect pit = new Rect(this);
			pit = pit.shrink(maxsize-size+2);
			pit = new Rect(pit.left,pit.top,pit.right+1,pit.bottom+1);
			Rect floor = new Rect(this);
			floor = floor.shrink(maxsize-size+3);
			floor = new Rect(floor.left,floor.top,floor.right+1,floor.bottom+1);
			try {
				Painter.fill(level, pit, wall);
				Painter.fill(level, floor, platform);
			} catch (Exception e){

			}
			HashSet<Point> cells = new HashSet<>();
			for (int i = pit.left; i<pit.right; i++){
				for (int j = pit.top;j<pit.bottom;j++){
					if (!(floor.inside(new Point(i,j)))){
						if(!((i==pit.left && j==pit.top)||(i==pit.right-1 && j==pit.top)||(i==pit.left && j==pit.bottom-1)||(i==pit.right-1 && j==pit.bottom-1))) {
//							Blob.seed(i+j*level.width(),1, DevMarkerBlob.class,level);
							cells.add(new Point(i, j));
						}
					}
				}
			}
			//System.out.println("");
			try {
				do {
					Point targ = Random.element(cells);
					Painter.set(level, targ, platform);
					//System.out.println(targ.x+"|"+targ.y);
				} while (Random.Int(100) > 50);
			} catch (NullPointerException e){
				//System.out.println("fail");
			}

			size-=2;
		}
//		Painter.fill(level, left+2,center().y,width()-4,1,Terrain.EMPTY_SP);
//		Painter.fill(level, center().x, top+2,1,height()-4,Terrain.EMPTY_SP);

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		Point center = center();
		Painter.set( level, center, Terrain.PEDESTAL );
		Item prize;
		prize = level.findPrizeItem(Artifact.class);
		if (prize == null && Random.Int(100)<10) prize = Generator.random(Generator.Category.ARTIFACT);

		if (prize == null || prize instanceof Ring || prize instanceof MissileWeapon) {
			if (prize!=null) level.addItemToSpawn(prize);
			prize = Random.oneOf(level.findPrizeItem(Weapon.class), level.findPrizeItem(Armor.class), level.findPrizeItem(Wand.class), level.findPrizeItem(Ring.class));
		}
		if (prize!=null) {
			if (!(prize instanceof MissileWeapon)){
				level.drop(prize, (center.x + center.y * level.width()));
			} else {
				level.drop(Generator.random( Random.oneOf(
						Generator.Category.WEAPON,
						Generator.Category.ARMOR,
						Generator.Category.WAND,
						Generator.Category.GUN,
						Generator.Category.RING)), (center.x + center.y * level.width()));
			}
		} else {
			level.drop(Generator.random( Random.oneOf(
					Generator.Category.WEAPON,
					Generator.Category.ARMOR,
					Generator.Category.WAND,
					Generator.Category.GUN,
					Generator.Category.RING)), (center.x + center.y * level.width()));
		}
	}
}
