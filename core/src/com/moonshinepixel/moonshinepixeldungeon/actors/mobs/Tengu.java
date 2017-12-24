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

import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.LockedFloor;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Poison;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroSubClass;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.LloydsBeacon;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.TenguSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BossHealthBar;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.TomeOfMastery;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Grim;
import com.moonshinepixel.moonshinepixeldungeon.levels.PrisonBossLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.SpearTrap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Tengu extends Mob {
	
	{
		spriteClass = TenguSprite.class;
		
		HP = HT = 120;
		EXP = 20;
		defenseSkill = 20;

		HUNTING = new Hunting();

		flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards
		defFlying = true;

		stonesreward=Random.IntRange(1,2);

		properties.add(Property.BOSS);
	}
	
	@Override
	protected void onAdd() {
		//when he's removed and re-added to the fight, his time is always set to now.
		spend(-cooldown());
		super.onAdd();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 6, 20 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 20;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

	@Override
	public void damage(int dmg, Object src) {

		int beforeHitHP = HP;
		super.damage(dmg, src);
		dmg = beforeHitHP - HP;

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) {
			int multiple = beforeHitHP > HT/2 ? 1 : 4;
			lock.addTime(dmg*multiple);
		}

		//phase 2 of the fight is over
		if (HP == 0 && beforeHitHP <= HT/2) {
			((PrisonBossLevel)Dungeon.level).progress();
			return;
		}

		int hpBracket = beforeHitHP > HT/2 ? 12 : 20;

		//phase 1 of the fight is over
		if (beforeHitHP > HT/2 && HP <= HT/2){
			HP = (HT/2)-1;
			yell(Messages.get(this, "interesting"));
			((PrisonBossLevel)Dungeon.level).progress();
			BossHealthBar.bleed(true);

		//if tengu has lost a certain amount of hp, jump
		} else if (beforeHitHP / hpBracket != HP / hpBracket) {
			jump();
		}
		//When you can attack him but he cn't attack you Tengu jumps
		else if (enemy!=null&&!canAttack(enemy)){
			jump();
		}
	}

	@Override
	public boolean isAlive() {
		return Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
	}

	@Override
	public void die( Object cause ) {
		
		if (Dungeon.hero.subClass == HeroSubClass.NONE) {
			Dungeon.level.drop( new TomeOfMastery(), pos ).sprite.drop();
		}
		
		GameScene.bossSlain();
		super.die( cause );
		
		Badges.validateBossSlain();

		LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
		if (beacon != null) {
			beacon.upgrade();
		}
		
		yell( Messages.get(this, "defeated") );
	}

	@Override
    public boolean canAttack(Char enemy) {
		return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}

	//tengu's attack is always visible
	@Override
    public boolean doAttack(Char enemy) {
		if (enemy == Dungeon.hero)
			Dungeon.hero.resting = false;
		sprite.attack( enemy.pos );
		spend( attackDelay() );
		return true;
}

	private void jump() {
		for (int i=0; i < 4; i++) {
			int trapPos;
			do {
				trapPos = Random.Int( Dungeon.level.length() );
			} while (!Level.fieldOfView[trapPos] || Level.getSolid(trapPos));
			
			if (Dungeon.level.map[trapPos] == Terrain.INACTIVE_TRAP) {
				Dungeon.level.setTrap( new SpearTrap().reveal(), trapPos );
				Level.set( trapPos, Terrain.TRAP );
				ScrollOfMagicMapping.discover( trapPos );
			}
		}

		if (enemy == null) enemy = chooseEnemy();

		int newPos;
		int iters = 0;
		//if we're in phase 1, want to warp around within the room
		if (HP > HT/2) {
			do {
				newPos = Random.Int(Dungeon.level.length());
                iters++;
			} while (
            (!(Dungeon.level.map[newPos] == Terrain.INACTIVE_TRAP || Dungeon.level.map[newPos] == Terrain.TRAP)||
							Level.getSolid(newPos) ||
							Dungeon.level.adjacent(newPos, enemy.pos) ||
							Actor.findChar(newPos) != null) && iters<1000);

		//otherwise go wherever, as long as it's a little bit away
		} else {
			do {
				newPos = Random.Int(Dungeon.level.length());
				iters++;
			} while ((
					Level.getSolid(newPos) ||
					Dungeon.level.distance(newPos, enemy.pos) < 8 ||
					Actor.findChar(newPos) != null) && iters < 1000);
		}
		if (iters>=1000) newPos=pos;
		if (newPos!=pos) {
            if (Dungeon.visible[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);


            sprite.move(pos, newPos);
            move(newPos);

            if (Dungeon.visible[newPos]) CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
            Sample.INSTANCE.play(Assets.SND_PUFF);

            spend(1 / speed());
        } else {
		    spend(0);
        }
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
		if (HP == HT) {
			yell(Messages.get(this, "notice_mine", Dungeon.hero.givenName()));
		} else {
			yell(Messages.get(this, "notice_face", Dungeon.hero.givenName()));
		}
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Poison.class );
		RESISTANCES.add( Grim.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
		if (HP <= HT/2) BossHealthBar.bleed(true);
	}

	//tengu is always hunting
	private class Hunting extends Mob.Hunting{

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (Random.Int(40)==0)jump();
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				} else {
					chooseEnemy();
					if (!canAttack(enemy)&&Random.Int(20)==0)jump();
					target = enemy.pos;
				}

				spend( TICK );
				return true;

			}
		}
	}
}
