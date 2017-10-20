/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.moonshinepixel.moonshinepixeldungeon.actors.mobs;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ToxicGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.*;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.SkeletonKey;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Grim;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.GooSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.StoneSnakeHeadSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.StoneSnakeTailSprite;
import com.moonshinepixel.moonshinepixeldungeon.sprites.YogSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BossHealthBar;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.*;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.*;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Scene;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.HashSet;

public class StoneSnake extends Mob {

	{
		spriteClass = YogSprite.class;

		HP = HT = 256;

		EXP = 20;

		state = PASSIVE;

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
	}

	public StoneSnake() {
		super();
	}
	
	public void spawnSnake() {
        HashSet<Mob> mobs = (HashSet<Mob>)(Dungeon.level.mobs.clone());
        for (Mob mob : mobs){
            if (mob instanceof StoneSnake && mob!=this){
                mob.destroy();
            }
        }
	    if (pos!=0) {
            Head head = new Head();
            head.pos = pos;
            GameScene.add(head);
            head.createTail(7);
//            pos = 0;
            notice();
        }
	}

	@Override
	protected boolean act() {
        int curHP = 0;
        HashSet<Mob> mobss = new HashSet<>();

        HashSet<Mob> mobs = (HashSet<Mob>)(Dungeon.level.mobs.clone());
        for (Mob mob : mobs)
            if (mob.properties().contains(Property.SNAKEPART)) {
                mobss.add(mob);
                if (!Dungeon.level.insideMap(mob.pos))mob.destroy();
            }

        for (Mob mob : mobss)
            curHP+=mob.HP;
        if (curHP<=0){
            die(Dungeon.hero);
        } else {
            HP = curHP;
        }

		return super.act();
	}

	@Override
	public void damage( int dmg, Object src ) {

		HashSet<Mob> fists = new HashSet<>();

		for (Mob mob : Dungeon.level.mobs)
				fists.add( mob );

		for (Mob fist : fists)
			fist.beckon( pos );

		dmg = 0;
		
		super.damage( dmg, src );


		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*0.5f);

	}
	
	@Override
	public void beckon( int cell ) {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void die( Object cause ) {

		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
		    if (mob!=this)
				mob.die( cause );
		}
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop();
		super.die( cause );
		
		yell( Messages.get(this, "defeated") );
	}
	
	@Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell( Messages.get(this, "notice") );
	}
	
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();
	static {
		
		IMMUNITIES.add( Grim.class );
		IMMUNITIES.add( Terror.class );
		IMMUNITIES.add( Amok.class );
		IMMUNITIES.add( Charm.class );
		IMMUNITIES.add( Sleep.class );
		IMMUNITIES.add( Burning.class );
		IMMUNITIES.add( ToxicGas.class );
		IMMUNITIES.add( ScrollOfPsionicBlast.class );
		IMMUNITIES.add( Vertigo.class );
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
	}
	
	public static class Head extends Mob {
		private int[]  tailID = new int[0];
		{
			spriteClass = StoneSnakeHeadSprite.class;
			
			HP = HT = 32;
			defenseSkill = 2;
			
			EXP = 0;
			
			state = HUNTING;

			properties.add(Property.SNAKEPART);
			properties.add(Property.IMMOVABLE);
		}

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) lock.addTime(dmg*0.5f);
        }

        @Override
        protected boolean act() {
		    if (!Dungeon.level.insideMap(pos))destroy();
            checkTail(tailID,this);
            return super.act();
        }

        @Override
		public int attackSkill( Char target ) {
			return 10;
		}
		
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 0, 4 );
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 3);
		}

		private final String TAIL = "tail";
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TAIL,tailID);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            tailID=bundle.getIntArray(TAIL);
        }

        @Override
        public void move( int step ) {
            super.move( step );
            checkTail(this.tailID,this);
            moveTail(tailID,this);
        }

        public void createTail(int length){
            Mob[] tail = new Mob[length];
            tailID = new int[length];
            for (int i = 0;i<length;i++){
                tail[i]=new Tail();
                ((Tail)tail[i]).snakepart=true;
                tailID[i]=tail[i].id();
                System.out.println(i+"|"+tailID[i]);
                GameScene.add( tail[i] );
            }
            allignTail(tailID, this);
        }
        public void createTail(Mob[] tail){
            tailID = new int[tail.length];
            for (int i = 0;i<tail.length;i++){
                ((Tail)tail[i]).snakepart=true;
                tailID[i]=tail[i].id();
            }
        }
        private void allignTail(int[] tail,Mob head){
            System.out.println(0+"|"+tail[0]);
            System.out.println(0+"|"+Actor.findById(tail[0]));
            ((Mob)Actor.findById(tail[0])).pos=findNear(head.pos);
//            ((Mob)Actor.findById(tail[0])).diactivate();
            for (int i = 1; i< tail.length; i++){
                Mob curTail = (Mob)Actor.findById(tail[i]);
                curTail.pos=findNear(((Mob)Actor.findById(tail[i-1])).pos);
//                GameScene.add( (Mob)Actor.findById(tail[i]) );
//                ((Mob)Actor.findById(tail[i])).diactivate();
            }
        }
        private int findNear(int cell){
            for (int o : PathFinder.CIRCLE8){
                if (Dungeon.level.isMap(cell+o)){
                    if (Level.getPassable(cell+o)||Level.getAvoid(cell+o)) {
                        if (Actor.findChar(cell + o) == null) {
                            return cell + o;
                        }
                    }
                }
            }
            return -1;
        }
        private void moveTail(int[] tail, Mob head){
            try {
                if (tail.length > 0) {
                    ((Mob)Actor.findById(tail[0])).move(head.oldPos);
                    ((Mob)Actor.findById(tail[0])).sprite.move(((Mob)Actor.findById(tail[0])).oldPos, ((Mob)Actor.findById(tail[0])).pos);
                    for (int i = 1; i < tail.length; i++) {
                        ((Mob)Actor.findById(tail[i])).move(((Mob)Actor.findById(tail[i-1])).oldPos);
                        ((Mob)Actor.findById(tail[i])).sprite.move(((Mob)Actor.findById(tail[i])).oldPos, ((Mob)Actor.findById(tail[i])).pos);
                    }
                }
            } catch (Exception e){
                MoonshinePixelDungeon.reportException(e);
            }
        }
        private void checkTail(int[] tail, Mob head){
            for (int i = 0; i<tail.length;i++){
                Mob ch = ((Mob)Actor.findById(tail[i]));
                if (ch!=null) {
                    if (!ch.isAlive()) {
                        detachTail(i);
                        return;
                    }
                } else {
                    detachTail(i);
                    return;
                }
            }
        }
        @Override
        public void die( Object cause ){
            super.die(cause);
            int[] curTail = tailID.clone();

            for (int i = 0;i<curTail.length;i++){
                if (((Mob)Actor.findById(curTail[i]))!=null){
                    if (((Mob)Actor.findById(curTail[i])).isAlive()){
                        int oldHP = ((Mob)Actor.findById(curTail[i])).HP;
                        int oldPos = ((Mob)Actor.findById(curTail[i])).pos;
                        ((Mob)Actor.findById(curTail[i])).pos=0;
                        Head newHead = new Head();
                        newHead.pos=oldPos;
                        newHead.HP=oldHP;
                        newHead.tailID=new int[curTail.length-i-1];
                        for (int j = 0;j<newHead.tailID.length;j++){
                            int k=j+i+1;
                            newHead.tailID[j]=curTail[k];
                        }
                        GameScene.add( newHead );
                        newHead.checkTail(newHead.tailID,newHead);
                        return;
                    }
                }
            }
        }
        private void detachTail(int start){
            int[] curTail = tailID.clone();
            tailID = new int[start];
            for (int i = 0; i<start;i++){
                tailID[i]=curTail[i];
            }
            for (int i = start+1;i<curTail.length;i++){
                if (((Mob)Actor.findById(curTail[i]))!=null){
                    if (((Mob)Actor.findById(curTail[i])).isAlive()){
                        int oldHP = ((Mob)Actor.findById(curTail[i])).HP;
                        int oldPos = ((Mob)Actor.findById(curTail[i])).pos;
                        ((Mob)Actor.findById(curTail[i])).sprite.killAndErase();
                        ((Mob)Actor.findById(curTail[i])).destroy();
                        Scene sc = Game.scene();
                        Head newHead = new Head();
                        newHead.pos=oldPos;
                        newHead.HP=oldHP;
                        newHead.tailID=new int[curTail.length-i-1];
                        for (int j = 0;j<newHead.tailID.length;j++){
                            int k=j+i+1;
                            newHead.tailID[j]=curTail[k];
                        }
                        GameScene.add( newHead );
                        newHead.checkTail(newHead.tailID,newHead);
                        return;
                    }
                }
            }
        }

	}
	public static class Tail extends Mob {

		{
			spriteClass = StoneSnakeTailSprite.class;

			HP = HT = 32;
			defenseSkill = 2;

			EXP = 0;

			state = HUNTING;

			properties.add(Property.SNAKEPART);
			properties.add(Property.IMMOVABLE);
		}
        public boolean snakepart=false;
		@Override
		public int attackSkill( Char target ) {
			return 0;
		}

        @Override
        public void add(Buff buff) {
        }

        @Override
        public void damage(int dmg, Object src) {
//            super.damage(dmg, src);
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) lock.addTime(dmg*0.5f);
        }

        @Override
        public void die(Object cause) {
            super.die(cause);
            if(!(cause instanceof Head)) {
                HashSet<Mob> mobs = (HashSet<Mob>) Dungeon.level.mobs.clone();
                for (Mob mob : mobs) {
                    if (mob instanceof Head) {
                        ((Head) mob).checkTail(((Head) mob).tailID, ((Head) mob));
                    }
                }
            }
        }

        @Override
        protected boolean act() {
            if (!Dungeon.level.insideMap(pos))destroy();
            diactivate();
            return true;
        }

		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 0, 0 );
		}
        protected Char chooseEnemy() {
            return null;
        }
        protected boolean getCloser( int target ) {
            return false;
        }

		@Override
		public int drRoll() {
			return Random.NormalIntRange(3, 6);
		}

	}
}
