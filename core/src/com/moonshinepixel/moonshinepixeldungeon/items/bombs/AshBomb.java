package com.moonshinepixel.moonshinepixeldungeon.items.bombs;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.AshGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.SmokeParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class AshBomb extends Bomb {
    {

        image = ItemSpriteSheet.BOMB4;

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
//            CellEmitter.center( cell ).burst( BlastParticle.FACTORY, 30 );
            CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 10);
        }

        boolean terrainAffected = false;
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null)
            heap.explode();
        try {
            Char ch = Actor.findChar(cell);
            if (ch != null && !(safe && ch instanceof Hero)) {
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

            Dungeon.spark(cell);
            GameScene.add(Blob.seed(cell, 100, AshGas.class));
            int gas = 100;
            for(int n: PathFinder.NEIGHBOURS4) {
                if (!Dungeon.level.getSolid(cell+n)) {
                    Dungeon.spark(cell + n);
                    GameScene.add(Blob.seed(cell + n, 100, AshGas.class));
                } else {
                    gas+=100;
                }
            }
            Dungeon.spark(cell);
            GameScene.add(Blob.seed(cell, gas, AshGas.class));
        } catch (Exception e){

        }
    }
}
