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
import com.moonshinepixel.moonshinepixeldungeon.effects.BlobEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.EverFlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.FlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;

public class EverlastingFire extends Blob {


	{
		actPriority = 3;
	}

	protected int pos;

	@Override
	protected void evolve() {

		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j* Dungeon.level.width();
				off[cell] = cur[cell];

				if (off[cell] > 0) {
					volume += off[cell];
				}
				if (cur[cell]>0){
					Fire.burn(cell);
					if (Actor.findChar(cell)!=null||Dungeon.level.heaps.get(cell)!=null||Level.flamable[cell]){
						Blob.seed(cell,2,Fire.class);
					}
				}
			}
		}

	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.start( EverFlameParticle.FACTORY, 0.03f, 0 );
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void explode(int cell) {
		Heap heap = Dungeon.level.heaps.get(cell);
		if (heap!=null) {
			if (heap.type == Heap.Type.FOR_SALE) {
				CellEmitter.get(heap.pos).burst(FlameParticle.FACTORY, 4);
				Blob.seed(cell,6,Fire.class);
			}
		}
		super.explode(cell);
	}
}

