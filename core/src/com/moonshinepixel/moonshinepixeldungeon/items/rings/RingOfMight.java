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
package com.moonshinepixel.moonshinepixeldungeon.items.rings;


import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;

public class RingOfMight extends Ring {

	@Override
	public boolean doEquip(Hero hero) {
		if (super.doEquip(hero)){
//			hero.HT += level()*5;
//			hero.HP = Math.min(hero.HP, hero.HT);
			hero.updateHT(false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {

		if (super.doUnequip(hero, collect, single)){
//			hero.HT -= level()*5;
//			hero.HP = Math.min(hero.HP, hero.HT);
			hero.updateHT(false);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public Item upgrade() {
		if (buff != null && buff.target != null){
//			buff.target.HT += 5;
			if (buff.target instanceof Hero) {
				((Hero)buff.target).updateHT();
			}
		}
		return super.upgrade();
	}

	@Override
	public void level(int value) {
//		if (buff != null && buff.target != null){
//			buff.target.HT -= level()*5;
//		}
		super.level(value);
		if (buff != null && buff.target != null){
//			buff.target.HT += level()*5;
//			buff.target.HP = Math.min(buff.target.HP, buff.target.HT);
			if (buff.target instanceof Hero) {
				((Hero)buff.target).updateHT();
			}
		}
	}

	@Override
    public RingBuff buff() {
		return new Might();
	}

	public class Might extends RingBuff {
	}
}

