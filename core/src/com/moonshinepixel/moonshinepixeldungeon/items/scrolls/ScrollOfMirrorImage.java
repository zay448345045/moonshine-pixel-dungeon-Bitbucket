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
package com.moonshinepixel.moonshinepixeldungeon.items.scrolls;

import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.MirrorImage;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Invisibility;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfMirrorImage extends Scroll {

	{
		initials = 4;
	}

	private static final int NIMAGES	= 3;
	
	@Override
	protected void doRead() {
		
		ArrayList<Integer> respawnPoints = new ArrayList<Integer>();
		
		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = Item.curUser.pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar( p ) == null && (Level.getPassable(p) || Level.getAvoid(p))) {
				respawnPoints.add( p );
			}
		}
		
		int nImages = NIMAGES;
		while (nImages > 0 && respawnPoints.size() > 0) {
			int index = Random.index( respawnPoints );
			
			MirrorImage mob = new MirrorImage();
			mob.duplicate( Item.curUser );
			GameScene.add( mob );
			ScrollOfTeleportation.appear( mob, respawnPoints.get( index ) );
			
			respawnPoints.remove( index );
			nImages--;
		}
		
		if (nImages < NIMAGES) {
			setKnown();
		}
		
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();

		readAnimation();
	}

	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return isKnown() ? 30 * quantity : super.price();
	}
}
