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

import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ShopBlob;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.levels.RegularLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.BlackjackShopRoom;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.InterlevelScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.BlackjackkeeperSprite;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Blackjackkeeper extends NPC {

	{
		spriteClass = BlackjackkeeperSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	private boolean die = false;

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (InterlevelScene.mode!=InterlevelScene.Mode.CONTINUE)
			die=true;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
	}

	@Override
	protected boolean act() {

		HP=HT=Random.Int(1000000);

		throwItem();

		if (die){
			flee();
		}

		sprite.turnTo( pos, Dungeon.hero.pos );
		spend( TICK );
		return true;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
		GLog.w(Messages.titleCase(name)+": "+Messages.get(this, Random.oneOf("dmg1","dmg2","dmg3","dmg4")));
	}
	
	@Override
	public void add( Buff buff ) {
		GLog.w(Messages.titleCase(name)+": "+Messages.get(this, Random.oneOf("dmg1","dmg2","dmg3","dmg4")));
	}
	
	public void flee() {
		((BlackjackShopRoom)((RegularLevel)Dungeon.level).room(pos)).unseal();
		if (Dungeon.visible[pos])
			GLog.n(Messages.titleCase(name) + ": " + Messages.get(this, "goodbye"));
		try {
			Dungeon.level.blobs.get(ShopBlob.class).explode(pos);
		} catch (Exception e){

		}
		CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 8);
		destroy();
		sprite.killAndErase();
	}
	
	@Override
	public boolean reset() {
		return true;
	}


	@Override
	public boolean interact() {
		if (Blob.volumeAt(pos, ShopBlob.class) <= 0 && !die) {
			GLog.n(Messages.titleCase(name) + ": " + Messages.get(this, "welcome"));
			((BlackjackShopRoom) ((RegularLevel) Dungeon.level).room(pos)).placeItems(Dungeon.level);
			((BlackjackShopRoom) ((RegularLevel) Dungeon.level).room(pos)).seal();
		} else {
			flee();
		}


		return false;
	}
}
