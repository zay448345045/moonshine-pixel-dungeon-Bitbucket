package com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.sprites.DogSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Dog extends NPC {

    public enum Command{
        NONE,
        GUARD,
        FOLLOW,
        STAY;

        public static int target = -1;
        public static int dieTime = 0;
    }

    public Command command;

    {
        spriteClass = DogSprite.class;

        state = WANDERING;
        enemy = null;

        command=Command.NONE;

        ally = true;
        hostile = false;
    }

    public Dog() {
        super();
    }

    public int level(){
        return Dungeon.hero.STR-9;
    }

    @Override
    protected boolean act() {
        return super.act();
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
    }

    @Override
    protected boolean getCloser( int target ) {
        if (command==Command.GUARD && Dungeon.level.distance(target, Dungeon.hero.pos) > 6)
            this.target = target = Dungeon.hero.pos;
        if (command==Command.FOLLOW)
            this.target = target = Dungeon.hero.pos;
        if (command==Command.STAY) return false;
        return super.getCloser( target );
    }

    @Override
    protected Char chooseEnemy() {
        if(enemy!=null&&enemy.isAlive()&&Dungeon.level.mobs.contains(enemy)) {
            if (command == Command.STAY && !Dungeon.level.adjacent(pos,enemy.pos)) {
                HashSet<Mob> enemies = new HashSet<Mob>();
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.hostile && Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE&&Dungeon.level.adjacent(pos,mob.pos)) {
                        enemies.add(mob);
                    }
                }
                enemy = enemies.size() > 0 ? Random.element( enemies ) : null;
            }
        } else {
            if (command == Command.GUARD) {
                HashSet<Mob> enemies = new HashSet<Mob>();
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.hostile && Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE) {
                        enemies.add(mob);
                    }
                }
                enemy = enemies.size() > 0 ? Random.element( enemies ) : null;
            } else if (command==Command.STAY){
                HashSet<Mob> enemies = new HashSet<Mob>();
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.hostile && Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE&&Dungeon.level.adjacent(pos,mob.pos)) {
                        enemies.add(mob);
                    }
                }
                enemy = enemies.size() > 0 ? Random.element( enemies ) : null;
            }
        }
        return enemy;
    }

    @Override
    public int attackSkill(Char target) {
        return level()*4;
    }

    public void levelup(){
        HT=HP=10+level()*8;
        defenseSkill=level()*3;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange((int)(level()*2.44f),(int)(level()*3.33f));
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, level()+1);
    }


    @Override
    public boolean interact() {

        int curPos = pos;

        moveSprite( pos, Dungeon.hero.pos );
        move( Dungeon.hero.pos );

        Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
        Dungeon.hero.move( curPos );

        Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
        Dungeon.hero.busy();

        return true;
    }

    private static final String CMD         = "command";
    private static final String TARG        = "target";
    private static final String DIETIME     = "dietime";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(CMD,command);
        bundle.put(TARG,Command.target);
        bundle.put(DIETIME,Command.dieTime);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        command=bundle.getEnum(CMD,Command.class);
        Command.target=bundle.getInt(TARG);
        Command.dieTime=bundle.getInt(DIETIME);
    }
}
