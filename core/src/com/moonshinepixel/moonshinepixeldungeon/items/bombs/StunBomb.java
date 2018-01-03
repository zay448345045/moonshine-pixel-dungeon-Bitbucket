package com.moonshinepixel.moonshinepixeldungeon.items.bombs;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ExplosiveGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Blindness;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Cripple;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Vertigo;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.SmokeParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.HashSet;

public class StunBomb extends Bomb {
    {

        image = ItemSpriteSheet.LIGHTBOMB;

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
        HashSet<Char> chars = new HashSet<>();

        if (Dungeon.visible[cell]) {
            GameScene.flash(0xFFFFFF);
            CellEmitter.get(cell).burst(Speck.factory(Speck.LIGHT), 4);
            chars.add(Dungeon.hero);
        }

        for (Mob m : Dungeon.level.mobs){
            Dungeon.level.updateFieldOfView(m, Level.fieldOfView);
            if (Level.fieldOfView[cell]){
                chars.add(m);
            }
        }

        for (Char ch : chars) {
            if (ch != null) {
                int len = Random.Int(4, 8);
                Buff.prolong(ch, Blindness.class, len*2);
                Buff.prolong(ch, Vertigo.class, len);

                if (ch instanceof Mob) {
                    ((Mob)ch).clearEnemy();
                    if (((Mob) ch).state == ((Mob) ch).HUNTING) ((Mob) ch).state = ((Mob) ch).WANDERING;
                    ((Mob) ch).beckon(Dungeon.level.randomDestination());
                }
                if (ch == Dungeon.hero) {
                    Sample.INSTANCE.play(Assets.SND_BLAST);
                }
            }
        }

        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null)
            heap.explode(destroyAll);
        Dungeon.level.press(cell, null);
    }
}
