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
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Bless;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroSubClass;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfSharpshooting;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.DummyBullet;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public abstract class BulletGun extends Gun {
    public final int tier(){
        return tier;
    }

    @Override
    public float acu() {
        return 1.0f;
    }
    protected boolean strAcuMod = true;
    @Override
    public int min(int lvl){
        return  Math.max(tier(),2) +//base
                lvl;    //level scaling
    }

    @Override
    public int max(int lvl) {
        return  3*(tier()+1) +    //base
                lvl*tier();       //+1 per level
    }

    public String statsDesc(){

        if (levelKnown)
            return Messages.get(this, "stats_desc", minWnd(), maxWnd(), min(), max(), tier);
        else
            return Messages.get(this, "stats_desc", minWnd(0), maxWnd(0), min(0), max(0), tier);
    }

	@Override
	public int STRReq() {
		return STRReq(level());
	}
	@Override
    public int STRReq(int lvl){
        lvl = Math.max(0, lvl);
        //strength req decreases at +1,+3,+6,+10,etc.
        return (8 + tier() * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1)/3;
    }

	public int minWnd(int lvl){
        int dmg;
        dmg = tier() +//base
                lvl;    //level scaling
        if (attachment!=null){
            dmg*=attachment.wndDmgMod()[0];
        }
        return dmg;
	}

	public int maxWnd(int lvl){
        int dmg;
        dmg =  5*(tier()+1) +    //base
                lvl*(tier()+1);   //level scaling
        if (attachment!=null){
            dmg*=attachment.wndDmgMod()[1];
        }
        return dmg;
	}

	@Override
    protected Class<? extends Ammo> defaultAmmoClass(){
	    return Bullet.class;
    }

	@Override
    public void onZap(Ballistica bolt, Class<? extends Ammo> ammo) {
        Ammo _ammo;
        Attachment attach = attachment;
        if (attach!=null) if (attach.overrideZap){
            attach.onZap(bolt, ammo, this, Item.curUser);
            return;
        }
        try{
            _ammo=ammo.newInstance();
        } catch (Exception e){
            MoonshinePixelDungeon.reportException(e);
            e.printStackTrace();
            _ammo= new DummyBullet();
        }
        int cell = bolt.collisionPos;
        Char ch = Actor.findChar( cell );
        if (attach!=null) {
            //System.out.println(this.getClass()+"-120");
            if (ch != null) {
                if (hit(Item.curUser, ch)) {
                    _ammo.shot(bolt, ammo);
                    attach.shot(bolt, ammo, this);
                    onHit(bolt,ch, Item.curUser);
                } else {
                    _ammo.miss(bolt.collisionPos, Item.curUser, 0);
                    attach.shot(bolt, ammo, this);
                    onMiss(bolt, bolt.collisionPos, Item.curUser);
                    if (Dungeon.visible[ch.pos]) {
                        String defense = ch.defenseVerb();
                        ch.sprite.showStatus(CharSprite.NEUTRAL, defense);

                        Sample.INSTANCE.play(Assets.SND_MISS);
                    }
                }
            } else {
                _ammo.miss(bolt.collisionPos, Item.curUser, 0);
                attach.miss(bolt, ammo, this);
            }
        } else {
            if (ch != null) {
                if (hit(Item.curUser,ch)) {
                    _ammo.shot( bolt, ammo);
                    onHit(bolt,ch, Item.curUser);
                }
                else {
                    _ammo.miss(bolt.collisionPos, Item.curUser, 0);
                    onMiss(bolt, bolt.collisionPos, Item.curUser);
                    if (Dungeon.visible[ch.pos]) {
                        String defense = ch.defenseVerb();
                        ch.sprite.showStatus(CharSprite.NEUTRAL, defense);

                        Sample.INSTANCE.play(Assets.SND_MISS);
                    }
                }
            }
            else {
                _ammo.miss(bolt.collisionPos, Item.curUser, 0);
                onMiss(bolt, bolt.collisionPos, Item.curUser);
            }
        }
	}
	protected boolean onHit(Ballistica bolt, Char targ, Hero user){
        return false;
    }
	protected boolean onMiss(Ballistica bolt, int targ, Hero user){
        return false;
    }


    @Override
    public Item random() {
        tier= (int) GameMath.gate(2, tier-1+Random.chances(new float[]{1,18,1}),5);
        return super.random();
    }

    @Override
    public boolean hit( Char attacker, Char defender) {
        float acuRoll = Random.Float( attacker.attackSkill( defender ) );
        float defRoll = Random.Float( defender.defenseSkill( attacker ) );
        if (attacker.buff(Bless.class) != null) acuRoll *= 1.20f;
        if (defender.buff(Bless.class) != null) defRoll *= 1.20f;
        if (attacker instanceof Hero) {
            Hero hero = ((Hero) attacker);
            int delta = STRReq()-hero.STR;
            if (hero.heroClass == HeroClass.GUNSLINGER) {
                acuRoll/=delta>0?1:strAcuMod?Math.pow(1.1,delta):1;
                if (hero.subClass== HeroSubClass.GANGSTER){
                    acuRoll *= strAcuMod?2.5f:1;
                } else {
                    acuRoll *= strAcuMod?1.5f:1;
                }
            }
            acuRoll/=delta>0?Math.pow(1.2,delta):1;
            if (hero.heroClass == HeroClass.HUNTRESS) acuRoll *= 1.2f;
            if (hero.heroClass == HeroClass.WARRIOR) acuRoll *= 0.8f;
            acuRoll*=Math.pow(1.1, RingOfSharpshooting.getBonus(attacker, RingOfSharpshooting.Aim.class));
            acuRoll*=acu();
        }
        if (cursed){
            acuRoll/=1.5f;
        }
        if (attachment!=null){
            Attachment attach = attachment;
            acuRoll*=attach.modifiers()[0];
        }
        if (defender instanceof Hero) {
            if (((Hero) attacker).heroClass == HeroClass.ROGUE) defRoll *= 1.3f;
        }
        return (acuRoll) >= defRoll;
    }



    @Override
    public int price(boolean levelKnown, boolean cursedKnown) {
        int price = 40 * tier();
        if (cursedKnown && cursed) {
            price /= 2;
        }
        if (levelKnown) {
            if (level()>0) {
                price *= (level() + 1);
            } else if (level()<0){
                price/=level()*-1;
            }
        }

        if (price < 1) {
            price = 1;
        }
        return price;
    }


    public static final String TIER = "tier";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TIER,tier);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(TIER)) {
            tier = bundle.getInt(TIER);
        }
    }
}
