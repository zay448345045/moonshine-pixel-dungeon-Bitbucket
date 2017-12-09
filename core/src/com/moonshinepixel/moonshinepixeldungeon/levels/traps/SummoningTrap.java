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
package com.moonshinepixel.moonshinepixeldungeon.levels.traps;

import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Bestiary;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SummoningTrap extends Trap {

	private static final float DELAY = 2f;

	{
		color = TEAL;
		shape = WAVES;
	}

	@Override
	public void activate() {

		if (Dungeon.bossLevel()) {
			return;
		}

		int nMobs = 1;
		if (Random.Int( 2 ) == 0) {
			nMobs++;
			if (Random.Int( 2 ) == 0) {
				nMobs++;
			}
		}

		if (Dungeon.isChallenged(Challenges.HORDE))nMobs*=Challenges.hiveMobsMod();

		ArrayList<Integer> candidates = new ArrayList<>();

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar( p ) == null && (Level.getPassable(p) || Level.getAvoid(p))) {
				candidates.add( p );
			}
		}

		ArrayList<Integer> respawnPoints = new ArrayList<>();
		while (nMobs > 0 && candidates.size() > 0) {
			int index = Random.index( candidates );

			respawnPoints.add( candidates.remove( index ) );
			nMobs--;
		}
		int tries=100;
		while(nMobs>0&&tries>0){
			tries--;
			int c = Dungeon.level.randomRespawnCell(false);
			if (respawnPoints.contains(c)) continue;
			respawnPoints.add(c);
			nMobs--;
		}

		ArrayList<Mob> mobs = new ArrayList<>();

		for (Integer point : respawnPoints) {
			try {
				Mob mob = Bestiary.mob(Dungeon.depth);
				mob.state = mob.WANDERING;
				mob.pos = point;
				GameScene.add(mob, DELAY);
				mobs.add(mob);
			} catch (Exception e){}
		}

		//important to process the visuals and pressing of cells last, so spawned mobs have a chance to occupy cells first
		for (Mob mob : mobs){
			ScrollOfTeleportation.appear(mob, mob.pos);
		}

	}
}
