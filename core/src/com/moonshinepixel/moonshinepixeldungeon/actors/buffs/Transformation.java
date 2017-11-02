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

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Bestiary;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Moonshine;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.HeroSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.InvisbleMobSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Transformation extends FlavourBuff {

	public static final float DURATION = 40f;
	public Mob mob;
	public boolean agressive;

	{
		type = buffType.POSITIVE;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.TRANSFORM;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!(target instanceof Hero)) return false;
		return super.attachTo(target);
	}

	public void setMob(Mob mob) {
		this.mob = mob;

		((Hero)target).spriteClass = this.mob.spriteClass;

		((Hero) target).updateHT(true);
		((Hero) target).updateStats();
		target.updateFlying();

		if (Game.scene() instanceof GameScene) {
			Game.resetScene();
			GameScene.flash(0xFFFFFF);
		}
		agressive=false;
	}

	public void prepareAttack(){
//		mob.spriteClass=InvisbleMobSprite.class;
		mob.HP=target.HP;
		mob.pos=target.pos;
		mob.hostile=false;
		mob.ally=true;
		GameScene.add(mob);
		GameScene.scene.addToFront(mob.sprite);
		mob.sprite.add(CharSprite.State.NOSPRITE);
		for (Buff buff : target.buffs()){
			if (buff instanceof FlavourBuff){
				Buff.prolong(mob, ((FlavourBuff)buff).getClass(), buff.cooldown());
			} else {
				Buff.affect(mob, buff.getClass());
			}
		}
	}
	public void finishAttack(){
//		try {
			target.HP=mob.HP;
			Dungeon.level.mobs.remove( mob );
			mob.sprite.killAndErase();
//			mob.destroy();
//			mob = mob.getClass().newInstance();
//		} catch (Exception e){
//			MoonshinePixelDungeon.reportException(e);
//		}
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public void detach() {
		super.detach();
		((Hero) target).updateHT(false);
		((Hero) target).updateStats();
		target.updateFlying();
		((Hero) target).spriteClass=HeroSprite.class;
		if (Game.scene() instanceof GameScene) {
			Game.resetScene();
			GameScene.flash(0xFFFFFF);
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put("mob",mob);
		bundle.put("agr",agressive);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		mob=(Mob)bundle.get("mob");
		agressive=bundle.getBoolean("agr");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", mob.name, dispTurns());
	}
}
