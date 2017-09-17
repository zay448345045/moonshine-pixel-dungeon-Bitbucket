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
package com.moonshinepixel.moonshinepixeldungeon.items.wands;

import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.plants.Plant;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Charm;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Corruption;
import com.moonshinepixel.moonshinepixeldungeon.effects.Beam;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.LeafParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.MagesStaff;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTilemap;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.BloodParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ShadowParticle;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class WandOfTransfusion extends Wand {

	{
		image = ItemSpriteSheet.WAND_TRANSFUSION;

		collisionProperties = Ballistica.PROJECTILE;
	}

	private boolean freeCharge = false;

	@Override
	protected void onZap(Ballistica beam) {

		for (int c : beam.subPath(0, beam.dist))
			CellEmitter.center(c).burst( BloodParticle.BURST, 1 );

		int cell = beam.collisionPos;

		Char ch = Actor.findChar(cell);
		Heap heap = Dungeon.level.heaps.get(cell);

		//this wand does a bunch of different things depending on what it targets.

		//if we find a character..
		if (ch != null && ch instanceof Mob){

			processSoulMark(ch, chargesPerCast());

			//heals an ally, or charmed/corrupted enemy
			if (((Mob) ch).ally || ch.buff(Charm.class) != null || ch.buff(Corruption.class) != null){

				int missingHP = ch.HT - ch.HP;
				//heals 30%+3%*lvl missing HP.
				int healing = (int)Math.ceil((missingHP * (0.30f+(0.03f*level()))));
				ch.HP += healing;
				ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + level() / 2);
				ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);

			//harms the undead
			} else if (ch.properties().contains(Char.Property.UNDEAD)){

				//deals 30%+5%*lvl total HP.
				int damage = (int) Math.ceil(ch.HT*(0.3f+(0.05f*level())));
				ch.damage(damage, this);
				ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + level());
				Sample.INSTANCE.play(Assets.SND_BURNING);

			//charms an enemy
			} else {

				float duration = 5+level();
				Buff.affect(ch, Charm.class, Charm.durationFactor(ch) * duration).object = Item.curUser.id();

				duration *= Random.Float(0.75f, 1f);
				Buff.affect(Item.curUser, Charm.class, Charm.durationFactor(ch) * duration).object = ch.id();

				ch.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );
				Item.curUser.sprite.centerEmitter().start( Speck.factory( Speck.HEART ), 0.2f, 5 );

			}


		//if we find an item...
		} else if (heap != null && heap.type == Heap.Type.HEAP){
			Item item = heap.peek();

			//30% + 10%*lvl chance to uncurse the item and reset it to base level if degraded.
			if (item != null && Random.Float() <= 0.3f+level()*0.1f){
				if (item.cursed){
					item.cursed = false;
					Item itm = item;
					itm.level(itm.level()>0?itm.level():-itm.level());
					CellEmitter.get(cell).start( ShadowParticle.UP, 0.05f, 10 );
					Sample.INSTANCE.play(Assets.SND_BURNING);
				}

				int lvldiffFromBase = item.level() - (item instanceof Ring ? 1 : 0);
				if (lvldiffFromBase < 0){
					item.upgrade(-lvldiffFromBase);
					CellEmitter.get(cell).start(Speck.factory(Speck.UP), 0.2f, 3);
					Sample.INSTANCE.play(Assets.SND_BURNING);
				}
			}

		//if we find some trampled grass...
		} else if (Dungeon.level.map[cell] == Terrain.GRASS) {

			//regrow one grass tile, suuuuuper useful...
			Dungeon.level.set(cell, Terrain.HIGH_GRASS);
			GameScene.updateMap(cell);
			CellEmitter.get( cell ).burst(LeafParticle.LEVEL_SPECIFIC, 4);

		//If we find embers...
		} else if (Dungeon.level.map[cell] == Terrain.EMBERS) {

			//30% + 3%*lvl chance to grow a random plant, or just regrow grass.
			if (Random.Float() <= 0.3f+level()*0.03f) {
				Dungeon.level.plant((Plant.Seed) Generator.random(Generator.Category.SEED), cell);
				CellEmitter.get( cell ).burst(LeafParticle.LEVEL_SPECIFIC, 8);
				GameScene.updateMap(cell);
			} else{
				Dungeon.level.set(cell, Terrain.HIGH_GRASS);
				GameScene.updateMap(cell);
				CellEmitter.get( cell ).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			}

		} else
			return; //don't damage the hero if we can't find a target;

		if (!freeCharge) {
			damageHero();
		} else {
			freeCharge = false;
		}
	}

	//this wand costs health too
	private void damageHero(){
		// 10% of max hp
		int damage = (int)Math.ceil(Item.curUser.HT*0.10f);
		Item.curUser.damage(damage, this);

		if (!Item.curUser.isAlive()){
			Dungeon.fail( getClass() );
			GLog.n( Messages.get(this, "ondeath") );
		}
	}

	@Override
	protected int initialCharges() {
		return 1;
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// lvl 0 - 10%
		// lvl 1 - 18%
		// lvl 2 - 25%
		if (Random.Int( level() + 10 ) >= 9){
			//grants a free use of the staff
			freeCharge = true;
			GLog.p( Messages.get(this, "charged") );
			attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
		}
	}

	@Override
	protected void fx(Ballistica beam, Callback callback) {
		Item.curUser.sprite.parent.add(
				new Beam.HealthRay(Item.curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
		callback.call();
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0xCC0000 );
		particle.am = 0.6f;
		particle.setLifespan(1f);
		particle.speed.polar( Random.Float(PointF.PI2), 2f );
		particle.setSize( 1f, 2f);
		particle.radiateXY(0.5f);
	}

	private static final String FREECHARGE = "freecharge";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		freeCharge = bundle.getBoolean( FREECHARGE );
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( FREECHARGE, freeCharge );
	}

}
