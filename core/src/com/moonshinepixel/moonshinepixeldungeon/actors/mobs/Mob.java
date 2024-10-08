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

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.moonshinepixel.moonshinepixeldungeon.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.NPC;
import com.moonshinepixel.moonshinepixeldungeon.effects.Flare;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.effects.Wound;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.SoulVial;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfAccuracy;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.RegularLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.BlackjackShopRoom;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroSubClass;
import com.moonshinepixel.moonshinepixeldungeon.effects.Surprise;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.TimekeepersHourglass;
import com.moonshinepixel.moonshinepixeldungeon.items.grimoires.Grimoire;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfWealth;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class Mob extends Char {

	{
		actPriority = 2; //hero gets priority over mobs.
	}
	
	private static final String	TXT_DIED	= "You hear something died in the distance";
	
	protected static final String TXT_NOTICE1	= "?!";
	protected static final String TXT_RAGE		= "#$%^";
	protected static final String TXT_EXP		= "%+dEXP";

	public AiState SLEEPING     = new Sleeping();
	public AiState HUNTING		= new Hunting();
	public AiState WANDERING	= new Wandering();
	public AiState FLEEING		= new Fleeing();
	public AiState PASSIVE		= new Passive();
	public AiState state = SLEEPING;
	
	public Class<? extends CharSprite> spriteClass;
	
	protected int target = -1;
	
	protected int defenseSkill = 0;

	protected int stonesreward = 0;

	public int EXP = 1;
	public int maxLvl = Hero.MAX_LEVEL;
	
	protected Char enemy;
	protected boolean enemySeen;
	protected boolean alerted = false;

	protected static final float TIME_TO_WAKE_UP = 1f;
	
	public boolean hostile = true;
	public boolean ally = false;
	
	private static final String STATE	= "state";
	private static final String SEEN	= "seen";
	private static final String TARGET	= "target";

	protected float dangerousMod(){
		return 1;
	}

	public int danger(){
		return (int)(HT+regularDMG()*baseSpeed);
	}

	//FIXME: THIS IS BAD! Must be a better way
	public int regularDMG(){
	    int min=0;
	    int max=0;
	    int repeats = damageRoll();
	    for (int i = 0; i<repeats*20+20; i++){
	        int roll = damageRoll();
	        min = Math.min(min,roll);
	        max = Math.max(max,roll);
        }
        return (min+max)/2;
    }

    public static final String HOSTILE = "hostile";
    public static final String ALLY = "ally";

	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );

		if (state == SLEEPING) {
			bundle.put( STATE, Sleeping.TAG );
		} else if (state == WANDERING) {
			bundle.put( STATE, Wandering.TAG );
		} else if (state == HUNTING) {
			bundle.put( STATE, Hunting.TAG );
		} else if (state == FLEEING) {
			bundle.put( STATE, Fleeing.TAG );
		} else if (state == PASSIVE) {
			bundle.put( STATE, Passive.TAG );
		}
		bundle.put( SEEN, enemySeen );
		bundle.put( TARGET, target );
		bundle.put( HOSTILE, hostile);
		bundle.put( ALLY, ally );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		
		super.restoreFromBundle( bundle );

		String state = bundle.getString( STATE );
		if (state.equals( Sleeping.TAG )) {
			this.state = SLEEPING;
		} else if (state.equals( Wandering.TAG )) {
			this.state = WANDERING;
		} else if (state.equals( Hunting.TAG )) {
			this.state = HUNTING;
		} else if (state.equals( Fleeing.TAG )) {
			this.state = FLEEING;
		} else if (state.equals( Passive.TAG )) {
			this.state = PASSIVE;
		}

		enemySeen = bundle.getBoolean( SEEN );

		target = bundle.getInt( TARGET );

		hostile = bundle.getBoolean(HOSTILE);
		ally = bundle.getBoolean(ALLY);
	}
	
	public CharSprite sprite() {
		CharSprite sprite = null;
		try {
			sprite = ClassReflection.newInstance(spriteClass);
		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
		}
		return sprite;
	}
	
	@Override
	protected boolean act() {
		
		super.act();
		
		boolean justAlerted = alerted;
		alerted = false;
		if (sprite!=null)
		sprite.hideAlert();
		
		if (paralysed > 0) {
			enemySeen = false;
			spend( TICK );
			return true;
		}
		
		enemy = chooseEnemy();
		if (enemy==Dungeon.hero&&Dungeon.level.alerted&&Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)){
			Dungeon.level.alertAll();
		}
		
		boolean enemyInFOV = enemy != null && enemy.isAlive() && Level.fieldOfView[enemy.pos] && enemy.invisible <= 0;

		return state.act( enemyInFOV, justAlerted );
	}
	
	protected Char chooseEnemy() {

		Terror terror = buff( Terror.class );
		if (terror != null) {
			Char source = (Char) Actor.findById( terror.object );
			if (source != null) {
				return source;
			}
		}

		//find a new enemy if..
		boolean newEnemy = false;
		//we have no enemy, or the current one is dead
		if ( enemy == null || !enemy.isAlive() || state == WANDERING)
			newEnemy = true;
		else {
            //We are corrupted, and current enemy is either the hero or another corrupted character.
            if (buff(Corruption.class) != null && (enemy == Dungeon.hero || enemy.buff(Corruption.class) != null))
                newEnemy = true;
            //We are amoked and current enemy is the hero
            if (buff(Amok.class) != null && enemy == Dungeon.hero)
                newEnemy = true;
            if (ally && (enemy == Dungeon.hero || enemy.buff(Corruption.class) != null || ((Mob) enemy).ally))
                newEnemy = true;

            boolean allyFound = true;
            for (Mob mob : Dungeon.level.mobs)
                if (mob != this && Level.fieldOfView[mob.pos] && mob.ally)
                    allyFound = true;
        }
        if (!ally && buff(Corruption.class) == null && enemy == Dungeon.hero)
            newEnemy=true;


		if ( newEnemy ) {

			HashSet<Char> enemies = new HashSet<>();

			//if the mob is corrupted...
			if ( buff(Corruption.class) != null) {

				//look for enemy mobs to attack, which are also not corrupted
				for (Mob mob : Dungeon.level.mobs)
					if (mob != this && Level.fieldOfView[mob.pos] && mob.buff(Corruption.class) == null && !mob.ally)
						enemies.add(mob);
				if (enemies.size() > 0) return Random.element(enemies);

				//otherwise go for nothing
				return null;

			} else if (ally){
                    for (Mob mob : Dungeon.level.mobs) {
                        if (Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE && !mob.ally) {
                            enemies.add(mob);
                        }
                    }
                    return enemies.size() > 0 ? Random.element(enemies) : null;

                //if the mob is amoked...
            } else if ( buff(Amok.class) != null) {

				//try to find an enemy mob to attack first.
				for (Mob mob : Dungeon.level.mobs)
					if (mob != this && Level.fieldOfView[mob.pos])
							enemies.add(mob);
				if (enemies.size() > 0) return Random.element(enemies);

				//try to find ally mobs to attack second.
				for (Mob mob : Dungeon.level.mobs)
					if (mob != this && Level.fieldOfView[mob.pos] && mob.ally)
						enemies.add(mob);
				if (enemies.size() > 0) return Random.element(enemies);

				//if there is nothing, go for the hero
				else return Dungeon.hero;

			} else {

				//try to find ally mobs to attack.
				for (Mob mob : Dungeon.level.mobs)
					if (mob != this && Level.fieldOfView[mob.pos] && mob.ally)
						enemies.add(mob);

				//and add the hero to the list of targets.
				enemies.add(Dungeon.hero);

				//target one at random.
				return Random.element(enemies);

			}

		} else
			return enemy;
	}

	public boolean moveSprite( int from, int to ) {

		try {
			if (sprite.isVisible() && (Dungeon.visible[from] || Dungeon.visible[to])) {
				sprite.move(from, to);
				return true;
			} else {
				sprite.place(to);
				return true;
			}
		} catch (NullPointerException e){
			return true;
		}
	}
	
	@Override
	public void add( Buff buff ) {
		super.add( buff );
		if (buff instanceof Amok) {
			if (sprite != null) {
				sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "rage") );
			}
			state = HUNTING;
		} else if (buff instanceof Terror) {
			state = FLEEING;
		} else if (buff instanceof Sleep) {
			state = SLEEPING;
			this.sprite().showSleep();
			postpone( Sleep.SWS );
		}
	}
	
	@Override
	public void remove( Buff buff ) {
		super.remove( buff );
		if (buff instanceof Terror) {
			if(sprite!=null)
				sprite.showStatus( CharSprite.NEGATIVE, Messages.get(this, "rage") );
			state = HUNTING;
		}
	}
	
	public boolean canAttack(Char enemy) {
		return Dungeon.level.adjacent( pos, enemy.pos );
	}


	protected boolean getCloser( int target ) {
	    if (ally){
	        boolean followhero = false;
	        boolean[] pass = Level.getPassable().clone();
	        for (Char ch : Dungeon.level.mobs){
	            if (ch!=this)
	            pass[ch.pos]=false;
            }
            pass[Dungeon.hero.pos]=false;
            PathFinder.buildDistanceMap(pos, pass);
            if (state == WANDERING || !Dungeon.visible[target] || PathFinder.distance[target] > Dungeon.level.distance(target, Dungeon.hero.pos)*2) {
                this.target = target = Dungeon.hero.pos;
                followhero=true;
            }
            if (followhero) {
                PathFinder.Path p = Dungeon.findPath(this, pos, target, Level.getPassable(), Level.fieldOfView);
                if (p != null) if (p.size() > 7) p = null;
                if (p == null) {
                    HashMap<Mob, Integer> allies = new HashMap<>();
                    for (Mob m : Dungeon.level.mobs) {
                        if (m.ally && Dungeon.findPath(this, pos, m.pos, Level.getPassable(), Level.fieldOfView) != null)
                            allies.put(m, PathFinder.find(m.pos, Dungeon.hero.pos, Level.getPassable()).size());
                    }
                    if (!allies.isEmpty()) {
                        Mob tmob = this;
                        int minDist = Integer.MAX_VALUE;
                        for (Map.Entry entry : allies.entrySet()) {
                            if ((int) (entry.getValue()) < minDist) {
                                tmob = (Mob) entry.getKey();
                                minDist = (int) entry.getValue();
                            }
                        }
                        this.target = target = tmob.pos;
                    }
                }
            }
        }
		
		if (rooted || target == pos) {
			return false;
		}

		int step = -1;

		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (Actor.findChar( target ) == null && Level.getPassable(target)) {
				step = target;
			}

		} else {

			boolean newPath = false;
			//scrap the current path if it's empty, no longer connects to the current location
			//or if it's extremely inefficient and checking again may result in a much better path
			if (path == null || path.isEmpty()
					|| !Dungeon.level.adjacent(pos, path.getFirst())
					|| path.size() >= 2*Dungeon.level.distance(pos, target))
				newPath = true;
			else if (path.getLast() != target) {
				//if the new target is adjacent to the end of the path, adjust for that
				//rather than scrapping the whole path.
				if (Dungeon.level.adjacent(target, path.getLast())) {
					int last = path.removeLast();

					if (path.isEmpty()) {

						//shorten for a closer one
						if (Dungeon.level.adjacent(target, pos)) {
							path.add(target);
							//extend the path for a further target
						} else {
							path.add(last);
							path.add(target);
						}

					} else if (!path.isEmpty()) {
						//if the new target is simply 1 earlier in the path shorten the path
						if (path.getLast() == target) {

							//if the new target is closer/same, need to modify end of path
						} else if (Dungeon.level.adjacent(target, path.getLast())) {
							path.add(target);

							//if the new target is further away, need to extend the path
						} else {
							path.add(last);
							path.add(target);
						}
					}

				} else {
					newPath = true;
				}

			}


			if (!newPath) {
				//looks ahead for path validity, up to length-1 or 4, but always at least 1.
				int lookAhead = (int)GameMath.gate(1, path.size()-1, 4);
				for (int i = 0; i < lookAhead; i++) {
					int cell = path.get(i);
					if (!Level.getPassable(cell) || ( Level.fieldOfView[cell] && Actor.findChar(cell) != null)) {
						newPath = true;
						break;
					}
				}
			}

			if (newPath) {
				path = Dungeon.findPath(this, pos, target,
                        Level.getPassable(),
						Level.fieldOfView);
			}

			if (path == null)
				return false;

			step = path.removeFirst();
		}
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	public void clearEnemy(){
		enemy=null;
		enemySeen=false;
	}

	protected boolean getFurther( int target ) {
		int step = Dungeon.flee( this, pos, target,
                Level.getPassable(),
			Level.fieldOfView );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null)
			sprite.add( CharSprite.State.PARALYSED );
	}

	@Override
	public void move( int step ) {
		super.move( step );
		
		if (!flying) {
			Dungeon.level.mobPress( this );
		}
	}
	
	public float attackDelay() {
		return 1f;
	}
	
	public boolean doAttack(Char enemy) {
		
		boolean visible = Dungeon.visible[pos];
		
		if (sprite!=null&&visible) {
			sprite.attack( enemy.pos );
		} else {
			attack( enemy );
		}
				
		spend( attackDelay() );
		
		return !visible;
	}
	
	@Override
	public void onAttackComplete() {
		attack( enemy );
		super.onAttackComplete();
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (buff(Weakness.class) != null){
			damage *= 0.67f;
		}
		return damage;
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		boolean seen = enemySeen || (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack());
		if (seen && paralysed == 0) {
			int defenseSkill = this.defenseSkill;
			int penalty = RingOfAccuracy.getBonus(enemy, RingOfAccuracy.Accuracy.class);
			if (penalty != 0 && enemy == Dungeon.hero)
				defenseSkill *= Math.pow(0.75, penalty);
			return defenseSkill;
		} else {
			return 0;
		}
	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {
		if (!enemySeen && enemy == Dungeon.hero && Dungeon.hero.canSurpriseAttack()) {
			if (((Hero)enemy).subClass == HeroSubClass.ASSASSIN) {
				damage *= 1.25f;
				Wound.hit(this);
			} else {
				Surprise.hit(this);
			}
		}

		//become aggro'd by a corrupted enemy
		if (enemy.buff(Corruption.class) != null) {
			aggro(enemy);
			target = enemy.pos;
			if (state == SLEEPING || state == WANDERING)
				state = HUNTING;
		}

		if (buff(SoulMark.class) != null) {
			int restoration = Math.min(damage, HP);
			Dungeon.hero.buff(Hunger.class).satisfy(restoration*0.5f);
			Dungeon.hero.HP = (int)Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP+(restoration*0.25f)));
			Dungeon.hero.sprite.emitter().burst( Speck.factory(Speck.HEALING), 1 );
		}

		return damage;
	}

	public boolean surprisedBy( Char enemy ){
		return !enemySeen && enemy == Dungeon.hero;
	}

	public void aggro( Char ch ) {
		enemy = ch;
		if (state != PASSIVE){
			state = HUNTING;
		}
	}

	@Override
	public void damage( int dmg, Object src ) {

		Terror.recover( this );

		if (state == SLEEPING) {
			state = WANDERING;
		}
		alerted = true;
		if (src==Dungeon.hero&&Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE))
			Dungeon.level.alertAll();
		
		super.damage( dmg, src );
	}
	
	
	@Override
	public void destroy() {

		boolean clear = Dungeon.level.cleared();

		super.destroy();

		Dungeon.level.mobs.remove( this );

		if (Dungeon.level instanceof RegularLevel&&Dungeon.isChallenged(Challenges.LOCKED)){
			RegularLevel lvl = (RegularLevel)Dungeon.level;
			Room r = lvl.room(pos);
			boolean unseal = true;
			if (r!=null&&!(r instanceof BlackjackShopRoom)&&r.sealed&&r.inside(new Point(PathFinder.pos2x(pos),PathFinder.pos2y(pos)))){
				iter: for(Point p:r.getPoints()){
					int c = lvl.pointToCell(p);
					Char chr=Char.findChar(c);
					if (chr!=null&&chr instanceof Mob && !(chr instanceof NPC)&&r.inside(new Point(PathFinder.pos2x(chr.pos),PathFinder.pos2y(chr.pos)))){
						unseal=false;
						break iter;
					}
				}
				if (unseal){
					r.unseal();
				}
			}
		}

		if (Dungeon.hero.isAlive()) {
			if (hostile) {
				Statistics.enemiesSlain++;
				Badges.validateMonstersSlain();
				Statistics.qualifiedForNoKilling = false;
				
				if (Dungeon.level.feeling == Level.Feeling.DARK) {
					Statistics.nightHunt++;
				} else {
					Statistics.nightHunt = 0;
				}
				Badges.validateNightHunter();
				int exp = exp();
				if (exp > 0) {
					Dungeon.hero.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "exp", exp) );
					Dungeon.hero.earnExp( exp );
					SoulVial sv = Dungeon.hero.belongings.getItem(SoulVial.class);
					if (sv!=null) {
						sv.collectSoul(this);
					}
				}
			}
            for (Item item : Dungeon.hero.belongings){
			    if (!ally) {
                    if (item instanceof Grimoire) {
                        Grimoire grim = (Grimoire) item;
                        grim.validateMobKill(this);
                    }
                }
            }
            if (clear!=Dungeon.level.cleared()){
            	GLog.h(Messages.get(this,"cleared"));
			}
		}
	}

	public int exp() {
		return Dungeon.hero.lvl <= maxLvl ? EXP : 0;
	}
	public int realexp() {
		return EXP;
	}
	
	@Override
	public void die( Object cause ) {
		
		super.die( cause );

		float lootChance = this.lootChance;
		lootChance *= RingOfWealth.dropChanceMultiplier( Dungeon.hero );

		if (!Dungeon.cheated()) {
			MoonshinePixelDungeon.moonstones(MoonshinePixelDungeon.moonstones() + stonesreward);
		}

		if (stonesreward>0){
			GLog.h( Messages.get(Mob.class, "stones",stonesreward) );
		}

		if (Random.Float() < lootChance && Dungeon.hero.lvl <= maxLvl + 2) {
			Item loot = createLoot();
			if (loot != null)
				Dungeon.level.drop( loot , pos ).sprite.drop();
		}
		
		if (Dungeon.hero.isAlive() && !Dungeon.visible[pos]) {
			GLog.i( Messages.get(this, "died") );
		}

		if (hostile && !ally && Dungeon.hero.lvl <= maxLvl + 2){
			int rolls = 1;
			if (properties.contains(Property.BOSS))             rolls = 15;
			else if (properties.contains(Property.MINIBOSS))    rolls = 5;
			ArrayList<Item> bonus = RingOfWealth.tryRareDrop(Dungeon.hero, rolls);
			if (bonus != null){
				for (Item b : bonus) Dungeon.level.drop( b , pos ).sprite.drop();
				new Flare(8, 32).color(0xFFFF00, true).show(sprite, 2f);
			}
		}

		Actor respawner = Dungeon.level.respawner();
		if (respawner!=null&&Dungeon.isChallenged(Challenges.RAPID)){
			if (respawner.cooldown()>0)respawner.enable();
			respawner.spend(-1);
		}
	}
	
	protected Object loot = null;
	protected float lootChance = 0;
	
	@SuppressWarnings("unchecked")
	protected Item createLoot() {
		Item item;
		if (loot instanceof Generator.Category) {

			item = Generator.random( (Generator.Category)loot );

		} else if (loot instanceof Class<?>) {

			item = Generator.random( (Class<? extends Item>)loot );

		} else {

			item = (Item)loot;

		}
		return item;
	}
	
	public boolean reset() {
		return false;
	}
	
	public void beckon( int cell ) {
		
		notice();
		
		if (state != HUNTING) {
			state = WANDERING;
		}
		target = cell;
	}
	
	public String description() {
		return Messages.get(this, "desc");
	}
	
	public void notice() {
		if (sprite!=null)
			sprite.showAlert();
	}
	
	public void yell( String str ) {
		GLog.n( "%s: \"%s\" ", Messages.titleCase(name), str );
	}

	//returns true when a mob sees the hero, and is currently targeting them.
	public boolean focusingHero() {
		return enemySeen && (target == Dungeon.hero.pos);
	}

	public interface AiState {
		boolean act( boolean enemyInFOV, boolean justAlerted );
		String status();
	}

	protected class Sleeping implements AiState {

		public static final String TAG	= "SLEEPING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && Random.Int( distance( enemy ) + enemy.stealth() + (enemy.flying ? 2 : 0) ) == 0) {

				enemySeen = true;

				notice();
				state = HUNTING;
				target = enemy.pos;

				if (Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
					for (Mob mob : Dungeon.level.mobs) {
						if (mob != Mob.this) {
							mob.beckon( target );
						}
					}
					Dungeon.level.alerted=true;
				}

				spend( TIME_TO_WAKE_UP );

			} else {

				enemySeen = false;

				spend( TICK );

			}
			return true;
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name );
		}
	}

	protected class Wandering implements AiState {

		public static final String TAG	= "WANDERING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && (justAlerted || Random.Int( (distance( enemy ) / 2 + enemy.stealth())*(buff(Blindness.class)!=null?10:1) ) == 0 || Dungeon.level.alerted)) {

				enemySeen = true;

				notice();
				state = HUNTING;
				target = enemy.pos;

			} else {

				enemySeen = false;

				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					target = Dungeon.level.randomDestination();
					spend( TICK );
				}

			}
			return true;
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name );
		}
	}

	protected class Hunting implements AiState {

		public static final String TAG	= "HUNTING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				}

				int oldPos = pos;
				if (target != -1 && getCloser( target )) {

					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {

					spend( TICK );
					state = WANDERING;
					target = Dungeon.level.randomDestination();
					return true;
				}
			}
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name );
		}
	}

	protected class Fleeing implements AiState {

		public static final String TAG	= "FLEEING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			//loses target when 0-dist rolls a 6 or greater.
			if (enemy == null || (!enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6 && !Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE))){
				target = -1;
			} else {
				target = enemy.pos;
			}

			int oldPos = pos;
			if (target != -1 && getFurther( target )) {

				spend( 1 / speed() );
				return moveSprite( oldPos, pos );

			} else {

				spend( TICK );
				nowhereToRun();

				return true;
			}
		}

		protected void nowhereToRun() {
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name );
		}
	}

	protected class Passive implements AiState {

		public static final String TAG	= "PASSIVE";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = false;
			spend( TICK );
			return true;
		}

		@Override
		public String status() {
			return Messages.get(this, "status", name );
		}
	}
}

