package com.moonshinepixel.moonshinepixeldungeon.items.guns;

import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;

public class GunslingerPistol extends BulletGun {
    {
        image = ItemSpriteSheet.HANDCRAFTGUN;
//        image = ItemSpriteSheet.BOW;
        this._load.fill(Bullet.class);
        tier=1;
    }
    @Override
    protected int initialCharges() {
        return 1;
    }
    @Override
    public float reloadTime(){
        return 0.5f;
    }

    @Override
    public float shotTime() {
        return 0.5f;
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

    @Override
    public Item random() {
        super.random();
        attachment=null;
        return this;
    }
}
