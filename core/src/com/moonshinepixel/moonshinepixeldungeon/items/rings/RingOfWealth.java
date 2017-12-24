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
package com.moonshinepixel.moonshinepixeldungeon.items.rings;


import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Gold;
import com.moonshinepixel.moonshinepixeldungeon.items.Honeypot;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class RingOfWealth extends Ring {

	private float triesToDrop = 0;

	@Override
	public RingBuff buff() {
		return new Wealth();
	}

	public static float dropChanceMultiplier( Char target ){
		return (float)Math.pow(1.15, getBonus(target, Wealth.class));
	}

	public static ArrayList<Item> tryRareDrop(Char target, int tries ){
		return tryRareDrop(target.buffs(Wealth.class),tries);
	}
	public static ArrayList<Item> tryRareDrop(int tries,Wealth ...buffs ){
		return tryRareDrop(new HashSet<>(Arrays.asList(buffs)),tries);
	}public static ArrayList<Item> tryRareDrop(RingOfWealth ring, int tries ){
		return tryRareDrop(tries, (Wealth)ring.buff());
	}
	public static ArrayList<Item> tryRareDrop(HashSet<Wealth> buffs, int tries ){
		if (getBonus(buffs) <= 0) return null;

		float triesToDrop = -1;

		//find the largest count (if they aren't synced yet)
		for (Wealth w : buffs){
			if (w.triesToDrop() > triesToDrop){
				triesToDrop = w.triesToDrop();
			}
		}

		//reset (if needed), decrement, and store counts
		if (triesToDrop <= 0) triesToDrop += Random.NormalIntRange(15, 60);
		triesToDrop -= dropProgression( buffs, tries );
		for (Wealth w : buffs){
			w.triesToDrop(triesToDrop);
		}

		//now handle reward logic
		if (triesToDrop <= 0){
			return generateRareDrop();
		} else {
			return null;
		}

	}

	//TODO this is a start, but i'm sure this could be made more interesting...
	private static ArrayList<Item> generateRareDrop(){
		float roll = Random.Float();
		ArrayList<Item> items = new ArrayList<>();
		if (roll < 0.6f){
			switch (Random.Int(3)){
				case 0:
					items.add(new Gold().random());
					break;
				case 1:
					items.add(Generator.random(Generator.Category.POTION));
					break;
				case 2:
					items.add(Generator.random(Generator.Category.SCROLL));
					break;
			}
		} else if (roll < 0.9f){
			switch (Random.Int(3)){
				case 0:
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					items.add(Generator.random(Generator.Category.SEED));
					break;
				case 1:
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					items.add(Generator.random(Random.Int(2) == 0 ? Generator.Category.POTION : Generator.Category.SCROLL ));
					break;
				case 2:
					items.add(Generator.random(Generator.Category.BOMB));
					items.add(new Honeypot());
					break;
			}
		} else {
			Gold g = new Gold();
			g.random();
			g.quantity(g.quantity()*5);
			items.add(g);
		}
		return items;
	}

	//caps at a 50% bonus
	private static float dropProgression( Char target, int tries ){
		return dropProgression(target.buffs(Wealth.class),tries);
	}
	private static float dropProgression( HashSet<Wealth> buffs, int tries ){
		return tries * (float)Math.pow(1.2f, getBonus(buffs) -1 );
	}

	public class Wealth extends RingBuff {

		private void triesToDrop( float val){
			triesToDrop = val;
		}

		private float triesToDrop(){
			return triesToDrop;
		}


	}
}
