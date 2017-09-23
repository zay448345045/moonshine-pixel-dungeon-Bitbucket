package com.moonshinepixel.moonshinepixeldungeon.items.bombs;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.BlastParticle;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.SmokeParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SmallBomb extends Bomb {
    {
        image = ItemSpriteSheet.SMALLBOMB;
    }

    public SmallBomb(){
        super();
    }
    public SmallBomb(int explodeDelay){
        super();
        fuseDly=explodeDelay;
    }

    @Override
    public Item random() {
        quantity(Random.NormalIntRange(2,6));
        return this;
    }

    @Override
    public int price(boolean levelKnown, boolean cursedKnown) {
        return 10 * quantity;
    }


    public void explode(int cell, boolean safe){
        //We're blowing up, so no need for a fuse anymore.
        this.fuse = null;

        Sample.INSTANCE.play( Assets.SND_BLAST );

        if (Dungeon.visible[cell]) {
            CellEmitter.center( cell ).burst( BlastParticle.FACTORY, 30 );
        }

        boolean terrainAffected = false;
        try {
            for (int n : PathFinder.NEIGHBOURS9) {
                int c = cell + n;
                if (c >= 0 && c < Dungeon.level.length()) {
                    if (Dungeon.visible[c]) {
                        CellEmitter.get(c).burst(SmokeParticle.FACTORY, 2);
                    }
                    Dungeon.spark(cell);
                    if (Level.flamable[c] && c==cell) {
                        Dungeon.level.destroy(c);
                        GameScene.updateMap(c);
                        terrainAffected = true;
                    }

                    //destroys items / triggers bombs caught in the blast.
                    Heap heap = Dungeon.level.heaps.get(c);
                    if (heap != null && c==cell)
                        heap.explode();

                    Char ch = Actor.findChar(c);
                    if (ch != null && !(safe && ch instanceof Hero)) {
                        //those not at the center of the blast take damage less consistently.
                        int minDamage = c == cell ? Dungeon.fakedepth[Dungeon.depth] + 5 : 1;
                        int maxDamage = 10 + Dungeon.fakedepth[Dungeon.depth] * 4;

                        int dmg = Random.NormalIntRange(minDamage, maxDamage)/2 - ch.drRoll();
                        if (dmg > 0) {
                            ch.damage(dmg, this);
                        }

                        if (ch == Dungeon.hero && !ch.isAlive())
                            Dungeon.fail(getClass());
                    }
                    Dungeon.level.press(c, null);
                }
            }
        } catch (Exception e){

        }

        if (terrainAffected) {
            Dungeon.observe();
        }
    }
}
