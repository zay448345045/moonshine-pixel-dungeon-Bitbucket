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
package com.moonshinepixel.moonshinepixeldungeon.actors.buffs;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Drunk extends Buff {
	public float drunk = 50;
	public float drunkCap = 100;
	private boolean informed = false;

	{
		actPriority=4; //acts after all other buffs
		type = buffType.NEUTRAL;
	}

	@Override
	public boolean act() {
		drunk--;
		if (drunk>drunkCap) {
			effect();
		} else informed=false;
		if (drunk<=0) {
			drunkCap=100;
			detach();
		}
		spend(Actor.TICK);
		return true;
	}

	public float dmgMod(){
		return drunk/drunkCap*.65f;
	}

	public float acumod(){
		return 1f-drunk/drunkCap*.65f;
	}

	private void effect(){
		if(!informed){
			GLog.n(Messages.get(this,"drunk"));
			informed=true;
		}
		float drunkEffect = Random.Float((drunk-drunkCap)/3f,drunk-drunkCap);
		drunk-=Random.Float(drunk-drunkCap);
		switch (Random.Int(8)){
			case 0:
				Buff.affect(target,Vertigo.class,drunkEffect);
				break;
			case 1:
				Buff.affect(target,Paralysis.class,drunkEffect);
				break;
			case 2:
				Buff.affect(target,Blindness.class,drunkEffect);
				break;
			case 3:
				Buff.affect(target,MagicalSleep.class);
				break;
			case 4:
				HashSet<Mob> mobs = new HashSet<>();
				for (Mob mob : Dungeon.level.mobs){
					if (Dungeon.level.adjacent(mob.pos,target.pos) && mob.hostile){
						mobs.add(mob);
					}
				}
				if (mobs.size()>0)
					Buff.affect( target, Charm.class, Charm.durationFactor( target ) * drunkEffect ).object = Random.element(mobs).id();
				break;
		}
	}

	@Override
	public int icon() {
		return BuffIndicator.DRUNK;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("drunk",drunk);
		bundle.put("drunkcap",drunkCap);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		drunk=bundle.getFloat("drunk");
		drunkCap=bundle.getFloat("drunkcap");
	}
}
