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
package com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.VenomGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MirrorSprite;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Burning;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class MirrorImage extends NPC {
	
	{
		spriteClass = MirrorSprite.class;
		
		state = HUNTING;
	}
	
	public int tier;
	
	private int attack;
	private int damage;
	
	private static final String TIER	= "tier";
	private static final String ATTACK	= "attack";
	private static final String DAMAGE	= "damage";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( TIER, tier );
		bundle.put( ATTACK, attack );
		bundle.put( DAMAGE, damage );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		tier = bundle.getInt( TIER );
		attack = bundle.getInt( ATTACK );
		damage = bundle.getInt( DAMAGE );
	}
	
	public void duplicate( Hero hero ) {
		tier = hero.tier();
		attack = hero.attackSkill( hero );
		damage = hero.damageRoll();
	}
	
	@Override
	public int attackSkill( Char target ) {
		return attack;
	}
	
	@Override
	public int damageRoll() {
		return damage;
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		int dmg = super.attackProc( enemy, damage );

		destroy();
		sprite.die();
		
		return dmg;
	}
	
	protected Char chooseEnemy() {
		
		if (enemy == null || !enemy.isAlive()) {
			HashSet<Mob> enemies = new HashSet<>();
			for (Mob mob:Dungeon.level.mobs) {
				if (mob.hostile && Level.fieldOfView[mob.pos]) {
					enemies.add( mob );
				}
			}
			
			enemy = enemies.size() > 0 ? Random.element( enemies ) : null;
		}
		
		return enemy;
	}
	
	@Override
	public CharSprite sprite() {
		CharSprite s = super.sprite();
		((MirrorSprite)s).updateArmor( tier );
		return s;
	}

	@Override
	public boolean interact() {
		
		int curPos = pos;
		
		moveSprite( pos, Dungeon.hero.pos );
		move( Dungeon.hero.pos );
		
		Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
		Dungeon.hero.move( curPos );
		
		Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
		Dungeon.hero.busy();

		return true;
	}

	private static final HashSet<Class> IMMUNITIES = new HashSet<>();
	static {
		IMMUNITIES.add( ToxicGas.class );
		IMMUNITIES.add( VenomGas.class );
		IMMUNITIES.add( Burning.class );
	}

	@Override
	public HashSet<Class> immunities() {
		return IMMUNITIES;
	}
}