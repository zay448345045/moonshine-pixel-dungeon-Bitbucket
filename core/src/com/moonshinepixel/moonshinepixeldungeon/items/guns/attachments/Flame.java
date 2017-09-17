package com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Fire;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.FlameParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfLiquidFlame;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Flame extends Gun.Attachment{
    Hero curUser = Dungeon.hero;
    @Override
    public boolean miss(Ballistica bolt, Class<? extends Ammo> ammo, Gun CurGun) {
        return false;
    }

    @Override
    public boolean shot(Ballistica bolt, Class<? extends Ammo> ammo, Gun curGun) {
        return false;
    }

    @Override
    public boolean onStartShoot(Ballistica bolt, Class<? extends Ammo> ammo, Gun curGun, Char curUser) {
        boolean selfStrike = Random.Int(100)<(curse()?75:10);
        if (!selfStrike) {
            fx(bolt, null);
        } else {
            PotionOfLiquidFlame polf = new PotionOfLiquidFlame();
            polf.shatter(curUser.pos);
        }
        return true;

    }

    public boolean triggerPos(int cell, Char shooter, boolean safe){
        if (Level.flamable[cell]
                || Actor.findChar(cell) != null
                || Dungeon.level.heaps.get(cell) != null) {

            GameScene.add(Blob.seed(cell, 2, Fire.class));

        } else {
            CellEmitter.get(cell).burst(FlameParticle.FACTORY, 2);
            GameScene.add(Blob.seed(cell, 1, Fire.class));
        }
        Sample.INSTANCE.play( Assets.SND_BURNING );
        return true;
    }
    private HashSet<Integer> affectedCells;
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
            Item proto = new Bullet();
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
        Item proto = new Bullet();
        proto.image= ItemSpriteSheet.EMPTY;
        if (enemy!=null){
            ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                    reset( curUser.pos, enemy.pos, proto, callback );
        } else {
            ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                    reset(curUser.pos, cell, proto, callback);
        }
    }


    @Override
    public ItemSprite.Glowing glowing() {
        return null;
    }
    @Override
    public float[] modifiers(){
        if(!curse()) {
            float[] mods = new float[]{
                    1f,      //accuracy mod
                    1f,      //reload speed mod
                    1f,      //shooting speed mod
                    1f,      //melee min dmg mod
                    1f,      //melee max dmg mod
            };
            return mods;
        } else {
            float[] mods = new float[]{
                    1f,      //accuracy mod
                    1f,      //reload speed mod
                    1f,      //shooting speed mod
                    1f,      //melee min dmg mod
                    1f,      //melee max dmg mod
            };
            return mods;
        }
    }
}
