package com.moonshinepixel.moonshinepixeldungeon.items.guns;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfBlastWave;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Random;

/**
 * Created by Анна on 01.08.2017.
 */
public class Mortair extends BulletGun {
    {
        image = ItemSpriteSheet.MORTAIR;
        this._load.fill(Bullet.class);
        shake=2f;
    }
    @Override
    public int tier() {
        return 5;
    }

    @Override
    public float reloadTime() {
        return 1.5f;
    }


    @Override
    public float shotTime() {
        return 1.5f;
    }

    @Override
    protected int initialCharges() {
        return 1;
    }

//    @Override
//    public int damageRoll(int lvl) {
//        return (int)(super.damageRoll(lvl)*1.2f);
//    }


    @Override
    public int minWnd() {
        return (int)(super.minWnd()*1.2f);
    }

    @Override
    public int maxWnd() {
        return (int)(super.maxWnd()*1.2f);
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
        return 1f;
    }

    @Override
    protected boolean onHit(Ballistica bolt, Char targ, Hero user) {
        int oppositeHero = targ.pos + (targ.pos - user.pos);
        Ballistica trajectory = new Ballistica(targ.pos, oppositeHero, Ballistica.MAGIC_BOLT);
        WandOfBlastWave.throwChar(targ, trajectory, 2);
        return true;
    }
}
