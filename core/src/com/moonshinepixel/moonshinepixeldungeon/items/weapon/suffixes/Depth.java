package com.moonshinepixel.moonshinepixeldungeon.items.weapon.suffixes;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.KindOfWeapon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.watabou.utils.Random;

public class Depth extends KindOfWeapon.Suffix{
    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        if (Random.Int(100)<weapon.level()*2+2.5f){
            damage+=defender.HP/2;
        }
        return damage;
    }

    @Override
    public float[] modifiers() {
        return new float[]{
                0.8f,	//min dmg(*)
                0.8f,	//max dmg(*)
                1.5f,   //speed(/)
                0f,		//defence(casted to int)(+)
        };
    }
}
