package com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.effects.CellEmitter;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.SparkParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTilemap;
import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.effects.Lightning;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.LightningTrap;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TeslaCoil extends Gun.Attachment{

    private ArrayList<Char> affected = new ArrayList<>();

    ArrayList<Lightning.Arc> arcs = new ArrayList<>();

    @Override
    public boolean miss(Ballistica boltt, Class<? extends Ammo> ammo, final Gun curGun) {
        final Ballistica bolt;
        boolean selfStrike = Random.Int(100)<(curse()?75:10);
        if (!selfStrike){
            bolt=boltt;
        } else {
            bolt = new Ballistica(boltt.sourcePos,boltt.sourcePos,Ballistica.PROJECTILE);
        }


        fx(bolt, new Callback() {
            @Override
            public void call() {
                //lightning deals less damage per-target, the more targets that are hit.
                float multipler = 0.4f + (0.6f/affected.size());
                //if the main target is in water, all affected take full damage
                if (Level.water[bolt.collisionPos]) multipler = 1f;

                int min = 3 + curGun.level();
                int max = 4 * curGun.level();


                for (Char ch : affected){
                    ch.damage(Math.round(Random.NormalIntRange(min,max) * multipler), LightningTrap.LIGHTNING);

                    if (ch == Dungeon.hero) Camera.main.shake( 2, 0.3f );
                    ch.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
                    ch.sprite.flash();
                }

                if (!Dungeon.hero.isAlive()) {
                    Dungeon.fail( getClass() );
                    GLog.n(Messages.get(this, "ondeath"));
                }
            }
        });
        return true;
    }
    protected void fx( Ballistica bolt, Callback callback ) {

        affected.clear();
        arcs.clear();

        int cell = bolt.collisionPos;

        Char ch = Actor.findChar( cell );
        if (ch != null) {
            arcs.add( new Lightning.Arc(Dungeon.hero.sprite.center(), ch.sprite.center()));
            arc(ch);
        } else {
            arcs.add( new Lightning.Arc(Dungeon.hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
            CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
        }

        //don't want to wait for the effect before processing damage.
        Dungeon.hero.sprite.parent.addToFront( new Lightning( arcs, null ) );
        callback.call();
    }
    private void arc( Char ch ) {

        affected.add( ch );

        int dist;
        if (Level.water[ch.pos] && !ch.flying)
            dist = 2;
        else
            dist = 1;

        PathFinder.buildDistanceMap( ch.pos, BArray.not(Level.getSolid(), null ), dist );
        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE){
                Char n = Actor.findChar( i );
                if (n == Dungeon.hero && PathFinder.distance[i] > 1)
                    //the hero is only zapped if they are adjacent
                    continue;
                else if (n != null && !affected.contains( n )) {
                    arcs.add(new Lightning.Arc(ch.sprite.center(), n.sprite.center()));
                    arc(n);
                }
            }
        }
    }

    @Override
    public boolean shot(Ballistica bolt, Class<? extends Ammo> ammo, Gun curGun) {
        miss(bolt, ammo, curGun);
        return false;
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
