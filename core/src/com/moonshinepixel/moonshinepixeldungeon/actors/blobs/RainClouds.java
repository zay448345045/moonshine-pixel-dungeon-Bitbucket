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
package com.moonshinepixel.moonshinepixeldungeon.actors.blobs;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.BlobEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Lightning;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.items.Dewdrop;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.HighGrass;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class RainClouds extends Blob implements Hero.Doom {

	@Override
	protected void evolve() {
		super.evolve();

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				int cell = i + j * Dungeon.level.width();
				if (cur[cell]>0) {
                    if (Dungeon.level.map[cell] == Terrain.EMPTY || Dungeon.level.map[cell] == Terrain.EMPTY_SP || Dungeon.level.map[cell] == Terrain.GRASS || Dungeon.level.map[cell] == Terrain.HIGH_GRASS || Dungeon.level.map[cell] == Terrain.EMPTY_DECO || Dungeon.level.map[cell] == Terrain.EMBERS) {
                        if (Random.Int(2) == 0) {
                            if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS)
                                HighGrass.trample(Dungeon.level, cell, null);
                            Dungeon.level.map[cell] = Terrain.WATER;
                            Level.water[cell] = true;
                            Level.flamable[cell] = false;
                            GameScene.updateMap(cell);
                        }
                    }
                    if (Random.Int(2)==0) new Dewdrop().drop(cell).sprite.drop();
                }
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.pour( Speck.factory( Speck.RAIN ), 0.4f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

    @Override
    public void onDeath() {
        Dungeon.fail( getClass() );
        GLog.n( Messages.get(this, "ondeath") );
    }
}
