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

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.LockedFloor;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Paralysis;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Terror;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.WeaponKit;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.CapeOfThorns;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.LloydsBeacon;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.SkeletonKey;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfBlastWave;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Grim;
import com.moonshinepixel.moonshinepixeldungeon.levels.GardenBossLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.bossRooms.GardenBossRoom;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.DM300Sprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.DummyMobSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ForestSpiritSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BossHealthBar;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class ForestSpirit extends Mob {
	
	{
		spriteClass = ForestSpiritSprite.class;
		
		HP = HT = 100;
		EXP = 10;
		defenseSkill = 8;

//		loot = new WeaponKit();
//		lootChance = 1;

		stonesreward=Random.IntRange(0,1);

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 7 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 120;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 2);
	}
	
	@Override
	public boolean act() {
		return super.act();
	}

	@Override
	public boolean doAttack(final Char enemy) {
		if (enemy == Dungeon.hero)
			Dungeon.hero.resting = false;
		boolean complete = true;
		if (Random.Int(5)==0){
			complete=false;
			int oppositeHero = enemy.pos + (enemy.pos - pos);
			Ballistica trajectory = new Ballistica(enemy.pos, oppositeHero, Ballistica.MAGIC_BOLT);

			WandOfBlastWave.throwChar(enemy, trajectory, 5, true, new Callback() {
				@Override
				public void call() {
					onAttackComplete();
					((GardenBossLevel)Dungeon.level).draw((GardenBossRoom) Dungeon.level.room(enemy.pos));
				}
			});
		}

		((ForestSpiritSprite)sprite).attack( enemy.pos, complete );
		spend( attackDelay() );
		return true;
	}

	@Override
	public void die( Object cause ) {
		
		super.die( cause );
		
		GameScene.bossSlain();
		Dungeon.level.unseal();
		
		Badges.validateBossSlain();
		
		yell( Messages.get(this, "defeated") );
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
	}

	public void say(){
		yell( Messages.get(this, Random.oneOf("say1","say2","say3")) );
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();
	static {
		RESISTANCES.add( Grim.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();
	static {
	}

	@Override
	public boolean canAttack(Char enemy) {
		return Dungeon.level.distance(pos,enemy.pos)<=3 && new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	public int attackProc(final Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		return damage;
	}

	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
	}
}
