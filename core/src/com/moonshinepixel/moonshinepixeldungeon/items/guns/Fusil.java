package com.moonshinepixel.moonshinepixeldungeon.items.guns;

import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;

/**
 * Created by Анна on 01.08.2017.
 */
public class Fusil extends BulletGun {
    {
        image = ItemSpriteSheet.FUSIL;
        this._load.fill(Bullet.class);
        tier = 4;
    }

    @Override
    public float reloadTime() {
        return 1f;
    }

    @Override
    public float shotTime() {
        return 1f;
    }

    @Override
    protected int initialCharges() {
        return 3;
    }

    @Override
    public String ammoType(){
        return Ammo.Type.BULLET;
    }

    @Override
    public int chargesPerCast() {
        return 1;
    }

    @Override
    public int shootsPerCast() {
        return 1;
    }

    @Override
    public float acu() {
        return 1.5f;
    }
}
