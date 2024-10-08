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
package com.moonshinepixel.moonshinepixeldungeon.actors.hero;

import com.moonshinepixel.moonshinepixeldungeon.*;
import com.moonshinepixel.moonshinepixeldungeon.items.*;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.MailArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.PlateArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.GunslingerSubbag;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.MasterThievesArmband;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.ClusterBomb;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.ShrapnelBomb;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.StunBomb;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Blandfruit;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Food;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Moonshine;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.*;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.*;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfEvasion;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfWealth;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.*;
import com.moonshinepixel.moonshinepixeldungeon.items.traps.TrapPlacer;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfBlastWave;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfCorruption;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.*;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.*;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Boomerang;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Dart;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.GrippingTrap;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfMagicMissile;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.ClothArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.CloakOfShadows;
import com.moonshinepixel.moonshinepixeldungeon.plants.Plant;
import com.moonshinepixel.moonshinepixeldungeon.plants.RainPoppy;
import com.moonshinepixel.moonshinepixeldungeon.plants.Sungrass;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

public enum HeroClass {

	WARRIOR( "warrior" ),
	MAGE( "mage" ),
	ROGUE( "rogue" ),
	HUNTRESS( "huntress" ),
	GUNSLINGER( "gunslinger" );


	private String title;

	HeroClass( String title ) {
		this.title = title;
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;

		initCommon( hero );

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

            case HUNTRESS:
                initHuntress( hero );
                break;
            case GUNSLINGER:
                initGunslinger( hero );
                break;
		}

		if (Game.previewmode){
			initDev( hero );
		}

		hero.updateAwareness();
	}

	private static void initDev( Hero hero ){
//		hero.earnExp(150);
//		hero.STR=26;
		hero.earnExp(2048);


		Item[] collectables = new Item[]{
				new DevBeacon(),
				new GiantShuriken(),
//				new Mace().random(),
//				new HandAxe().random(),
//				new ScrollOfRecharging().quantity(10),
//				new WandOfBlastWave().upgrade(0),
//				new StunBomb().quantity(10),
//				new ShrapnelBomb().quantity(50),
//				new ClusterBomb().quantity(50),
//				new RingOfWealth().upgrade(150),
//				new ScrollOfRage().quantity(10),
//				new ScrollOfUpgrade().quantity(10),
//				new PotionOfInvisibility().quantity(10),
//				new PotionOfStrength().quantity(10),
//				new Amulet(),
				new PlateArmor().upgrade(10),
				new ArmorKit(),
				new PotionOfHealing().quantity(12),
				((Weapon)new SwitchHook().setTier(6).upgrade(10)).enchant(new Greedy()),
				new Bomb().quantity(6),
				new Honeypot().quantity(2)
		};
		for(Item itm:collectables){
			itm.identify().collect();
		}
	}

	private static void initCommon( Hero hero ) {
        Boolean curse = Dungeon.isChallenged(Challenges.CURSE);
        ClothArmor armor = new ClothArmor();
        armor.cursed=armor.cursedKnown=curse;
		if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
            (hero.belongings.armor = armor).identify();
        }

		if (!Dungeon.isChallenged(Challenges.NO_FOOD))
			new Food().identify().collect();

		if ((Dungeon.devoptions&2)!=0) {
			TomeOfMastery tom = new TomeOfMastery();
			tom.collect();
		}
	}

	public Hero.Gender defaultGender(){
		switch (this){
			case WARRIOR:
				return Hero.Gender.MALE;
			case MAGE:
				return Hero.Gender.FEMALE;
			case ROGUE:
				return Hero.Gender.MALE;
			case HUNTRESS:
				return Hero.Gender.FEMALE;
			case GUNSLINGER:
				return Hero.Gender.MALE;
		}
		return null;
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
            case HUNTRESS:
                return Badges.Badge.MASTERY_HUNTRESS;
            case GUNSLINGER:
                return Badges.Badge.MASTERY_GUNSLINGER;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
	    Boolean curse = Dungeon.isChallenged(Challenges.CURSE);
	    WornShortsword sword = new WornShortsword();
	    sword.cursed=sword.cursedKnown=curse;
        (hero.belongings.weapon = sword).identify();
		Dart darts = new Dart( 8 );
		darts.identify().collect();

		if ( Badges.isUnlocked(Badges.Badge.TUTORIAL_WARRIOR) ){
			if (!Dungeon.isChallenged(Challenges.NO_ARMOR))
				hero.belongings.armor.affixSeal(new BrokenSeal());
			Dungeon.quickslot.setSlot(0, darts);
		} else {
			if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
				BrokenSeal seal = new BrokenSeal();
				seal.collect();
				Dungeon.quickslot.setSlot(0, seal);
			}
			Dungeon.quickslot.setSlot(1, darts);
		}

		new PotionOfHealing().setKnown();
	}

	private static void initMage( Hero hero ) {
        Boolean curse = Dungeon.isChallenged(Challenges.CURSE);
		MagesStaff staff;

		if ( Badges.isUnlocked(Badges.Badge.TUTORIAL_MAGE) ){
			staff = new MagesStaff(new WandOfMagicMissile());
		} else {
			staff = new MagesStaff();
			new WandOfMagicMissile().identify().collect();
		}
        staff.cursed=staff.cursedKnown=curse;

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollOfUpgrade().setKnown();
	}

	private static void initRogue( Hero hero ) {
        Boolean curse = Dungeon.isChallenged(Challenges.CURSE);
        Dagger dagger = new Dagger();

        dagger.cursed=dagger.cursedKnown=curse;
		(hero.belongings.weapon = dagger).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.misc1 = cloak).identify();
		hero.belongings.misc1.activate( hero );

		Dart darts = new Dart( 8 );
//		darts.identify().collect();

		TrapPlacer trapGrim = new TrapPlacer(GrippingTrap.class);
//		TrapPlacer trapGrim = new TrapPlacer(SpearTrap.class);
		trapGrim.quantity(4);
		trapGrim.collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, trapGrim);

		new ScrollOfMagicMapping().setKnown();
	}

    private static void initHuntress( Hero hero ) {
        Boolean curse = Dungeon.isChallenged(Challenges.CURSE);
        Knuckles knuckles = new Knuckles();
        knuckles.cursed=knuckles.cursedKnown=curse;
        (hero.belongings.weapon = knuckles).identify();
        Boomerang boomerang = new Boomerang();
        boomerang.identify().collect();
        boomerang.cursed=boomerang.cursedKnown=curse;

        Dungeon.quickslot.setSlot(0, boomerang);

        new PotionOfMindVision().setKnown();
    }

    private static void initGunslinger( Hero hero ) {
        Boolean curse = Dungeon.isChallenged(Challenges.CURSE);
        GunslingerPistol pistol = new GunslingerPistol();
        pistol.cursed=pistol.cursedKnown=curse;
//        pistol.enchant(Gun.Attachment.random());
        (hero.belongings.weapon = pistol).identify();
        GunslingerSubbag subbag = new GunslingerSubbag();
		(hero.belongings.misc1 = subbag).identify();
		hero.belongings.misc1.activate( hero );
		Bullet bullet = new Bullet(25);
		bullet.collect();
		Bomb bomb = new Bomb();
		bomb.quantity(4);
		bomb.collect();


        Dungeon.quickslot.setSlot(0, pistol);
        Dungeon.quickslot.setSlot(1, bomb);

		new ScrollOfRage().setKnown();
    }
	
	public String title() {
		return title(MoonshinePixelDungeon.lastGender());
	}
	public String title(int gender) {
		return Messages.get(HeroClass.class, gender==0?title:title+"_fem");
	}
	
	public String spritesheet() {
		
		switch (this) {
		case WARRIOR:
			return Dungeon.hero.gender== Hero.Gender.MALE? Assets.WARRIOR:Assets.FEM_WARRIOR;
		case MAGE:
			return Dungeon.hero.gender== Hero.Gender.MALE?Assets.MAGE:Assets.FEM_MAGE;
		case ROGUE:
			return Dungeon.hero.gender== Hero.Gender.MALE?Assets.ROGUE:Assets.FEM_ROGUE;
		case HUNTRESS:
			return Dungeon.hero.gender== Hero.Gender.FEMALE?Assets.HUNTRESS:Assets.MAL_HUNTRESS;
        case GUNSLINGER:
			return Dungeon.hero.gender== Hero.Gender.MALE?Assets.GUNSLINGER:Assets.FEM_GUNSLINGER;
		}
		
		return null;
	}
	
	public String[] perks() {
		
		switch (this) {
		case WARRIOR:
			return new String[]{
					Messages.get(HeroClass.class, "warrior_perk1"),
					Messages.get(HeroClass.class, "warrior_perk2"),
					Messages.get(HeroClass.class, "warrior_perk3"),
					Messages.get(HeroClass.class, "warrior_perk4"),
					Messages.get(HeroClass.class, "warrior_perk5"),
			};
		case MAGE:
			return new String[]{
					Messages.get(HeroClass.class, "mage_perk1"),
					Messages.get(HeroClass.class, "mage_perk2"),
					Messages.get(HeroClass.class, "mage_perk3"),
					Messages.get(HeroClass.class, "mage_perk4"),
					Messages.get(HeroClass.class, "mage_perk5"),
			};
		case ROGUE:
			return new String[]{
					Messages.get(HeroClass.class, "rogue_perk1"),
					Messages.get(HeroClass.class, "rogue_perk2"),
					Messages.get(HeroClass.class, "rogue_perk3"),
					Messages.get(HeroClass.class, "rogue_perk4"),
					Messages.get(HeroClass.class, "rogue_perk5"),
					Messages.get(HeroClass.class, "rogue_perk6"),
			};
		case HUNTRESS:
			return new String[]{
					Messages.get(HeroClass.class, "huntress_perk1"),
					Messages.get(HeroClass.class, "huntress_perk2"),
					Messages.get(HeroClass.class, "huntress_perk3"),
					Messages.get(HeroClass.class, "huntress_perk4"),
					Messages.get(HeroClass.class, "huntress_perk5"),
			};
            case GUNSLINGER:
			return new String[]{
					Messages.get(HeroClass.class, "gunslinger_perk1"),
					Messages.get(HeroClass.class, "gunslinger_perk2")/*,
					Messages.get(HeroClass.class, "gunslinger_perk3"),
					Messages.get(HeroClass.class, "gunslinger_perk4"),
					Messages.get(HeroClass.class, "gunslinger_perk5"),*/
			};
		}
		
		return null;
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}
}
