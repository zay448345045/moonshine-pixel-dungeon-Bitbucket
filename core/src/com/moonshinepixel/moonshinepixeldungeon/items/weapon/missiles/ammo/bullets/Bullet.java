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
package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Bleeding;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Cripple;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Bullet extends BulletAmmo {

	{
		image = ItemSpriteSheet.BULLET;
	}

	public Bullet() {
		this( 1 );
	}

	public Bullet(int number ) {
		super();
		quantity = number;
	}

	@Override
	public Item random() {
		quantity = Random.NormalIntRange( 15, 30 );
		return this;
	}
	public Item random(float mod) {
		quantity = Random.NormalIntRange( (int)(15*mod), (int)(30*mod) );
		return this;
	}
	
	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return 4 * quantity;
	}

	@Override
	public boolean miss(int cell, Char shooter, int dmg){
		super.miss(cell,shooter,dmg);
		return true;
	}
	public boolean hit(Char targ, Char shooter, int dmg){
		if (Random.Int(100)<25) Buff.affect(targ, Bleeding.class).set(dmg);
		if (Random.Int(100)<25) Buff.prolong(targ, Cripple.class, dmg);
//		miss(targ.pos,shooter,dmg);
		return true;
	}
}
