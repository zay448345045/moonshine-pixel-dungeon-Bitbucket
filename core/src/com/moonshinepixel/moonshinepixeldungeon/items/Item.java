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
package com.moonshinepixel.moonshinepixeldungeon.items;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.moonshinepixel.moonshinepixeldungeon.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.SnipersMark;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Transformation;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.Speck;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Boomerang;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.QuickSlotButton;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Combo;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.Bag;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.MissileWeapon;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.scenes.CellSelector;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Item implements Bundlable {

	protected static final String TXT_TO_STRING_LVL		= "%s %+d";
	protected static final String TXT_TO_STRING_X		= "%s x%d";

	public static final int 	  BROKEN_COLOR			= 0xff9900;

	protected static final float TIME_TO_THROW		= 1.0f;
	protected static final float TIME_TO_PICK_UP	= 1.0f;
	protected static final float TIME_TO_DROP		= 0.5f;
	
	public static final String AC_DROP		= "DROP";
	public static final String AC_THROW		= "THROW";
	
	public String defaultAction;
	public boolean usesTargeting;
	public int knownTurns=-1;
	
	protected String name = Messages.get(this, "name");
	protected String givenName = "";
	public int image = 0;

	public boolean stackable = false;
	protected int quantity = 1;

	public int tier = 0;

	public float durability = 1f;

	private int level = 0;

	public boolean levelKnown = false;
	
	public boolean cursed;
	public boolean cursedKnown;

	public boolean destructable = false;
	
	// Unique items persist through revival
	public boolean unique = false;

	// whether an item can be included in heroes remains
	public boolean bones = false;

	public boolean isDouble = false;

	public boolean renameable = false;
	
	private static Comparator<Item> itemComparator = new Comparator<Item>() {
		@Override
		public int compare( Item lhs, Item rhs ) {
			return Generator.Category.order( lhs ) - Generator.Category.order( rhs );
		}
	};
	
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add( AC_DROP );
		actions.add( AC_THROW );
		return actions;
	}
	
	public boolean doPickUp( Hero hero ) {
		if (collect( hero.belongings.backpack )) {
			
			GameScene.pickUp( this );
			Sample.INSTANCE.play( Assets.SND_ITEM );
			hero.spendAndNext( TIME_TO_PICK_UP );
			return true;
			
		} else {
			return false;
		}
	}
	
	public void doDrop( Hero hero ) {
		hero.spendAndNext( TIME_TO_DROP );
		Dungeon.level.drop( detachAll( hero.belongings.backpack ), hero.pos ).sprite.drop( hero.pos );
	}

	//resets an item's properties, to ensure consistency between runs
	public void reset(){
		//resets the name incase the language has changed.
		name = Messages.get(this, "name");
	}

	public void doThrow( Hero hero ) {
		GameScene.selectCell( thrower );
	}
	
	public void execute( Hero hero, String action ) {
		curUser = hero;
		curItem = this;

		if (curUser.buff(Transformation.class)!=null){
			GLog.i(Messages.get(this,"cantuse"));
		}

		Combo combo = hero.buff(Combo.class);
		if (combo != null) combo.detach();
		
		if (action.equals( AC_DROP )) {
			
			doDrop( hero );
			
		} else if (action.equals( AC_THROW )) {
			
			doThrow( hero );
			
		}
	}
	
	public void execute( Hero hero ) {
		execute( hero, defaultAction );
	}
	
	public void onThrow(int cell) {
		Heap heap = Dungeon.level.drop( this, cell );
		if (!heap.isEmpty()) {
			heap.sprite.drop( cell );
		}
	}
	
	public boolean collect( Bag container ) {
		
		ArrayList<Item> items = container.items;
		
		if (items.contains( this )) {
			return true;
		}
		
		for (Item item:items) {
			if (item instanceof Bag && ((Bag)item).grab( this )) {
				return collect( (Bag)item );
			}
		}
		
		if (stackable) {
			for (Item item:items) {
				if (isSimilar( item )) {
					item.quantity += quantity;
					item.updateQuickslot();
					return true;
				}
			}
		}
		
		if (items.size() < container.size) {
			
			if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
				Badges.validateItemLevelAquired( this );
			}
			
			items.add( this );
			if (stackable || this instanceof Boomerang) Dungeon.quickslot.replaceSimilar(this);
			updateQuickslot();
			Collections.sort( items, itemComparator );
			return true;
			
		} else {
			
			GLog.n( Messages.get(Item.class, "pack_full", name()) );
			return false;
			
		}
	}

	public boolean collect() {
		return collect( Dungeon.hero.belongings.backpack );
	}

	public void give(){
		boolean collected = collect();
		if (!collected){
            Dungeon.level.drop( this, Dungeon.hero.pos ).sprite.drop( Dungeon.hero.pos );
        }
	}

	public final Item detach( Bag container ) {
	    return detach(container, 1);
	}
	public final Item detach( Bag container, int num ) {

		if (quantity <= 0) {
			return null;

		} else
		if (quantity <= num) {

			if (stackable || this instanceof Boomerang){
				Dungeon.quickslot.convertToPlaceholder(this);
			}
			return detachAll( container );

		} else {

			quantity-=num;
			updateQuickslot();

			try {

				//pssh, who needs copy constructors?
				Item detached = getClass().newInstance();
				Bundle copy = new Bundle();
				this.storeInBundle(copy);
				detached.restoreFromBundle(copy);
				detached.quantity(num);
                for (int i = 0; i < num; i++) {
                    detached.onDetach();
                }
				return detached;
			} catch (Exception e) {
				MoonshinePixelDungeon.reportException(e);
				return null;
			}
		}
	}
	
	public final Item detachAll( Bag container ) {
		Dungeon.quickslot.clearItem( this );
		updateQuickslot();

		for (Item item : container.items) {
			if (item == this) {
				container.items.remove(this);
				item.onDetach();
				return this;
			} else if (item instanceof Bag) {
				Bag bag = (Bag)item;
				if (bag.contains( this )) {
					return detachAll( bag );
				}
			}
		}
		
		return this;
	}
	
	public boolean isSimilar( Item item ) {
		return getClass() == item.getClass();
	}

	protected void onDetach(){}

	public int level(){
		return level;
	}

	public void level( int value ){
		level = value;

		updateQuickslot();
	}
	
	public Item upgrade() {

		if (cursed) {
			ScrollOfRemoveCurse.uncurse(null, this);
		}
		level(level()>0?level():-level());
		this.level++;
		durability=1;

		updateQuickslot();
		
		return this;
	}
	
	final public Item upgrade( int n ) {
		for (int i=0; i < n; i++) {
			upgrade();
		}
		
		return this;
	}
	
	public Item degrade() {
		
		this.level--;
		
		return this;
	}
	
	final public Item degrade( int n ) {
		for (int i=0; i < n; i++) {
			degrade();
		}
		
		return this;
	}
	
	public int visiblyUpgraded() {
		return levelKnown ? level : 0;
	}
	
	public boolean visiblyCursed() {
		return cursed && cursedKnown;
	}
	
	public boolean isUpgradable() {
		return true;
	}
	
	public boolean isIdentified() {
		return levelKnown && cursedKnown;
	}
	
	public boolean isEquipped( Hero hero ) {
		return false;
	}

	public Item identify() {
        knownTurns=1;
		levelKnown = true;
		cursedKnown = true;

		return this;
	}

    public Item unIdentify() {
        return this.unIdentify(true);
    }
    public Item unIdentify(boolean curse) {

        knownTurns=-1;
        levelKnown = false;
        cursedKnown = curse && cursedKnown;

        return this;
    }
    public void unIdentifyTry(int i){
	    unIdentifyTry(i, false);
    }
    public void unIdentifyTry(int i, boolean curseKnown) {
        if (knownTurns!=-1){
            if (Random.Int(6000)<knownTurns){
                unIdentify(curseKnown);
            } else {
                knownTurns+=1;
            }
        }
    }
	
	public static void evoke( Hero hero ) {
		hero.sprite.emitter().burst( Speck.factory( Speck.EVOKE ), 5 );
	}
	
	@Override
	public String toString() {

		String name = name();

		if (visiblyUpgraded() != 0)
			name = Messages.format( TXT_TO_STRING_LVL, name, visiblyUpgraded()  );

		if (quantity > 1)
			name = Messages.format( TXT_TO_STRING_X, name, quantity );

		return name;

	}
	
	public String name() {
    	String nm = broken()?Messages.get(Item.class,"brokename"):"";
    	if(givenName.equals("")) {
			nm += name;
		} else{
    		nm += givenName;
		}
		return nm;
	}

	public void rename(String name){
    	givenName=name;
	}
	
	public final String trueName() {
		return name;
	}

	public boolean hasName(){
    	return !givenName.equals("");
	}
	
	public int image() {
		return image;
	}
	
	public ItemSprite.Glowing glowing() {
		return null;
	}

	public Emitter emitter() { return null; }
	
	public String info() {
		return desc();
	}
	
	public String desc() {
		return Messages.get(this, "desc");
	}
	
	public int quantity() {
		return quantity;
	}
	
	public Item quantity( int value ) {
		quantity = value;
		return this;
	}

	public Heap drop(int cell){
		return Dungeon.level.drop(this, cell);
	}

	public int price(boolean levelKnown, boolean cursedKnown) {
		return 0;
	}
	public int price() {
		return price(levelKnown, cursedKnown);
	}
	
	public static Item virtual( Class<? extends Item> cl ) {
		try {
			
			Item item = (Item)ClassReflection.newInstance(cl);
			item.quantity = 0;
			return item;
			
		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}
	
	public Item random() {
		return this;
	}
	
	public String status() {
		return quantity != 1 ? Integer.toString( quantity ) : null;
	}
	
	public void updateQuickslot() {
			QuickSlotButton.refresh();
	}
	
	private static final String QUANTITY		= "quantity";
	private static final String LEVEL			= "level";
	private static final String LEVEL_KNOWN		= "levelKnown";
	private static final String TURNS_KNOWN		= "turnsKnown";
	private static final String CURSED			= "cursed";
	private static final String CURSED_KNOWN	= "cursedKnown";
	private static final String QUICKSLOT		= "quickslotpos";
	private static final String TIER 			= "tier";
	private static final String GIVENNAME 		= "givenname";
	private static final String DURABILITY 		= "durability";

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( QUANTITY, quantity );
		bundle.put( LEVEL, level );
		bundle.put( LEVEL_KNOWN, levelKnown );
		bundle.put( TURNS_KNOWN, knownTurns );
		bundle.put( CURSED, cursed );
		bundle.put( CURSED_KNOWN, cursedKnown );
		bundle.put( TIER, tier );
		bundle.put( DURABILITY, durability );
		if (Dungeon.quickslot.contains(this)) {
			bundle.put( QUICKSLOT, Dungeon.quickslot.getSlot(this) );
		}
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		quantity	= bundle.getInt( QUANTITY );
		levelKnown	= bundle.getBoolean( LEVEL_KNOWN );
		cursedKnown	= bundle.getBoolean( CURSED_KNOWN );

		knownTurns=bundle.getInt(TURNS_KNOWN);

		givenName = bundle.getString(GIVENNAME);

		durability=bundle.getFloat(DURABILITY);

		if (bundle.contains(TIER)) {
			tier = bundle.getInt(TIER);
		}

		int level = bundle.getInt( LEVEL );
		if (level > 0) {
			upgrade( level );
		} else if (level < 0) {
			degrade( -level );
		}
		
		cursed	= bundle.getBoolean( CURSED );

		//only want to populate slot on first load.
		if (Dungeon.hero == null) {
			if (bundle.contains(QUICKSLOT)) {
				Dungeon.quickslot.setSlot(bundle.getInt(QUICKSLOT), this);
			}
		}
	}

	public int throwPos( Hero user, int dst){
		return new Ballistica( user.pos, dst, Ballistica.PROJECTILE ).collisionPos;
	}
	
	public void cast( final Hero user, int dst ) {
		
		final int cell = throwPos( user, dst );
		user.sprite.zap( cell );
		user.busy();

		Sample.INSTANCE.play( Assets.SND_MISS, 0.6f, 0.6f, 1.5f );

		Char enemy = Actor.findChar( cell );
		QuickSlotButton.target(enemy);

		// FIXME!!!
		float delay = TIME_TO_THROW;
		if (this instanceof MissileWeapon) {
			delay *= ((MissileWeapon)this).speedFactor( user );
			if (enemy != null) {
				SnipersMark mark = user.buff( SnipersMark.class );
				if (mark != null) {
					if (mark.object == enemy.id()) {
						delay *= 0.5f;
					}
					user.remove( mark );
				}
			}
		}
		final float finalDelay = delay;

		if (enemy != null) {
			((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
					reset(user.sprite,
							enemy.sprite,
							this,
							new Callback() {
						@Override
						public void call() {
							Item.this.detach(user.belongings.backpack).onThrow(cell);
							user.spendAndNext(finalDelay);
						}
					});
		} else {
			((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
					reset(user.sprite,
							cell,
							this,
							new Callback() {
						@Override
						public void call() {
							Item.this.detach(user.belongings.backpack).onThrow(cell);
							user.spendAndNext(finalDelay);
						}
					});
		}
	}
	
	protected static Hero curUser = null;
	protected static Item curItem = null;
	protected static CellSelector.Listener thrower = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				curItem.cast( curUser, target );
			}
		}
		@Override
		public String prompt() {
			return Messages.get(Item.class, "prompt");
		}
	};

	public void invAct(){
		if (cursed&&level>0){
			level(-level);
		}
		if (!cursed&&level<0){
			level(-level);
		}
	}

	public Item damage(float durability){
		if (Dungeon.isChallenged(Challenges.RUST)) {
			if (cursed)return this;
			if (this.durability > 0) {
				this.durability = Math.max(this.durability - durability, 0);
				if (this.durability <= 0) GLog.n(Messages.get(Item.class, "broken"), name());
			}
		}
		return this;
	}

	public boolean broken(){
		return durability<=0;
	}
}
