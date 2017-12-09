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

import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Piranha;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.Armor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.Artifact;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Food;
import com.moonshinepixel.moonshinepixeldungeon.items.food.MysteryMeat;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfLiquidFlame;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.Wand;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.MissileWeapon;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.watabou.utils.*;

import java.util.HashSet;

public class WoodCircleRoom extends StandardRoom {

	private int passageWidth = 0;

	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 11);
	}

	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 11);
	}

	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 1, 0};
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1 , Terrain.EMPTY );

		int minDim = Math.min(width(), height());
		passageWidth = (int)Math.floor(0.25f*(minDim+1));
		Painter.fill(level, this, passageWidth+1, Terrain.WALL);
		Painter.fill(level, this, passageWidth+2, Terrain.WATER);

		Item itm = level.findPrizeItem();
		if (itm!=null) level.drop(itm,level.pointToCell(center()));
		else
		level.drop(Generator.random(Random.oneOf(
				Generator.Category.SCROLL,
				Generator.Category.POTION
				)), level.pointToCell(center()));


		level.addItemToSpawn(Random.oneOf(new PotionOfLiquidFlame(), new Bomb(), new Bomb()));

		HashSet<Point> targs = new HashSet<>();
		for (Point p:getPoints()){
			if (level.map[level.pointToCell(p)]==Terrain.WALL && inside(p)){
				targs.add(p);
			}
		}
		Painter.set(level,Random.element(targs),Terrain.BOOKSHELF);

		for(Door d: connected.values()){
			d.type= Door.Type.REGULAR;
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("psw", passageWidth);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		passageWidth=bundle.getInt("psw");
	}

	@Override
	public boolean canPlaceMob(Point p) {
		Rect pool = new Rect(left+passageWidth+2,top+passageWidth+2,right-passageWidth+2,bottom-passageWidth+2);
		return !pool.inside(p)&&inside(p);
	}
}
