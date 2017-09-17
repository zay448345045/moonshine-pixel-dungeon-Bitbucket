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

import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Bleeding;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.effects.BlobEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.BlastParticle;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class ExplosiveGas extends Blob {

    @Override
    public void seed(Level level, int cell, int amount) {
        super.seed(level, cell, amount);
        Dungeon.level.flamable[cell]=true;
    }

    @Override
	protected void evolve() {
		super.evolve();
		Char ch;
		int cell;

		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				/*if (cur[cell] > 0 && (ch = Actor.findChar(cell)) != null) {
					if (!ch.immunities().contains(this.getClass()))
						GameScene.add(Blob.seed(cell, 2, Fire.class));
				}*/
				if (cur[cell]>0) {
				    Dungeon.level.flamable[cell]=true;
                    for (int n : PathFinder.NEIGHBOURS9){
                        int targ = n+cell;
                        if (volumeAt(targ, Fire.class) > 0) {
                            explode(cell);
                        }
                    }
				}
			}
		}
		for (int i = 0;i<Dungeon.level.length();i++){
            if ((Terrain.flags[Dungeon.level.map[i]]&Terrain.FLAMABLE)==0 && volumeAt(i, ExplosiveGas.class) <= 0) Dungeon.level.flamable[i]=false;
        }
	}
	@Override
	public void explode(int cell){
        Char ch;
        if (cur[cell]!=0) {
            if ((Terrain.flags[Dungeon.level.map[cell]]&Terrain.FLAMABLE)==0) Dungeon.level.flamable[cell]=false;
            if (Dungeon.level.map[cell]== Terrain.WATER) {
                if (Actor.findChar(cell)!=null) {
                    GameScene.add(seed(cell, 2, Fire.class));
                }
            } else {
                GameScene.add(seed(cell, 4, Fire.class));
            }
            if (Dungeon.visible[cell]) {
                CellEmitter.center(cell).burst(BlastParticle.FACTORY, 10);
            }
//            volume-=cur[cell];
//            cur[cell] = 0;
            if ((ch = Actor.findChar( cell )) != null){
                Buff.affect(ch, Bleeding.class).set(Random.NormalIntRange(2,5));
            }
//            for (int n : PathFinder.NEIGHBOURS8){
//                int targ = cell+n;
//                explode(targ);
//            }
        }
        super.explode(cell);
    }
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		
		emitter.pour( Speck.factory( Speck.FLAMABLE ), 0.4f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
