package com.moonshinepixel.moonshinepixeldungeon.items.guns;

import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;

/**
 * Created by Анна on 01.08.2017.
 */
public class Pistol extends BulletGun {
    {
        image = ItemSpriteSheet.PISTOL;
        this._load.fill(Bullet.class);
    }
    @Override
    public int tier() {
        return 2;
    }
    @Override
    protected int initialCharges() {
        return 2;
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
    public float acu() {
        return 1f;
    }
}
