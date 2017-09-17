package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.HashSet;


/*  All non-default bullets disabled!
 *
 *  But this bullet so cool that i added it as function of class armor
 */


public class GunslingerBullet extends BulletAmmo {


    {
        image = ItemSpriteSheet.BULLET;
    }

    public GunslingerBullet() {
        this( 1 );
    }

    public GunslingerBullet(int number ) {
        super();
        quantity = number;
    }

    @Override
    public int ballisticaPropeties(){
        return Ballistica.STOP_TERRAIN;
    }

    @Override
    public boolean miss(int cell, Char shooter, int dmg){
        super.miss(cell, shooter, dmg);
        triggerPos(cell, shooter, true);
        return true;
    }
    public boolean hit(Char targ, Char shooter, int dmg){
        miss(targ.pos,shooter,dmg);
        return true;
    }

    public boolean triggerPos(int cell, Char shooter, boolean safe){
        Bomb bomb = new Bomb();
        if (safe) {
            bomb.explode(cell, true);
        } else {
            bomb.explode(cell);
        }
        return true;
    }

    private int chargesPerCast(){
        return 1;
    }

    //the actual affected cells
    private HashSet<Integer> affectedCells;
    //the cells to trace fire shots to, for visual effects.


    @Override
    public void fx( Ballistica bolt, Callback callback ) {
        affectedCells = new HashSet<>();
        int dist =bolt.dist;

        for (int c : bolt.subPath(1, dist)) {
            affectedCells.add(c);
        }

        for (final int cell : affectedCells){
            Callback call = new Callback() {
                @Override
                public void call() {
                    triggerPos(cell, null, true);
                }
            };

            Char enemy = Actor.findChar( cell );
            Item proto = this;
            if (enemy!=null){
                ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                        reset( curUser.pos, enemy.pos, proto, call );
            } else {
                ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                        reset(curUser.pos, cell, proto, call);
            }
        }
        final int cell = bolt.collisionPos;
        Char enemy = Actor.findChar( cell );
        Item proto = this;
        //proto.image=ItemSpriteSheet.BULLET;
        if (enemy!=null){
            ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                    reset( curUser.pos, enemy.pos, proto, callback );
        } else {
            ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                    reset(curUser.pos, cell, proto, callback);
        }

        Sample.INSTANCE.play( Assets.SND_BLAST );
    }


    public boolean shot(Ballistica bolt, Class<? extends Ammo> ammo) {
        int cell =bolt.collisionPos;
        for (int pos:PathFinder.CIRCLE8){
            triggerPos(cell, null, true);
        }
        return true;
    }
    @Override
    public boolean collect() {
        for (int i = 0; i< this.quantity;i++) {
            try {
                triggerPos(Dungeon.hero.pos, Dungeon.hero, false);
            } catch (Exception e) {
                MoonshinePixelDungeon.reportException(e);
                e.printStackTrace();
            }
        }
        return false;
    }
}
