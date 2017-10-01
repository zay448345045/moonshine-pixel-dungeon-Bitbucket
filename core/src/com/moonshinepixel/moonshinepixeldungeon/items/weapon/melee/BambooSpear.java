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
package com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BambooSpear extends MeleeWeapon {

	private static final String AC_BREAK = "BREAK";

	private static final float dly = 10;

	private final BambooSpear spear = this;

	private float growLevel = 0;

	private Growth growth;
	{
		image = ItemSpriteSheet.BAMBOSPEAR;

		tier = 2;

	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions =  super.actions(hero);
		if (growLevel>=dly)
			actions.add(AC_BREAK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_BREAK)){
			if (growLevel>=dly){
				growLevel-=dly;
				GLog.i(Messages.get(this,"broken"));
				curUser.spendAndNext(2);
			}
		}
	}

	public BambooSpear(){
		super();
		growth = new Growth();
	}

	private boolean act(){
		if (this.isEquipped(Dungeon.hero)){
			grow(Random.Float(0,0.1f));
		}
		growth.spend(Actor.TICK);
		return true;
	}

	public BambooSpear grow(float num){
		growLevel+= num;
//		DLY=(float)(1+Math.pow(Math.sqrt(1.5),reachFactor(Dungeon.hero)-1));
		DLY=Math.max((float)(Math.sqrt(reachFactor(Dungeon.hero)*1.5f)),1);
		return this;
	}

	@Override
	public int reachFactor(Hero hero) {
		return super.reachFactor(hero)+(int)(growLevel/dly);
	}

	@Override
	public String info() {
		String info = super.info();
		info+="\n\n"+ Messages.get(this,"reach",reachFactor(Dungeon.hero));
		return info;
	}

	private static final String ACTOR = "actor";
	private static final String GROWTH= "growth";
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GROWTH,growLevel);
	}

	@Override
	public boolean doEquip(Hero hero) {
		if( super.doEquip(hero)) {
			growth=new Growth();
			growth.attachTo(hero);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if(super.doUnequip(hero, collect, single)){
			growth.detach();
			return true;
		}
		return false;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		growLevel=bundle.getFloat(GROWTH);
	}

	@Override
	public Item random() {
		super.random();
		int length = Random.chances(new float[]{3,0.75f,0.429f,0.3f,0.231f,0.188f});
		grow(length*dly);
		return grow(length*dly);
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		growth=new Growth();
		growth.attachTo(ch);
	}

	private class Growth extends Buff {
		@Override
		public boolean act() {
			if (growth!=this){
					detach();
					return true;
			}
			System.out.println(growLevel);
			return spear.act();
		}

		@Override
		public void spend(float time) {
			super.spend(time);
		}
	}
}
