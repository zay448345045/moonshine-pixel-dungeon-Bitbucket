package com.moonshinepixel.moonshinepixeldungeon.actors.blobs.triggers;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Roots;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Vine;
import com.moonshinepixel.moonshinepixeldungeon.effects.Pushing;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.GardenLasherSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class VineTrigger extends Trigger {
    @Override
    public boolean trigger(Char targ) {
        int pos = targ.pos;
        Char ch = Actor.findChar( pos );
        if (ch != null) {
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS8) {
                int cell = pos + n;
                if ((Level.getPassable(cell) || Level.getAvoid(cell)) && Actor.findChar( cell ) == null) {
                    candidates.add( cell );
                }
            }
            if (candidates.size() > 0) {
                int newPos = Random.element( candidates );
                Actor.addDelayed( new Pushing( ch, ch.pos, newPos ), -1 );

                ch.pos = newPos;
                // FIXME
                if (ch instanceof Mob) {
                    Dungeon.level.mobPress( (Mob)ch );
                } else {
                    Dungeon.level.press( newPos, ch );
                }
            } else {
                return false;
            }
        }

        Vine m = new Vine();
        m.pos = pos;
        m.state = m.HUNTING;
        GameScene.add( m, 1 );

        m.sprite.turnTo( pos, Dungeon.hero.pos );
        ((GardenLasherSprite)m.sprite).playApear();
        Buff.prolong(targ, Roots.class,2);

        return true;
    }
}
