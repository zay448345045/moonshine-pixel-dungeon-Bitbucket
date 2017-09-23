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

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.ClusterBomb;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.ShrapnelBomb;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.LightningTrap;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.sprites.BombergnollSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.GnollSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ShamanSprite;
import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.HashSet;

public class Bomberman extends Mob implements Callback {

	private static final float TIME_TO_ZAP	= 1.5f;
	
	{
		spriteClass = BombergnollSprite.class;
		
		HP = HT = 10;
		defenseSkill = 3;
		
		EXP = 2;
		maxLvl = 8;
		
		loot = Generator.Category.BOMB;
		lootChance = 0.66f;
		baseSpeed=1.5f;

		FLEEING = new Fleeing();
	}
	public float delay = 0;

	public boolean attack = false;

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 5 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 9;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		if (!attack && delay<=0) {
			Arrays.fill(PathFinder.distance,Integer.MAX_VALUE);
			PathFinder.buildDistanceMap(pos,Dungeon.level.getPassable());
			Ballistica attack = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
			return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos && PathFinder.distance[enemy.pos] < 5 && PathFinder.distance[enemy.pos] > 2;
		} else if (!attack){
			return false;
		} else {
			return super.canAttack(enemy);
		}
	}

	@Override
	public void spend(float time) {
		super.spend(time);
		delay-=time;
		delay=delay<0?0:delay;
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.distance( pos, enemy.pos ) <= 1) {
//			damage(HP,new Bomb());
//			new Bomb().explode(pos);
//			for (int c : PathFinder.NEIGHBOURS8){
//				System.out.println(c);
//			}
//			int targ = PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)];
//			new Bomb().explode(enemy.pos+targ);
//			spend( 0 );
//			next();
//			return !Dungeon.visible[pos];
			return super.doAttack(enemy);
		} else {
			
			boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos];
			if (visible) {
				sprite.zap( enemy.pos );
			}

			final Ballistica ball = new Ballistica(pos,enemy.pos,Ballistica.PROJECTILE);
			final Bomb sb = new Bomb[]{new Bomb(), new ClusterBomb(), new ShrapnelBomb()}[Random.chances(new float[]{6,1,1})];
			sb.fuseDly=2;
			sb.canPickup=false;
			sb.lightingFuse = true;
			Char ch = Actor.findChar(ball.collisionPos);

			Callback callback = new Callback() {
				@Override
				public void call() {
					sb.onThrow(ball.collisionPos);
					spend( TIME_TO_ZAP );
					next();
					delay+=1;
				}
			};
			if (ch!=null){
				((MissileSprite)sprite.parent.recycle( MissileSprite.class )).
						reset(  pos, ch.pos, sb, callback );
			} else {
				((MissileSprite)sprite.parent.recycle(MissileSprite.class)).
						reset( pos, ball.collisionPos, sb, callback);
			}

			return !visible;
		}
	}

	@Override
	protected boolean getCloser(int target) {
		Arrays.fill(PathFinder.distance,Integer.MAX_VALUE);
		PathFinder.buildDistanceMap(pos,Dungeon.level.getPassable());
		if (PathFinder.distance[target]>=5 || attack){
			return super.getCloser(target);
		} else if (PathFinder.distance[target]<=2){

			boolean move = getFurther(target);

			if (!move){
				baseSpeed=1;
				attack=true;
			}
			return enemySeen;
		} else {
			return enemySeen;
		}
	}

	@Override
	public void call() {
		next();
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();
	static {
		RESISTANCES.add( LightningTrap.Electricity.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("attack", attack);
		bundle.put("dly", delay);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		attack=bundle.getBoolean("attack");
		delay = bundle.getFloat("dly");
	}

	private class Fleeing extends Mob.Fleeing {
		@Override
		protected void nowhereToRun() {
			attack=true;
		}
	}
}
