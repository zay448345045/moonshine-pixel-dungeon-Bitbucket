package com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;


/*  All non-default bullets disabled!
 *
 *  This bullet broken. don't use.
 */


public class BouncingBullet extends Bullet {

    public BouncingBullet() {
        this( 1 );
    }

    public BouncingBullet(int number ) {
        super();
        quantity = number;
    }

    @Override
    public boolean miss(int cell, Char shooter, int dmg){
        return true;
    }
    public boolean hit(Char targ, Char shooter, int dmg){
        return true;
    }

    @Override
    public int ballisticaPropeties() {
        return Ballistica.STOP_TERRAIN;
    }

    private enum Dir{ NONE,
        NW,  N, NE,
        W,      E,
        SW,  S, SE
    }
    private enum Bounce{ NONE,
        X,Y,BACK
    }


    @Override
    public void fx(final Ballistica bolt, Callback callback ) {
        final Callback call1 = new Callback() {
            @Override
            public void call() {
                int x1 = PathFinder.pos2x(bolt.sourcePos);
                int y1 = PathFinder.pos2y(bolt.sourcePos);
                int x2 = PathFinder.pos2x(bolt.collisionPos);
                int y2 = PathFinder.pos2y(bolt.collisionPos);
                int pos1 = bolt.sourcePos;
                int pos2 = bolt.collisionPos;
                int[] ofsts = PathFinder.NEIGHBOURS8;
                int col = 1;
                int colPos = bolt.collisionPos2;
                boolean[] wallArr = new boolean[8];
                for (int i = 0; i<ofsts.length;i++){
                    if (Dungeon.level.getSolid(ofsts[i]+pos2)) wallArr[i]=true;
                    if (ofsts[i]+pos2==colPos) col = i;
                }
//                Dir dir = Dir.NONE;
                int dir = 0;
                Dir _dir = Dir.NONE;
                Bounce bnc = Bounce.NONE;
                if (Math.abs(x1-x2)>Math.abs(y1-y2)){
                    if (x1-x2<0) dir = 5;
                    if (x1-x2>0) dir = 4;
                } else if (Math.abs(x1-x2)<Math.abs(y1-y2)){
                    if (y1-y2>0) dir = 7;
                    if (y1-y2<0) dir = 2;
                } else {
                    if (x1-x2>0 && y1-y2>0) dir = 6;
                    if (x1-x2<0 && y1-y2>0) dir = 8;
                    if (x1-x2<0 && y1-y2<0) dir = 3;
                    if (x1-x2>0 && y1-y2<0) dir = 1;
                }
                dir--;
                switch (col){
                    case 0:
                        _dir=Dir.NW;
                        break;
                    case 1:
                        _dir=Dir.N;
                        break;
                    case 2:
                        _dir=Dir.NE;
                        break;
                    case 3:
                        _dir=Dir.W;
                        break;
                    case 4:
                        _dir=Dir.E;
                        break;
                    case 5:
                        _dir=Dir.SW;
                        break;
                    case 6:
                        _dir=Dir.S;
                        break;
                    case 7:
                        _dir=Dir.SE;
                        break;
                }
                if (_dir==Dir.NW){
                    switch (dir){
                        case 1:
                            bnc=Bounce.X;
                            break;
                        case 3:
                            bnc=Bounce.Y;
                            break;
                        default:
                            if(wallArr[1] && !wallArr[3]){
                                bnc=Bounce.X;
                            } else if(!wallArr[1] && wallArr[3]){
                                bnc=Bounce.Y;
                            } else {
                                bnc=Bounce.BACK;
                            }
                    }
                } else if (_dir==Dir.N){
                            bnc=Bounce.X;
                } else if (_dir==Dir.NE){
                    switch (dir){
                        case 4:
                            bnc=Bounce.Y;
                            break;
                        case 1:
                            bnc=Bounce.X;
                            break;
                        default:
                            if(wallArr[7] && !wallArr[3]){
                                bnc=Bounce.X;
                            } else if(!wallArr[7] && wallArr[3]){
                                bnc=Bounce.Y;
                            } else {
                                bnc=Bounce.BACK;
                            }
                    }
                } else if (_dir==Dir.W){
                            bnc=Bounce.Y;
                } else if (_dir==Dir.E){
                            bnc=Bounce.Y;
                } else if (_dir==Dir.SW){
                    switch (dir){
                        case 3:
                            bnc=Bounce.Y;
                            break;
                        case 6:
                            bnc=Bounce.X;
                            break;
                        default:
                            if(wallArr[7] && !wallArr[6]){
                                bnc=Bounce.X;
                            } else if(!wallArr[7] && wallArr[6]){
                                bnc=Bounce.Y;
                            } else {
                                bnc=Bounce.BACK;
                            }
                    }
                } else if (_dir==Dir.S){
                            bnc=Bounce.X;
                } else if (_dir==Dir.SE){
                    switch (dir){
                        case 4:
                            bnc=Bounce.Y;
                            break;
                        case 6:
                            bnc=Bounce.X;
                            break;
                        default:
                            if(wallArr[6] && !wallArr[4]){
                                bnc=Bounce.X;
                            } else if(!wallArr[6] && wallArr[4]){
                                bnc=Bounce.Y;
                            } else {
                                bnc=Bounce.BACK;
                            }
                    }
                }

                int targx;
                int targy;
                int targ;
                final Ballistica bolt2;
                switch (bnc) {
                    case BACK:
                        bolt2 = new Ballistica(bolt.collisionPos,bolt.sourcePos,Ballistica.GUNBULLET);
                        BouncingBullet.super.fxPos(bolt2, new Callback() {
                            @Override
                            public void call() {
                                ((Gun) Dungeon.hero.belongings.weapon).onZap(bolt2, Bullet.class);
                                ((Gun) Dungeon.hero.belongings.weapon).wandUsed();
                            }
                        });
                        break;
                    case X:
                        targx = 2*x2-x1;
                        targy = y1;
                        targ = PathFinder.xy2pos(targx,targy);
                        bolt2 = new Ballistica(bolt.collisionPos,targ,Ballistica.GUNBULLET);
                        BouncingBullet.super.fxPos(bolt2, new Callback() {
                            @Override
                            public void call() {
                                ((Gun) Dungeon.hero.belongings.weapon).onZap(bolt2, Bullet.class);
                                ((Gun) Dungeon.hero.belongings.weapon).wandUsed();
                            }
                        });
                        break;
                    case Y:
                        targy = 2*y2-y1;
                        targx = x1;
                        targ = PathFinder.xy2pos(targx,targy);
                        bolt2 = new Ballistica(bolt.collisionPos,targ,Ballistica.GUNBULLET);
                        BouncingBullet.super.fxPos(bolt2, new Callback() {
                            @Override
                            public void call() {
                                ((Gun) Dungeon.hero.belongings.weapon).onZap(bolt2, Bullet.class);
                                ((Gun) Dungeon.hero.belongings.weapon).wandUsed();
                            }
                        });
                        break;
                }
            }
        };
        final int cell = bolt.collisionPos;

        Char enemy = Actor.findChar( cell );

        Item proto = new Bullet();
        if (enemy!=null){
            ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                    reset( curUser.pos, enemy.pos, proto, call1 );
        } else {
            ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                    reset(curUser.pos, cell, proto, call1);
        }

        Sample.INSTANCE.play( Assets.SND_BLAST );
    }

    /*@Override
    public void shot(Ballistica bolt, Class<? extends Ammo> ammo) {
        final BulletAmmo bull2;
        bull2 = new Bullet();
//        final Bullet bull2 = new Bullet();
//        final GunslingerBullet bull2 = new GunslingerBullet();
//        final BouncingBullet bull2 = this;
        final Ballistica shot = new Ballistica( bolt.collisionPos, bolt.sourcePos, bull2.ballisticaPropeties());
        int cell = bolt.collisionPos;

        bull2.fxPos(shot, new Callback() {
            @Override
            public void call() {
                //bull.shot(shot, bull.getClass());
                ((Gun)Dungeon.hero.belongings.weapon).onZap(shot,bull2.getClass());
                ((Gun)Dungeon.hero.belongings.weapon).wandUsed();
            }
        }, cell);
    }*/
}
