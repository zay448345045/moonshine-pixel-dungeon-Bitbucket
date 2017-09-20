package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Bleeding;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;


/*  All non-default bullets disabled!
 *
 *  Applies bleeding.
 */


public class BleedingBullet extends Bullet {



    public BleedingBullet() {
        this( 1 );
    }

    public BleedingBullet(int number ) {
        super();
        quantity = number;
    }

    @Override
    public boolean miss(int cell, Char shooter, int dmg){
        super.miss(cell, shooter, dmg);
        return false;
    }
    public boolean hit(Char targ, Char shooter, int dmg){
        Buff.affect(targ, Bleeding.class).set(dmg);
        return true;
    }

    @Override
    public int min(int i) {
        return 2;
    }

    @Override
    public int max(int i) {
        return 5;
    }

    @Override
    public void fx(Ballistica bolt, Callback callback ) {
        final int cell = bolt.collisionPos;

        Char enemy = Actor.findChar( cell );

        Item proto = new Bullet();
        if (enemy!=null){
            ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                    reset( curUser.pos, enemy.pos, proto, callback );
        } else {
            ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                    reset(curUser.pos, cell, proto, callback);
        }

        /*MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FIRE,
                curUser.sprite,
                bolt.collisionPos,
                null);*/

        Sample.INSTANCE.play( Assets.SND_BLAST );
    }

    @Override
    public int price(boolean levelKnown, boolean cursedKnown) {
        return 6 * quantity;
    }
}
