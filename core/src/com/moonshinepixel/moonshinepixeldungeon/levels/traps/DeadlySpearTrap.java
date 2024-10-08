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

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.effects.Wound;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class DeadlySpearTrap extends ActingTrap {

	{
		color = GREY;
		shape = STARS;
	}

	@Override
	protected boolean act() {
		if (!visible)
			reveal();
		if (!active) {
			Level.set(pos, Terrain.TRAP);
			active = true;
			GameScene.updateMap(pos);
			if (Actor.findChar(pos)!=null)
			if(!Actor.findChar(pos).flying){
				trigger();
				activate();
			}
			actor.spend(Actor.TICK);
		} else {
			active=false;
			Level.set(pos, Terrain.INACTIVE_TRAP);
			GameScene.updateMap(pos);
			actor.spend(Actor.TICK*3);
		}
		return true;
	}

	@Override
	public void trigger() {
		if (Dungeon.visible[pos]){
			Sample.INSTANCE.play(Assets.SND_TRAP);
		}
		//this trap is not disarmed by being triggered
		reveal();
		Level.set(pos, Terrain.TRAP);
//		activate();
	}


	@Override
	public Trap hide() {
		return reveal();
	}

	@Override
	public Trap set(int pos) {
		super.set(pos);
		actor.spend(Random.Int(5));
		return this;
	}

	@Override
	public void activate() {
		if (Dungeon.visible[pos]){
			Sample.INSTANCE.play(Assets.SND_HIT);
			Wound.hit(pos);
		}

		Char ch = Actor.findChar( pos);
		if (ch != null && !ch.flying){
			int damage = Random.NormalIntRange(Dungeon.fakedepth[Dungeon.depth]*2, Dungeon.fakedepth[Dungeon.depth]*4);
			damage -= ch.drRoll();
			ch.damage( Math.max(damage, 0) , this);
			if (!ch.isAlive() && ch == Dungeon.hero){
				Dungeon.fail( getClass() );
				GLog.n( Messages.get(this, "ondeath") );
			}
		}
	}
}
