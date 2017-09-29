package com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments;

import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;

public class SteelBayonet extends Gun.Attachment{
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
                    0.75f,   //accuracy mod
                    1f,      //reload speed mod
                    1f,      //shooting speed mod
                    1.2f,      //melee min dmg mod
                    1.2f,      //melee max dmg mod
            };
            return mods;
        } else {
            float[] mods = new float[]{
                    0.5f,      //accuracy mod
                    1f,      //reload speed mod
                    1f,      //shooting speed mod
                    1/1.5f,      //melee min dmg mod
                    1/1.5f,      //melee max dmg mod
            };
            return mods;
        }
    }

    @Override
    public float[] wndDmgMod() {
        float[] mods;
        if (!curse()){
            mods = new float[]{
                    1,
                    1
            };
        } else {
            mods = new float[]{
                    1,
                    1
            };
        }
        return mods;
    }

    @Override
    public float failChanceMod() {
        float mod = 1;
        if (!curse()){
            mod = 1;
        } else {
            mod = 1;
        }
        return mod;
    }
}
