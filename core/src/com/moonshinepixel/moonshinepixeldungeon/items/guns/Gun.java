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
package com.moonshinepixel.moonshinepixeldungeon.items.guns;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.SmokeGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Buff;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Invisibility;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.attachments.*;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.DummyBullet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.GunslingerBullet;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.QuickSlotButton;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Vertigo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.scenes.CellSelector;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndBag;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;
import com.watabou.utils.Random;

import java.util.*;

public abstract class Gun extends Weapon {

	{
		if (!(this instanceof GunslingerPistol)) {
			attachment = Attachment.random();
		}
	}

	private static final int USAGES_TO_KNOW    = 5;

	public static final String AC_SHOT	= "SHOT";

	public static final String AC_RELOAD	= "RELOAD";

	public static final String AC_CHOOSE	= "CHOOSE";

    protected static float TIME_TO_SHOT	= 1f;
    public float shotTime(){
        return TIME_TO_SHOT;
    }
    public float realShotTime(){
        float time = shotTime();
        if (attachment!=null){
            time/=attachment.modifiers()[2];
        }
        return time;
    }

	protected static float TIME_TO_RELOAD	= 1f;
	public float reloadTime(){
		return TIME_TO_RELOAD;
	}

    public float realReloadTime(){
        float time = reloadTime();
        if (attachment!=null){
            time/=attachment.modifiers()[1];
        }
        return time;
    }

	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;
	public float partialCharge = 0f;
    public abstract String ammoType();
    public Load _load = new Load(initialCharges(), ammoType());
    private Ammo ammoItem;
    public Attachment attachment;
	public Class<? extends Ammo> getAmmoClass() {
		return ammoClass;
	}

	public void setAmmoClass(Class<? extends Ammo> ammoClass) {
		this.ammoClass = ammoClass;
	}

	private Class<? extends Ammo> ammoClass = defaultAmmoClass();
	protected abstract Class<? extends Ammo> defaultAmmoClass();
	public abstract float acu();

	protected static float shake = 0;
	
	private boolean curChargeKnown = false;

	protected int usagesToKnow = USAGES_TO_KNOW;
	
	{
		defaultAction = AC_SHOT;
		usesTargeting = true;
	}


    @Override
    public int proc(Char attacker, Char defender, int damage ) {
		if (Random.Int(2)==0)damage(.02f);
        return damage;
    }


    @Override
    public int damageRoll( Hero hero ) {

        int damage = super.damageRoll( hero );

        if (hero.heroClass == HeroClass.GUNSLINGER) {
            int exStr = hero.STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange( exStr/2, exStr );
            }
        } else {
            int exStr = hero.STR() - STRReq();
            if (exStr > 0) {
                damage += Random.IntRange( 0, exStr*3/4 );
            }
        }
        if (attachment!=null){
            damage*=Math.round(Random.Float(attachment.modifiers()[3],attachment.modifiers()[4]));
        }
        return imbue.damageFactor(damage);
    }

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
            actions.add(AC_SHOT);
            actions.add(AC_RELOAD);
            actions.add(AC_CHOOSE);
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );
		if(curUser.ready) {
			if (this.isEquipped(hero)) {
				if (action.equals(AC_SHOT)) {
					Item.curUser = hero;
					Item.curItem = this;
					if (this._load.curLoad() > 0 || !curChargeKnown) {
						GameScene.selectCell(zapper);
					} else if (this._load.maxLoad() > 0) {
						QuickSlotButton.cancel();
						action = AC_RELOAD;
					}
				}
				if (action.equals(AC_RELOAD)) {
					if (this._load.curLoad() < this._load.maxLoad()) {
						Item.curUser = hero;
						Item.curItem = this;
						reload();
					} else {
						GLog.w(Messages.get(this, "already_loaded"));
					}
				}
				if (action.equals(AC_CHOOSE)) {
					QuickSlotButton.cancel();
					chooseAmmoItem();
				}
			} else {
				GLog.w(Messages.get(this, "not_equipped"));
				QuickSlotButton.cancel();
			}
		} else {
			QuickSlotButton.cancel();
		}
		updateQuickslot();
	}

	public void reload(){
		reload(1);
	}

    protected WndBag.Mode mode = WndBag.Mode.GUNAMMO;
	public void reload(int charges){
		Item.curUser= Dungeon.hero;
	    try {
	        if (this.ammoClass!=null) {
                for (int i = 0; i < charges; i++) {
                    Ammo amo = Item.curUser.belongings.getItem(this.ammoClass);
                    if (amo != null) {
                        if (amo.quantity() > 0) {
                            if (this._load.curLoad() < this._load.maxLoad()) {
                                this._load.add(amo.getClass());
                                amo.detach(Dungeon.hero.belongings.backpack);
                                Item.curUser.sprite.operate(Item.curUser.pos);
								Item.curUser.busy();
                                Item.curUser.spendAndNext(realReloadTime()* Item.curUser.timeToReloadMod());
                            } else break;
                        } else {
                            chooseAmmoItem();
                        }
                    } else {
                        chooseAmmoItem();
                    }
                }
            } else {
	            chooseAmmoItem();
            }
        } catch (Exception e){
            MoonshinePixelDungeon.reportException(e);
            e.printStackTrace();
        }
		updateQuickslot();
	}
    public void chooseAmmoItem(){
        GameScene.selectItem(itemSelector, mode, Messages.get(this, "ammo_choose"));
    }
    public abstract void onZap(Ballistica attack, Class<? extends Ammo> ammo);


	
	public void level( int value) {
		super.level( value );
		updateLevel();
	}

    @Override
    public Item identify() {

        curChargeKnown = true;
        super.identify();

        updateQuickslot();

        return this;
    }
    @Override
    public Item unIdentify() {

        curChargeKnown = false;
        super.unIdentify();

        updateQuickslot();

        return this;
    }

	@Override
	public String info() {
		String desc = desc();

		desc += "\n\n" + statsDesc();
		if(isIdentified()||(cursed&&cursedKnown)) {
			if (attachment != null) {
				if (!attachment.curse()) {
					desc += "\n\n" + Messages.get(Gun.class, "attachment", attachment.name());
					desc += "\n" + attachment.desc();
				} else {
					desc += "\n\n" + Messages.get(Gun.class, "cursedattachment", attachment.name());
					desc += "\n" + attachment.desc();
				}
			}
		}
		if (cursed && cursedKnown)
			desc += "\n\n" + Messages.get(Gun.class, "cursed");

		desc+=(broken()?"\n"+Messages.get(Item.class,"brokendesc"):"");
		return desc;
	}

	public String statsDesc(){

        if (levelKnown)
            return Messages.get(this, "stats_desc", minWnd(), maxWnd(), min(), max());
        else
            return Messages.get(this, "stats_desc", minWnd(0), maxWnd(0), min(0), max(0));
	}
	
	@Override
	public boolean isIdentified() {
		return super.isIdentified() && curChargeKnown;
	}
	
	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? _load.curLoad() : "?") + "/" + _load.maxLoad();
		} else {
			return null;
		}
	}
	
	@Override
	public Item upgrade() {

		super.upgrade();

		if (Random.Float() > Math.pow(0.9, level())) {
            cursed = false;
            if (attachment!=null) {
				attachment.cursed = false;
			}
            Item itm = this;
            itm.level(itm.level() > 0 ? itm.level() : -itm.level());
        }

		updateLevel();
		//curCharges = Math.min( curCharges + 1, maxCharges );
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public Item degrade() {
		super.degrade();
		
		updateLevel();
		updateQuickslot();
		
		return this;
	}
	
	public void updateLevel() {
		//maxCharges = Math.min( initialCharges() + level(), 10 );
		//curCharges = Math.min( curCharges, maxCharges );
	}
	
	protected int initialCharges() {
		return 1;
	}

    public int chargesPerCast() {
        return 1;
    }
    public int shootsPerCast() {
        return 1;
    }
	
	protected void fx( Ballistica bolt, Callback callback ) {
        final int cell = bolt.collisionPos;

        Char enemy = Actor.findChar( cell );

        Item proto = new Bullet();
        //proto.image=ItemSpriteSheet.BULLET;
        if (enemy!=null){
            ((MissileSprite) Item.curUser.sprite.parent.recycle( MissileSprite.class )).
                    reset( Item.curUser.pos, enemy.pos, proto, callback );
        } else {
            ((MissileSprite) Item.curUser.sprite.parent.recycle(MissileSprite.class)).
                    reset(Item.curUser.pos, cell, proto, callback);
        }

		Sample.INSTANCE.play( Assets.SND_BLAST );
	}

	public void wandUsed() {
		usagesToKnow -= cursed ? 1 : chargesPerCast();

		if (Random.Int(2)==0)damage(.05f);

		if (!isIdentified() && usagesToKnow <= 0) {
			identify();
			GLog.w( Messages.get(Gun.class, "identify", name()) );
		} else {
			if (Item.curUser.heroClass == HeroClass.GUNSLINGER) levelKnown = true;
			updateQuickslot();
		}

		Item.curUser.spendAndNext( Math.max((realShotTime() * Item.curUser.timeToShootMod()),1.5f) );
	}
	
	@Override
	public Item random() {
		int n = 0;

		if (Random.Int(3) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}

		if (Random.Float() < 0.3f || Dungeon.isChallenged(Challenges.CURSE)) {
			cursed = true;
			cursedKnown = false;
			enchant(Attachment.randomCurse());
		} else {
		    enchant(Attachment.random());
        }
        if (!cursed){ upgrade(n); } else degrade(n);
		this._load.fill(Bullet.class);

		return this;
	}
	
	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		int price = 75;
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level() > 0) {
				price *= (level() + 1);
			} else if (level() < 0) {
				price /= (1 - level());
			}
		}
		if (attachment!=null){
		    if (!attachment.curse()){
		        price *= 2;
            }
        }
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	private static final String UNFAMILIRIARITY     = "unfamiliarity";
	private static final String CUR_CHARGES			= "curCharges";
	private static final String MAX_CHARGES			= "maxCharges";
	private static final String AMMO_LOAD			= "ammo_load";
	private static final String ATTACHMENT			= "attachment";
	private static final String AMMO_CLASS			= "ammo_class";
	private static final String CUR_CHARGE_KNOWN	= "curChargeKnown";
	private static final String PARTIALCHARGE 		= "partialCharge";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
	    Class<? extends Ammo>[] ammoLoad = this._load.getLoadArrray();
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, usagesToKnow );
		bundle.put( AMMO_LOAD, ammoLoad);
		bundle.put( AMMO_CLASS, ammoClass);
		bundle.put( CUR_CHARGES, this._load.curLoad());
		bundle.put( MAX_CHARGES, this.maxCharges);
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
		bundle.put( PARTIALCHARGE , partialCharge );
		bundle.put( ATTACHMENT, attachment);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((usagesToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			usagesToKnow = USAGES_TO_KNOW;
		}
        Class<? extends Ammo>[] ammoLoad=bundle.getClassArray(AMMO_LOAD);
		this._load=new Load(ammoLoad,ammoType());
		curCharges = bundle.getInt( CUR_CHARGES );
		maxCharges = bundle.getInt( MAX_CHARGES );
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
		partialCharge = bundle.getFloat( PARTIALCHARGE );
		ammoClass=bundle.getClass(AMMO_CLASS);
		attachment=(Attachment)bundle.get(ATTACHMENT);
		enchantment=null;
	}
	
	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {
		
		@Override
		public void onSelect( Integer target ) {
			
			if (target != null) {

				final Gun curGun = (Gun) Gun.curItem;

				if (Item.curUser.buff(Vertigo.class)!=null)
					target+=PathFinder.NEIGHBOURS8[Random.Int(8)];

                if (shake > 1f)
                    Camera.main.shake( GameMath.gate( 1, shake, 5), 0.3f );
                Ammo _ammo;
                try{
                    _ammo=curGun._load.getLoadArrray()[0].newInstance();
                } catch (Exception e){
                    MoonshinePixelDungeon.reportException(e);
                    e.printStackTrace();
                    _ammo=new Bullet();
                }
				final Ballistica shot = new Ballistica( Item.curUser.pos, target, _ammo.ballisticaPropeties());
				int cell = shot.collisionPos;
				if (target == Item.curUser.pos || cell == Item.curUser.pos) {
					GLog.i( Messages.get(Gun.class, "self_target") );
					return;
				}
				Item.curUser.sprite.zap(cell);

				//attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
				if (Actor.findChar(target) != null)
					QuickSlotButton.target(Actor.findChar(target));
				else
					QuickSlotButton.target(Actor.findChar(cell));
				boolean spAmmo = false;
				if (curGun._load.getLoadArrray()[0]== GunslingerBullet.class) spAmmo=true;
				if (curGun._load.curLoad() >= curGun.chargesPerCast() || spAmmo) {
					
					Item.curUser.busy();

					if (curGun.cursed && Random.Boolean()){
						//CursedWand.cursedZap(curGun, curUser, new Ballistica( curUser.pos, target, Ballistica.MAGIC_BOLT));
						curGun.cursedZap(shot, Item.curUser,curGun);
						if (!curGun.cursedKnown){
							curGun.cursedKnown = true;
							GLog.n(Messages.get(Gun.class, "curse_discover", curGun.name()));
						}
					}
					int chance = Random.Int(100);
					if(curGun.attachment!=null){
					    chance*=curGun.attachment.failChanceMod();
                    }
					if( chance>10) {
                            Item.curUser.sprite.burst(0x69697F, 5);
							final Class<? extends Ammo> ammo = curGun._load.get();
							Ammo rlAmmo;
							try {
							    rlAmmo = ammo.newInstance();
                            } catch (Exception e){
							    MoonshinePixelDungeon.reportException(e);
							    e.printStackTrace();
							    rlAmmo=new Bullet();
                            }
                            int shots = spAmmo?1:curGun.shootsPerCast();
                        for (int i=0;i<shots;i++) {
							Dungeon.spark(Item.curUser.pos);
							if (curGun.attachment!=null)
                            	curGun.attachment.onStartShoot(shot,ammo,curGun, Item.curUser);
							if (i==shots-1) {
								rlAmmo.fx(shot, new Callback() {
									public void call() {
										curGun.wandUsed();
										curGun.onZap(shot, ammo);
										if (curGun.attachment!=null){
                                        }
									}
								});
							} else {
								rlAmmo.fx(shot, new Callback() {
									public void call() {
										curGun.onZap(shot, ammo);
									}
								});
                                if (curGun.attachment!=null){
                                }
							}
						}
                        int load = spAmmo?1:curGun.chargesPerCast();
                        for (int i=1;i<load;i++){
                            curGun._load.remove(0);
                        }
					} else {

                        GLog.w( Messages.get(Gun.class, "fizzles") );
                        Item.curUser.spendAndNext(curGun.realShotTime()*1.5f);
//                        curUser.ready();

                    }
					
					Invisibility.dispel();
					
				} else {

					GLog.w( Messages.get(Gun.class, "fizzles") );
					Item.curUser.spendAndNext(curGun.realShotTime()*1.5f);

				}
				
			}
		}
		
		@Override
		public String prompt() {
			return Messages.get(Gun.class, "prompt");
		}
	};

    public int minWnd(){
        return minWnd(broken()?0:level());
    }

    public abstract int minWnd(int lvl);

    public int maxWnd(){
        return maxWnd(broken()?0:level());
    }

    public abstract int maxWnd(int lvl);

    public int damageRoll(){
        return Random.NormalIntRange(minWnd(), maxWnd());
    }

    public int damageRoll(int lvl){
        return Random.NormalIntRange(minWnd(lvl), maxWnd(lvl));
    }


    @Override
    public Weapon enchant( Enchantment ench ) {
		enchantment=null;
    	if (ench!=null) {
			if (ench.curse()) {
				return enchant(Attachment.randomCurse());
			} else {
				return enchant(Attachment.random());
			}
		} else {
			return enchant(Attachment.random());
		}
    }
    public Weapon enchant( Attachment ench ) {
        attachment = ench;
        Class<? extends Ammo>[] ammos = _load.resize(ench.loadMod(_load, this));
        for (Class<? extends Ammo> ammo : ammos){
            try {
                Item item = ammo.newInstance();
                item.give();
            } catch (Exception e){

            }
        }
        enchantment=null;
        return this;
    }
    @Override
    public Weapon enchant() {
		enchantment=null;

        Class<? extends Attachment> oldEnchantment = attachment != null ? attachment.getClass() : null;
        Attachment ench = Attachment.random();
        while (ench.getClass() == oldEnchantment) {
            ench = Attachment.random();
        }

        return enchant( ench );
    }

    public boolean hasEnchant(Class<?extends Enchantment> type) {
        return false;
    }

    public boolean hasAttachment(Class<?extends Attachment> type) {
        return attachment != null && attachment.getClass() == type;
    }
    @Override
    public boolean hasGoodEnchant(){
        return attachment != null && !attachment.curse();
    }

    public boolean hasCurseEnchant(){		return attachment != null && attachment.curse();
    }


    public class Load{
        Class clas;
        private Class<? extends Ammo>[] loadArr;
        private String type = "";
        public Load(int size, String type){
            loadArr = new Class[size];
            Arrays.fill(loadArr, null);
            this.type = type;
        }
        public Load(Class<? extends Ammo>[] array, String type){
            loadArr=array;
            this.type = type;
        }

        public int maxLoad() {
        	try {
				return loadArr.length;
			} catch(Exception e){
        		return maxCharges;
			}
        }


        public int curLoad(){
            try {
                int load = 0;
                for (int i = 0; i < maxLoad(); i++) {
                    if (loadArr[i] != DummyBullet.class) {
                        load++;
                    }
                }
                return load;
            } catch (Exception e){
                return curCharges;
            }
        }
        public void add(Class<? extends Ammo> ammo){
            int load = curLoad();
            Object obj;
            try {
                obj = ammo.newInstance();
                if (((Ammo)obj).getAmmoType()==type)
                    if (load<maxLoad()){
                        loadArr[load]=ammo;
                    }
            }
            catch (Exception e){
            }
        }
        public void addDown(Class<? extends Ammo> ammo){
            int load = curLoad();
            Object obj;
            try {
                obj = ammo.newInstance();
                if (((Ammo)obj).getAmmoType()==type)
                    if (load<maxLoad()){
                        for (int i = load-1;i>=0;i--){
                            loadArr[i+1]=loadArr[i];
                        }
                        loadArr[0]=ammo;
                    }
            }
            catch (Exception e){
            }
        }

        private void remove(int i){
            if (i<maxLoad()){
                loadArr[i]=null;
                for (int j = i+1; j<maxLoad();j++){
                    loadArr[j-1]=loadArr[j];
                }
                loadArr[loadArr.length-1]= DummyBullet.class;
            }
        }
        public Class<? extends Ammo> get(){
            Class<? extends Ammo> ret = loadArr[0];
            remove(0);
            return ret;
        }
        public Class<? extends Ammo>[] getLoadArrray(){
            return loadArr.clone();
        }
        public void fill(Class<? extends Ammo> ammo){
            for (int i = 0; i < maxLoad(); i++){
                loadArr[i]=ammo;
            }
        }
        public Class<? extends Ammo>[] resize(int newSize){
            Class<? extends Ammo>[] oldLoad = getLoadArrray().clone();
            int oldSize = oldLoad.length;
            loadArr = new Class[newSize];
            Arrays.fill(loadArr,DummyBullet.class);
            for (int i = 0; i < Math.min(oldSize,newSize);i++){
                loadArr[i]=oldLoad[i];
                oldLoad[i]=DummyBullet.class;
            }
            if (oldSize>newSize){
                ArrayList ret = new ArrayList();
                for (Class<? extends Ammo> ammo:oldLoad){
                    if (ammo!=null && ammo!=DummyBullet.class){
                        ret.add(ammo);
                    }
                }
                Object[] objectList = ret.toArray();
                Class<? extends Ammo>[] ammoArr =  Arrays.copyOf(objectList,objectList.length,Class[].class);
                return ammoArr;
            } else {
                return new Class[0];
            }
        }
    }
    protected WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null && item instanceof Ammo) if (((Ammo)item).getAmmoType()==ammoType()){
//                setAmmoItem((Ammo)item);
                setAmmoClass(((Ammo)item).getClass());
            }
        }
    };
    public abstract boolean hit( Char attacker, Char defender);
    public void cursedZap(Ballistica shot, Hero hero, Gun gun){
        int cell = hero.pos;
        GameScene.add(Blob.seed(cell, 100, SmokeGas.class));
        if (Random.Int(100)<25){
            Buff.prolong(hero,Vertigo.class,2);
        }
        gun._load.get();
    }



    public static abstract class Attachment implements Bundlable {

        public boolean cursed = false;

        private static final Class<?>[] attachments = new Class<?>[]{
                ExtendedLoad.class,
                TeslaCoil.class,
                Flame.class,
                //enabled
				LongBarrel.class,
                ShortBarrel.class,
                FlintLock.class,
                WhellLock.class,
                SteelBayonet.class,
                MetalButt.class
                };
        private static final float[] chances= new float[]{
                0f,
                0f,
				0f,
                //enabled
				1f,
                1f,
                1f,
                1f,
                1f,
                1f
                 };

        private static final Class<?>[] curses = new Class<?>[]{
        };
        public boolean overrideZap = false;
        public abstract boolean shot( Ballistica bolt, Class<? extends Ammo> ammo, Gun curGun);
        public abstract boolean miss( Ballistica bolt, Class<? extends Ammo> ammo, Gun CurGun);
        public boolean onZap(Ballistica bolt, Class<? extends Ammo> ammo, Gun curGun, Char curUser){
            return false;
        }
        public boolean onStartShoot(Ballistica bolt, Class<? extends Ammo> ammo, Gun curGun, Char curUser){
            return false;
        }

        public float[] modifiers(){
            float[] mods = new float[]{
                1,      //accuracy mod
                1,      //reload speed mod
                1,      //shooting speed mod
                1,      //melee min dmg mod
                1,      //melee max dmg mod
            };
            return mods;
        }
        public float[] wndDmgMod(){
            return new float[]{
                    1,
                    1
            };
        }
        public float failChanceMod(){ //fail = defaultFailChance/this
            return 1;
        }

        public int loadMod(Load curLoad, Gun gun){
            return curLoad.maxLoad();
        }

        public String name() {
            if (!curse())
                return Messages.get(this, "name");
            else
                return Messages.get(this, "cursename");
        }

        public String desc() {
            if (!curse()) {
                return Messages.get(this, "desc");
            } else {
                return Messages.get(this, "cursedesc");
            }
        }

        public boolean curse() {
            return cursed;
        }

        private final String CURSE = "cursed";
        @Override
        public void restoreFromBundle( Bundle bundle ) {
            cursed=bundle.getBoolean(CURSE);
        }

        @Override
        public void storeInBundle( Bundle bundle ) {
            bundle.put(CURSE,cursed);
        }

        public abstract ItemSprite.Glowing glowing();

        @SuppressWarnings("unchecked")
        public static Attachment random() {
            try {
                return ((Class<Attachment>)attachments[ Random.chances( chances ) ]).newInstance();
            } catch (Exception e) {
                MoonshinePixelDungeon.reportException(e);
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        public static Attachment randomCurse() {
            try {
                Attachment ret = ((Class<Attachment>)attachments[ Random.chances( chances ) ]).newInstance();
                ret.cursed = true;
                return ret;
            } catch (Exception e) {
                MoonshinePixelDungeon.reportException(e);
                return null;
            }
        }
    }
}