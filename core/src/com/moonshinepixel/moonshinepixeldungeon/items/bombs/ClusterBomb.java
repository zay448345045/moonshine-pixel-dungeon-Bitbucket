package com.moonshinepixel.moonshinepixeldungeon.items.bombs;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.SmokeParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class ClusterBomb extends Bomb {
    {

        image = ItemSpriteSheet.CLUSTERBOMB;

        defaultAction = AC_LIGHTTHROW;
        usesTargeting = true;

        stackable = true;

        fuseDly=1;
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
//            CellEmitter.center( cell ).burst( BlastParticle.FACTORY, 30 );
            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 10);
        }

        boolean terrainAffected = false;
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null)
            heap.explode();
        try {
            Dungeon.spark(cell);
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
            Dungeon.level.press(cell, null);
            int repeats = Random.NormalIntRange(3,6);
            for (int i = 0; i<repeats;i++){
                final int targ;
                HashSet<Integer> targs = new HashSet<>();
                for (int of : PathFinder.NEIGHBOURS24){
                    int c = cell+of;
                    if (Dungeon.level.insideMap(c)){
                        if (Dungeon.level.map[c]!= Terrain.CHASM){
                            targs.add(c);
                        }
                    }
                }
                if (targs.size()>0) {
                    Ballistica ball = new Ballistica(cell,Random.element(targs),Ballistica.PROJECTILE);
                    targ = ball.collisionPos;
                } else {
                    targ = cell;
                }
                Emitter emiter = CellEmitter.get(cell);
                Char enemy = Actor.findChar(targ);
                final SmallBomb sb = new SmallBomb(0);
                sb.canPickup=false;
                sb.lightingFuse=true;
                Callback callback = new Callback() {
                    @Override
                    public void call() {
                        sb.onThrow(targ);
                    }
                };
                if (enemy!=null){
                    ((MissileSprite)emiter.recycle( MissileSprite.class )).
                            reset(  cell, enemy.pos, sb, callback );
                } else {
                    ((MissileSprite) emiter.recycle(MissileSprite.class)).
                            reset( cell, targ, sb, callback);
                }
            }
        } catch (Exception e){

        }
    }
}
