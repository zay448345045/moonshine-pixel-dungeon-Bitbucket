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

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.EquipableItem;
import com.moonshinepixel.moonshinepixeldungeon.items.craftingitems.EmptyItem;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndOptions;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.scenes.CellSelector;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndBag;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GiantShuriken extends MeleeWeapon {

    private static final String AC_THROWBIG = "THROWBIG";

	{
		image = ItemSpriteSheet.GIGANTSHURIKEN;
		tier = 2;
	}

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero)) {
            actions.remove(Item.AC_THROW);
            actions.add(AC_THROWBIG);
        }
        return actions;
    }

    @Override
    public boolean doEquip(Hero hero) {
	    if (super.doEquip(hero)) {
            defaultAction = AC_THROWBIG;
            updateQuickslot();
            return true;
        } else return false;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if(super.doUnequip(hero, collect, single)){
            defaultAction=null;
            Dungeon.quickslot.clearItem(this);
            updateQuickslot();
            return true;
        } else return false;
    }

    @Override
    public void execute(Hero hero, String action) {
	    curItem=this;
        if (action.equals(AC_THROWBIG) && this.isEquipped(hero)){
            if (!cursed)
                GameScene.selectCell( ((GiantShuriken) Item.curItem).throwerbig );
            else
                GLog.w(Messages.get(EquipableItem.class, "unequip_cursed"));
        }
        super.execute(hero, action);
    }

    public void throwme(Hero hero, int target){
	    final GiantShuriken curWep = this;
        final Ballistica ball = new Ballistica(hero.pos,target,Ballistica.STOP_TERRAIN);
        ArrayList<Integer> cells = ball.path;
        cells.remove(0);
        for (int cell : cells){
            final int targ = cell;
            Callback call = new Callback() {
                @Override
                public void call() {
                    Char enemy = Actor.findChar(targ);
                    if (enemy!=null){
                        if (Item.curUser.hit(Item.curUser,enemy,false)){
                            int dmg = curWep.damageRoll(Item.curUser)- Item.curUser.drRoll();
                            if (curWep.enchantment!=null){
                                dmg=curWep.enchantment.proc(curWep, Item.curUser,enemy,dmg);
                            }
                            if (curWep.suffix!=null){
                                dmg=curWep.suffix.proc(curWep, Item.curUser,enemy,dmg);
                            }
                            enemy.damage(dmg, Item.curUser);
                            enemy.sprite.bloodBurstA( Item.curUser.sprite.center(), dmg );
                            enemy.sprite.flash();
//                            if (curWep.enchantment!=null){
//                                curWep.enchantment.proc(curWep, Item.curUser,enemy,dmg);
//                            }
//                            if (curWep.suffix!=null){
//                                curWep.suffix.proc(curWep, Item.curUser,enemy,dmg);
//                            }
                            if (!enemy.isAlive() && Dungeon.visible[enemy.pos]) {
                                GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", enemy.name)) );
                            }
                        }
                    }
                }
            };
            Char enemy = Actor.findChar( cell );
            Item proto = new EmptyItem();
            if (enemy!=null){
                ((MissileSprite) Item.curUser.sprite.parent.recycle( MissileSprite.class )).
                        reset( Item.curUser.pos, enemy.pos, proto, call );
            } else {
                ((MissileSprite) Item.curUser.sprite.parent.recycle(MissileSprite.class)).
                        reset(Item.curUser.pos, cell, proto, call);
            }
        }

        Callback call2 = new Callback() {
            @Override
            public void call() {
                    Dungeon.level.drop(curWep,ball.collisionPos);
                    Item.curUser.spendAndNext(1f);
                    GameScene.selectItem( itemSelector, WndBag.Mode.WEAPONSLOTABLE, Messages.get(GiantShuriken.class, "selector") );
            }
        };

        Item proto = this;
        int targ = ball.collisionPos;
        Char enemy = Actor.findChar( targ );
        if (enemy!=null){
            ((MissileSprite) Item.curUser.sprite.parent.recycle( MissileSprite.class )).
                    reset( Item.curUser.pos, enemy.pos, proto, call2 );
        } else {
            ((MissileSprite) Item.curUser.sprite.parent.recycle(MissileSprite.class)).
                    reset(Item.curUser.pos, targ, proto, call2);
        }
        Item.curUser.sprite.zap(ball.collisionPos);
        Item.curUser.busy();
        updateQuickslot();
        Dungeon.quickslot.clearItem(this);

    }

    protected static final CellSelector.Listener throwerbig = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                ((GiantShuriken) Item.curItem).throwme( Item.curUser, target );
                if (Dungeon.hero.belongings.weapon== Item.curItem){
                    Dungeon.hero.belongings.weapon=null;
                } else {
                    Item.curItem.detach(Dungeon.hero.belongings.backpack);
                }
            }
        }
        @Override
        public String prompt() {
            return Messages.get(Item.class, "prompt");
        }
    };

    private void confirmCancelation() {
        GameScene.show( new WndOptions( name(), Messages.get(this, "warning"),
                Messages.get(this, "yes"), Messages.get(this, "no") ) {
            @Override
            protected void onSelect( int index ) {
                switch (index) {
                    case 0:
                        break;
                    case 1:
                        GameScene.selectItem( itemSelector, WndBag.Mode.WEAPONSLOTABLE, Messages.get(GiantShuriken.class, "selector") );
                        break;
                }
            }
            public void onBackPressed() {};
        } );
    }

    protected static WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null){
                Weapon wep = (Weapon)item;
                wep.doEquip(Dungeon.hero);
            } else {
                ((GiantShuriken) Item.curItem).confirmCancelation();
            }
        }
    };

    @Override
    public Item random() {
        tier= Random.NormalIntRange(2,5);
        return super.random();
    }
}
