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
package com.moonshinepixel.moonshinepixeldungeon.actors.mobs;

import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.VenomGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Paralysis;
import com.moonshinepixel.moonshinepixeldungeon.items.food.MysteryMeat;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.sprites.PiranhaSprite;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Roots;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.Statistics;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Burning;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Frost;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Piranha extends Mob {
	
	{
		spriteClass = PiranhaSprite.class;

		baseSpeed = 2f;
		
		EXP = 0;
	}
	
	public Piranha() {
		super();
		
		HP = HT = 10 + Dungeon.fakedepth[Dungeon.depth] * 5;
		defenseSkill = 10 + Dungeon.fakedepth[Dungeon.depth] * 2;
	}
	
	@Override
	protected boolean act() {
		if (!Level.water[pos]) {
			die( null );
			sprite.killAndErase();
			return true;
		} else {
			//this causes pirahna to move away when a door is closed on them.
			Dungeon.level.updateFieldOfView( this, Level.fieldOfView );
			enemy = chooseEnemy();
			if (state == this.HUNTING &&
					!(enemy != null && enemy.isAlive() && Level.fieldOfView[enemy.pos] && enemy.invisible <= 0)){
				state = this.WANDERING;
				int oldPos = pos;
				int i = 0;
				do {
					i++;
					target = Dungeon.level.randomDestination();
					if (i == 100) return true;
				} while (!getCloser(target));
				moveSprite( oldPos, pos );
				return true;
			}

			return super.act();
		}
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( Dungeon.fakedepth[Dungeon.depth], 4 + Dungeon.fakedepth[Dungeon.depth] * 2 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20 + Dungeon.fakedepth[Dungeon.depth] * 2;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, Dungeon.fakedepth[Dungeon.depth]);
	}
	
	@Override
	public void die( Object cause ) {
		Dungeon.level.drop( new MysteryMeat(), pos ).sprite.drop();
		super.die( cause );
		
		Statistics.piranhasKilled++;
		Badges.validatePiranhasKilled();
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	protected boolean getCloser( int target ) {
		
		if (rooted) {
			return false;
		}
		
		int step = Dungeon.findStep( this, pos, target,
			Level.water,
			Level.fieldOfView );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean getFurther( int target ) {
		int step = Dungeon.flee( this, pos, target,
			Level.water,
			Level.fieldOfView );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	private static final HashSet<Class> IMMUNITIES = new HashSet<>();
	static {
		IMMUNITIES.add( Burning.class );
		IMMUNITIES.add( Paralysis.class );
		IMMUNITIES.add( ToxicGas.class );
		IMMUNITIES.add( VenomGas.class );
		IMMUNITIES.add( Roots.class );
		IMMUNITIES.add( Frost.class );
	}
	
	@Override
	public HashSet<Class> immunities() {
		return IMMUNITIES;
	}
}
