package com.moonshinepixel.moonshinepixeldungeon.items.guns;

import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.watabou.utils.Random;

/**
 * Created by Анна on 01.08.2017.
 */
public class Blunderbuss extends BulletGun {
    {
        image = ItemSpriteSheet.BLUNDERBUSS;
        this._load.fill(Bullet.class);
        strAcuMod=false;
    }
    @Override
    public int tier() {
        return 3;
    }

    @Override
    public float reloadTime() {
        return 0.75f;
    }


    @Override
    public float shotTime() {
        return 1f;
    }

    @Override
    protected int initialCharges() {
        return 2;
    }

    @Override
    public int damageRoll(int lvl) {
        return (int)(super.damageRoll(lvl)/1.5f);
    }

    @Override
    public String ammoType(){
        return Ammo.Type.BULLET;
    }
    @Override
    public int chargesPerCast() {
        return 2;
    }

    @Override
    public int shootsPerCast() {
        return 3;
    }

    @Override
    public float acu() {
        return 0.75f;
    }
}
