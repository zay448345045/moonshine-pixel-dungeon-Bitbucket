package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Sheep;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public abstract class Ammo extends MissileWeapon {
    public static final String ammoType = "";
    public abstract String getAmmoType();
    public int damageRoll(){
        return Random.NormalIntRange(min(), max());
    }
    public boolean miss(int cell, Char shooter, int dmg){
        Dungeon.spark(cell);
        return false;
    }
    public boolean hit(Char targ, Char shooter, int dmg){
        return false;
    }

    public int ballisticaPropeties(){
        return Ballistica.STOP_CHARS | Ballistica.STOP_TERRAIN;
    }

    public void fx(Ballistica bolt, Callback callback ) {
        final int cell = bolt.collisionPos;

        Char enemy = Actor.findChar( cell );

        Item proto = this;
        if (enemy!=null){
            ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                    reset( curUser.pos, enemy.pos, proto, callback );
        } else {
            ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                    reset(curUser.pos, cell, proto, callback);
        }

        Sample.INSTANCE.play( Assets.SND_BLAST );
    }

    public void fxPos(Ballistica bolt, Callback callback ) {
        final int cell = bolt.collisionPos;
        int pos = bolt.sourcePos;


        Sheep sheep = new Sheep();
        sheep.pos=pos;
        sheep.lifespan = 2;
        GameScene.add(sheep);

        Char enemy = Actor.findChar( cell );
        Item proto = this;
        if (enemy!=null){
            ((MissileSprite)sheep.sprite.parent.recycle( MissileSprite.class )).
                    reset(  sheep.pos, enemy.pos, proto, callback );
        } else {
            ((MissileSprite) sheep.sprite.parent.recycle(MissileSprite.class)).
                    reset( sheep.pos, cell, proto, callback);
        }
        sheep.destroy();
        sheep.sprite.destroy();

        Sample.INSTANCE.play( Assets.SND_BLAST );
    }

    public boolean shot(Ballistica bolt, Class<? extends Ammo> ammo){
        int cell = bolt.collisionPos;
        Char ch = Actor.findChar( cell );
        Ammo bullet;
        try {
            bullet = ammo.newInstance();
        } catch (Exception e){
            bullet = new Bullet();
        }
        int dmg = ((Gun)curUser.belongings.weapon).damageRoll()+bullet.damageRoll();
            boolean visibleFight= Dungeon.visible[ch.pos];
                ch.damage(Math.max(dmg-ch.drRoll(),0), this);

                if (ch.isAlive()){
                    bullet.hit(ch,curUser,dmg);
                }
                if (visibleFight){
                    Sample.INSTANCE.play( Assets.SND_HIT, 1, 1, Random.Float( 0.8f, 1.25f ) );
                    ch.sprite.bloodBurstA( ch.sprite.center(), dmg );
                    ch.sprite.flash();
                }
                if (!ch.isAlive() && visibleFight) {

                    if (ch == Dungeon.hero) {
                        Dungeon.fail( getClass() );
                        GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name)) );

                    } else if (curUser == Dungeon.hero) {
                        GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", ch.name)) );
                    }
                }
                return true;
    }

    public class Type{
        public static final String BULLET =      "bullet";
        public static final String DART =        "dart";
    }
}
