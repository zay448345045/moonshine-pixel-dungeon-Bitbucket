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

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ArmorPiercingBullet extends Bullet {

	{
		image = ItemSpriteSheet.BULLET;
	}

	public ArmorPiercingBullet() {
		this( 1 );
	}

	public ArmorPiercingBullet(int number ) {
		super();
		quantity = number;
	}

	@Override
	public Item random() {
		quantity = Random.NormalIntRange( 10, 25 );
		return this;
	}
	public Item random(float mod) {
		quantity = Random.NormalIntRange( (int)(10*mod), (int)(25*mod) );
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
		miss(targ.pos,shooter,dmg);
		return true;
	}
	@Override
	public boolean shot(Ballistica bolt, Class<? extends Ammo> ammo){
		int cell = bolt.collisionPos;
		Char ch = Actor.findChar( cell );
		Ammo bullet;
		try {
			bullet = ammo.newInstance();
		} catch (Exception e){
			bullet = new Bullet();
		}
		int dmg = ((Gun) Item.curUser.belongings.weapon).damageRoll()+bullet.damageRoll();
		boolean visibleFight= Dungeon.visible[ch.pos];
		ch.damage(dmg, this);

		if (ch.isAlive()){
			bullet.hit(ch, Item.curUser,dmg);
		}
		if (visibleFight){
			Sample.INSTANCE.play( Assets.SND_HIT, 1, 1, Random.Float( 0.8f, 1.25f ) );
			ch.sprite.bloodBurstA( ch.sprite.center(), dmg );
			ch.sprite.flash();
		}
		if (!ch.isAlive() && visibleFight) {

			if (ch == Dungeon.hero) {
				Dungeon.fail( getClass() );
				GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name)) );

			} else if (Item.curUser == Dungeon.hero) {
				GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", ch.name)) );
			}
		}
		return true;
	}
}
