package com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments;

import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;

public class ExtendedLoad extends Gun.Attachment {
    @Override
    public boolean miss(Ballistica bolt, Class<? extends Ammo> ammo, Gun CurGun) {
        return false;
    }

    @Override
    public boolean shot(Ballistica bolt, Class<? extends Ammo> ammo, Gun curGun) {
        return false;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return null;
    }

    @Override
    public float[] modifiers(){
        if(!curse()) {
            float[] mods = new float[]{
                    1f,      //accuracy mod
                    1.25f,      //reload speed mod
                    1f,      //shooting speed mod
                    1f,      //melee min dmg mod
                    1f,      //melee max dmg mod
            };
            return mods;
        } else {
            float[] mods = new float[]{
                    1f,      //accuracy mod
                    0.5f,      //reload speed mod
                    1f,      //shooting speed mod
                    1f,      //melee min dmg mod
                    1f,      //melee max dmg mod
            };
            return mods;
        }
    }

    @Override
    public int loadMod(Gun.Load curLoad, Gun gun) {
        int size = curLoad.maxLoad();
        if (!curse()){
            if (gun.chargesPerCast()>2) {
                size += gun.chargesPerCast();
            } else size += gun.chargesPerCast()*2;
        } else {
            size-=gun.chargesPerCast();
            size=size>=gun.chargesPerCast()?size:gun.chargesPerCast();
        }
        return size;
    }
}
