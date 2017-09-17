package com.moonshinepixel.moonshinepixeldungeon.items.guns;

import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;

/**
 * Created by Анна on 01.08.2017.
 */
public class GunslingerPistol extends BulletGun {
    {
        image = ItemSpriteSheet.HANDCRAFTGUN;
        this._load.fill(Bullet.class);
    }
    @Override
    public int tier() {
        return 1;
    }
    @Override
    protected int initialCharges() {
        return 1;
    }
    @Override
    public float reloadTime(){
        return 0.75f;
    }

    @Override
    public float shotTime() {
        return 0.75f;
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
        return 0.9f;
    }
}
