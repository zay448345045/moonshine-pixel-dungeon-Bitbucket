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
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.Chains;
import com.moonshinepixel.moonshinepixeldungeon.effects.Hook;
import com.moonshinepixel.moonshinepixeldungeon.effects.Pushing;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.Artifact;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.EtherealChains;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Projecting;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.CellSelector;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTilemap;
import com.moonshinepixel.moonshinepixeldungeon.ui.QuickSlotButton;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SwitchHook extends MeleeWeapon {

	private final String AC_GRAB = "GRAB";

	{
		image = ItemSpriteSheet.HOOK;

		tier = 2;

		defaultAction=AC_GRAB;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_GRAB);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (this.isEquipped(curUser)) {
			if (action.equals(AC_GRAB)) {
				GameScene.selectCell(caster);
			}
		} else {
			GLog.i( Messages.get(this, "equip_need") );
			QuickSlotButton.cancel();
		}
	}


	private CellSelector.Listener caster = new CellSelector.Listener(){

		@Override
		public void onSelect(Integer target) {
			if (target != null && (Dungeon.visible[target])) {
				if (Dungeon.level.distance(curUser.pos, target) <= (2 + curItem.level())*(hasEnchant(Projecting.class)?1:1.5f)) {
					//ballistica does not go through walls on pre-rework boss arenas
					int missileProperties = Ballistica.STOP_CHARS | Ballistica.STOP_TARGET | (hasEnchant(Projecting.class)?0:Ballistica.STOP_TERRAIN);

					final Ballistica chain = new Ballistica(curUser.pos, target, missileProperties);

					if (Dungeon.hasPatch(curUser.pos,chain.collisionPos)) {
						//determine if we're grabbing an enemy, pulling to a location, or doing nothing.
						if (Actor.findChar(chain.collisionPos) != null) {
							final int newMobPos = curUser.pos;
							final Char affected = Actor.findChar(chain.collisionPos);
							if (affected.properties().contains(Char.Property.IMMOVABLE)) {
								GLog.w(Messages.get(SwitchHook.class, "cant_pull"));
								return;
							} else {
								updateQuickslot();
							}
							curUser.busy();
							curUser.sprite.parent.add(new Hook(curUser.sprite.center(), affected.sprite.center(), new Callback() {
								public void call() {
									if (curUser.attack(affected)) {
										Actor.add(new Pushing(affected, affected.pos, newMobPos, new Callback() {
											public void call() {
												Dungeon.level.press(newMobPos, affected);
											}
										}));
										Actor.add(new Pushing(curUser, curUser.pos, affected.pos, new Callback() {
											public void call() {
												Dungeon.level.press(affected.pos, curUser);
											}
										}));
										curUser.pos = affected.pos;
										affected.pos = newMobPos;

										Dungeon.observe();
										GameScene.updateFog();
									}
									curUser.spendAndNext(2f);
								}
							}));

						} else {
							GLog.i(Messages.get(EtherealChains.class, "nothing_to_grab"));
						}
					} else {
						GLog.i(Messages.get(SwitchHook.class, "impassable"));
					}

				} else {
					GLog.i(Messages.get(SwitchHook.class, "too_far"));
				}
			}
		}

		@Override
		public String prompt() {
			return Messages.get(EtherealChains.class, "prompt");
		}
	};

	@Override
	public Item random() {
		tier=Random.NormalIntRange(2,5);
		return super.random();
	}
}
