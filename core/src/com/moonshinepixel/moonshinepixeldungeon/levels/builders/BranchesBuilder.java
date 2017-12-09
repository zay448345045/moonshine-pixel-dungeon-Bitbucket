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

package com.moonshinepixel.moonshinepixeldungeon.levels.builders;

import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

//A builder that creates only branches, very simple and very random
public class BranchesBuilder extends RegularBuilder {
	
	@Override
	public ArrayList<Room> build(ArrayList<Room> rooms) {
		
		setupRooms( rooms );
		
		if (entrance == null){
			return null;
		}
		
		ArrayList<Room> branchable = new ArrayList<>();
		
		entrance.setSize();
		entrance.setPos(0, 0);
		branchable.add(entrance);

		float a = Random.Float(360f);

		if (shop != null) {
			float angle;
			int tries = 10;
			do {
				angle = placeRoom(branchable, entrance, shop, a+Random.Float(90f));
				tries--;
			} while (angle == -1 && tries >= 0);
			if (angle == -1) return null;
		}

		if (bjshop != null) {
			a=360-a;
			float angle;
			int tries = 10;
			do {
				angle = placeRoom(branchable, entrance, bjshop, a+Random.Float(90f));
				tries--;
			} while (angle == -1 && tries >= 0);
			if (angle == -1) return null;
		}
		
		ArrayList<Room> roomsToBranch = new ArrayList<>();
		roomsToBranch.addAll(multiConnections);
		if (exit != null) roomsToBranch.add(exit);
		roomsToBranch.addAll(singleConnections);
		if (!createBranches(rooms, branchable, roomsToBranch, branchTunnelChances)){
			return null;
		}
		
		findNeighbours(rooms);
		
		for (Room r : rooms){
			for (Room n : r.neigbours){
				if (!n.connected.containsKey(r)
						&& Random.Float() < extraConnectionChance){
					r.connect(n);
				}
			}
		}
		
		return rooms;
	}
}
