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
        strAcuMod=true;
        tier=3;
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
        return 1;
    }

//    @Override
//    public int damageRoll(int lvl) {
//        return (int)(super.damageRoll(lvl)/3f);
//    }

    @Override
    public int minWnd(int lvl) {
        return super.minWnd(lvl)/3;
    }

    @Override
    public int maxWnd() {
        return super.maxWnd()/3;
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
        return 3;
    }

    @Override
    public float acu() {
        return 1.2f;
    }
}
