package com.moonshinepixel.moonshinepixeldungeon.items.bombs;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.BlastParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.SmokeParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class ShrapnelBomb extends Bomb {
    {

        image = ItemSpriteSheet.BOMB3;

        defaultAction = AC_LIGHTTHROW;
        usesTargeting = true;

        stackable = true;
    }

    @Override
    public Item random() {
        quantity(Random.chances(new float[]{0,4,2,1}));
        return this;
    }

    @Override
    public void explode(int cell, boolean safe) {
        this.fuse = null;

        Sample.INSTANCE.play( Assets.SND_BLAST );

        if (Dungeon.visible[cell]) {
            CellEmitter.center( cell ).burst( BlastParticle.FACTORY, 30 );
            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 30);
        }

        boolean terrainAffected = false;
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null)
            heap.explode();
        try {
            Char ch = Actor.findChar(cell);
            if (ch != null && !(safe && ch instanceof Hero)) {
                //those not at the center of the blast take damage less consistently.
                int minDamage = 1;
                int maxDamage = 5 + Dungeon.fakedepth[Dungeon.depth];

                int dmg = Random.NormalIntRange(minDamage, maxDamage) - ch.drRoll();
                if (dmg > 0) {
                    ch.damage(dmg, this);
                }

                if (ch == Dungeon.hero && !ch.isAlive())
                    Dungeon.fail(getClass());
            }
            Dungeon.spark(cell);
            Dungeon.level.press(cell, null);
            for (int n : PathFinder.NEIGHBOURS24) {
                int c = cell + n;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Random.Int(100)<75){
                        final Ballistica ball = new Ballistica(cell, c, Ballistica.GUNBULLET);
                        final Shrapnel sh = new Shrapnel();
                        final ShrapnelBomb bmb = this;
                        sh.fxPos(ball, new Callback() {
                            @Override
                            public void call() {
                                int minDamage = Dungeon.fakedepth[Dungeon.depth] + 3;
                                int maxDamage = 6 + Dungeon.fakedepth[Dungeon.depth] * 4 / 3;
                                sh.shot(ball, minDamage,maxDamage, bmb);
                            }
                        });
                    }
                }
            }
        } catch (Exception e){

        }
    }
    private class Shrapnel extends Bullet {
        public void fxPos(Ballistica bolt, Callback callback ) {
            final int cell = bolt.collisionPos;
            int pos = bolt.sourcePos;


//            Sheep sheep = new Sheep();
//            sheep.pos=pos;
//            sheep.lifespan = 2;
//            GameScene.add(sheep);
            Emitter emiter = CellEmitter.get(pos);

            Char enemy = Actor.findChar( cell );
            Item proto = new Bullet();
            if (enemy!=null){
                ((MissileSprite)emiter.recycle( MissileSprite.class )).
                        reset(  pos, enemy.pos, proto, callback );
            } else {
                ((MissileSprite) emiter.recycle(MissileSprite.class)).
                        reset( pos, cell, proto, callback);
            }
//            sheep.sprite.killAndErase();
//            sheep.destroy();
        }
        public void shot(Ballistica bolt, int min, int max, Object src){
            int cell = bolt.collisionPos;
            Char enemy = Actor.findChar(cell);
            if (enemy!=null){
                if (enemy.isAlive()){
                    enemy.damage(Random.NormalIntRange(min,max)-enemy.drRoll(), src);
                    if(!enemy.isAlive()){
                        if (enemy == Dungeon.hero) {
                            Dungeon.fail( getClass() );
                            GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name)) );
                        }
                    }
                }
            } else {
                miss(cell, null, 0);
            }
        }
    }
}
