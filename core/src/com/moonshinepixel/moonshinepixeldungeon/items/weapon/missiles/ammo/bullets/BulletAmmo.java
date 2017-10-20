package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets;

import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.EquipableItem;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;

import java.util.ArrayList;

public abstract class BulletAmmo extends Ammo {
    {
        defaultAction=null;
    }
    public int min(int i) {
        return 0;
    }
    public int max(int i) {
        return 0;
    }

    @Override
    public int STRReq() {
        return 0;
    }
    @Override
    public int STRReq(int i) {
        return 0;
    }

    @Override
    public String getAmmoType(){
        return Type.BULLET;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.remove( EquipableItem.AC_EQUIP );
        return actions;
    }
    @Override
    public String info() {
    return Messages.get(this, "desc");
    }
    @Override
    public void onThrow(int cell) {
        Heap heap = Dungeon.level.drop( this, cell );
        if (!heap.isEmpty()) {
            heap.sprite.drop( cell );
        }
    }
}
