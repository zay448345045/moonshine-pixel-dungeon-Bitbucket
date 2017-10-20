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
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Corruption;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Terror;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Pushing;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.*;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.MasterThievesArmband;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ShopGuardSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ThiefSprite;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShopThief extends Mob {

	private int level;
	public ArrayList<Item> items;

	{
		spriteClass = ShopGuardSprite.class;
		
		HP = HT = 20;
		defenseSkill = 12;
		
		EXP = 5;
		maxLvl = 10;

		properties.add(Property.DEMONIC);
	}

	public Item item(){
		if (items!=null){
			if (items.size()>0){
				return items.get(items.size()-1);
			}
		}
		return null;
	}

	private static final String LEVEL	= "level";
	private static final String ITEMS	= "items";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ITEMS, items );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		items = new ArrayList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
		adjustStats(bundle.getInt(LEVEL));
	}

	public void adjustStats( int level ) {
		this.level = level;

		HP = HT = (level) * 4;
		EXP = 3 + 2 * (level - 1) / 5;
		defenseSkill = attackSkill( null ) / 2;

		enemySeen = true;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( HT / 4, HT / 2 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 6 + level;
	}
	@Override
	public void die( Object cause ) {

		super.die( cause );

		if (items != null) {
			for (Item item : items) {
				Dungeon.level.drop( item, pos ).sprite.drop();
			}
		}
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 3);
	}


	public static ShopThief spawnAt( int pos, Heap heap ) {
		Char ch = Actor.findChar( pos );
		if (ch != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int n : PathFinder.NEIGHBOURS8) {
				int cell = pos + n;
				if ((Level.getPassable(cell) || Level.getAvoid(cell)) && Actor.findChar( cell ) == null) {
					candidates.add( cell );
				}
			}
			if (candidates.size() > 0) {
				int newPos = Random.element( candidates );
				Actor.addDelayed( new Pushing( ch, ch.pos, newPos ), -1 );

				ch.pos = newPos;
				// FIXME
				if (ch instanceof Mob) {
					Dungeon.level.mobPress( (Mob)ch );
				} else {
					Dungeon.level.press( newPos, ch );
				}
			} else {
				return null;
			}
		}

		ShopThief m = new ShopThief();
		if (heap!=null){
			m.items=new ArrayList<>(heap.items);
		} else {
			m.items=new ArrayList<>();
		}
		heap.destroy();
		m.adjustStats( Dungeon.fakedepth[Dungeon.depth] );
		m.pos = pos;
		m.state = m.HUNTING;
		GameScene.add( m, 1 );

		m.sprite.turnTo( pos, Dungeon.hero.pos );

		if (Dungeon.visible[m.pos]) {
			CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 4);
			Sample.INSTANCE.play( Assets.SND_TOMB );
		}

		return m;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public String description() {
		String desc = super.description();

		if (item() != null) {
			desc += Messages.get(this, "carries", item().name() );
		}

		return desc;
	}
}
