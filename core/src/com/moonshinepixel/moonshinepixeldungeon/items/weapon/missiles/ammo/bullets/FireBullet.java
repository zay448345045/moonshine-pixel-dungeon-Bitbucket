package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Fire;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.MagicMissile;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.FlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;


/*  All non-default bullets disabled!
 *
 *  Fire bullet ignites cell bullet lands at.
 */


public class FireBullet extends Bullet {

    public FireBullet() {
        this( 1 );
    }

    public FireBullet(int number ) {
        super();
        quantity = number;
    }

    @Override
    public boolean miss(int cell, Char shooter, int dmg){
        super.miss(cell, shooter, dmg);
            if (Level.flamable[cell]
                    || Actor.findChar(cell) != null
                    || Dungeon.level.heaps.get(cell) != null) {

                GameScene.add(Blob.seed(cell, 2, Fire.class));

            } else {

                CellEmitter.get(cell).burst(FlameParticle.FACTORY, 2);

            }
        return true;
    }
    public boolean hit(Char targ, Char shooter, int dmg){
        miss(targ.pos,shooter,dmg);
        return true;
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

        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.FIRE,
                curUser.sprite,
                bolt.collisionPos,
                null);

        Sample.INSTANCE.play( Assets.SND_BLAST );
    }
}
