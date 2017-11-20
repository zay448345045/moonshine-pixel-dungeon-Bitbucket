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


import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.triggers.DummyTrigger;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.triggers.Trigger;
import com.moonshinepixel.moonshinepixeldungeon.effects.BlobEmitter;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.GooSprite;
import com.watabou.utils.Bundle;

import java.util.Arrays;

public class TriggerBlob extends Blob {
	{
		actPriority=3;
	}
	Class<? extends Trigger>[] triggers;

	protected int pos;

	@Override
	protected void evolve() {

		int cell;

		for (int i = area.left; i < area.right; i++){
			for (int j = area.top; j < area.bottom; j++){
				cell = i + j*Dungeon.level.width();
				off[cell] = cur[cell];
				if (off[cell] > 0) {
					volume += off[cell];
				}
				if (cur[cell]>0) {
					//System.out.println(triggers[cell]);
					Char ch = Actor.findChar(cell);
					Trigger tr;
					try {
						tr = triggers[cell].newInstance();
					} catch (Exception e) {
						MoonshinePixelDungeon.reportException(e);
						tr = new DummyTrigger();
					}
					if (tr.triggerBy(ch)) {
						if (tr.trigger(ch)) {
							volume -= cur[cell];
							cur[cell] = off[cell] = 0;
						}
					}
				}
			}
		}
	}

	@Override
	public void seed(Level level, int cell, int amount) {
		seed(level,cell,DummyTrigger.class);
	}

	public void seed(Level level, int cell, Class<? extends Trigger> trigger){
		super.seed(level, cell, 1);
		if (triggers==null){
			triggers = new Class[level.map.length];
			Arrays.fill(triggers, DummyTrigger.class);
		}
		triggers[cell]=trigger;
	}
	@SuppressWarnings("unchecked")
	public static TriggerBlob place( int cell, Level level, Class<? extends Trigger> trigger ) {
		try {
			//System.out.println("place_start");
			TriggerBlob gas = (TriggerBlob)Blob.seed(cell,100,TriggerBlob.class,level);

			gas.seed( level, cell, trigger );

			//System.out.println("place_finished");

			//System.out.println(gas.triggers[cell]);
			return gas;

		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("triggers",triggers);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		triggers=bundle.getClassArray("triggers");
	}
}

