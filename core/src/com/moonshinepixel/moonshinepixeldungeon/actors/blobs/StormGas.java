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
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.BlobEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Lightning;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class StormGas extends Blob implements Hero.Doom {

	@Override
	protected void evolve() {
		super.evolve();

		int levelDamage = 5 + Dungeon.fakedepth[Dungeon.depth] * 5;

		Char ch;
		Char ch2;
		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0 && Random.Int(100)<5) {
				    int targ = cell;
					HashSet<Integer> cells = new HashSet();
					for (int c : PathFinder.NEIGHBOURS25){
					    int t = cell+c;
					    if (t>=0 && t<Dungeon.level.length()) {
                            if (cur[t] > 0) {
                                cells.add(t);
                            }
                        }
                    }
                    Object[] dumm = cells.toArray();
                    Integer[] targs = Arrays.copyOf(dumm,dumm.length,Integer[].class);
                    System.out.println(targs.length);
                    if (targs.length>0){
                        targ=Random.element(targs);
                    } else {
                        targ=cell;
                    }
//                    do {
//                        targ = PathFinder.NEIGHBOURS25[Random.Int(PathFinder.NEIGHBOURS25.length)] + cell;
//                    } while (cur[targ]<=0 || targ<0 || targ> Dungeon.level.width());

                    Ballistica bolt = new Ballistica(cell,targ,Ballistica.STOP_TARGET);
                    for(int bCell : bolt.path){
                        if (cur[bCell]>0) {
                            ch = Actor.findChar(bCell);
                            if (ch != null) {
                                int dmg = Math.max(1, (Random.NormalIntRange((int) (levelDamage * 0.5f), (levelDamage)))) / 2 + 1;
//                            ch.damage( dmg, LightningTrap.LIGHTNING );
                                ch.damage(dmg, this);
                            }
                        }
                    }

                    ArrayList<Lightning.Arc> arcs = new ArrayList<>();
                    arcs.add(new Lightning.Arc(cell,targ));
                    if (Dungeon.visible[cell] || Dungeon.visible[targ]) {
                        Emitter emmiter = new CellEmitter().get(cell);

                        emmiter.parent.addToFront(new Lightning(arcs, null));

                    }
				}
			}
		}
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.pour( Speck.factory( Speck.STORM ), 0.4f );
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
