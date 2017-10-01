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
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.Armor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.UnstableSpellbook;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.*;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Food;
import com.moonshinepixel.moonshinepixeldungeon.items.food.MysteryMeat;
import com.moonshinepixel.moonshinepixeldungeon.items.food.Pasty;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.*;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.*;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfTerror;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfFireblast;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfTransfusion;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.*;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.BleedingBullet;
import com.moonshinepixel.moonshinepixeldungeon.plants.Plant;
import com.moonshinepixel.moonshinepixeldungeon.plants.Sorrowmoss;
import com.moonshinepixel.moonshinepixeldungeon.plants.Stormvine;
import com.moonshinepixel.moonshinepixeldungeon.plants.Sungrass;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Ghost;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.LeatherArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfBlastWave;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfCorruption;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfMagicMissile;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.IncendiaryDart;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Shuriken;
import com.moonshinepixel.moonshinepixeldungeon.plants.Fadeleaf;
import com.moonshinepixel.moonshinepixeldungeon.plants.Firebloom;
import com.moonshinepixel.moonshinepixeldungeon.plants.Icecap;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.ClothArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.MailArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.PlateArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.ScaleArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.AlchemistsToolkit;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.Artifact;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.CapeOfThorns;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.ChaliceOfBlood;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.CloakOfShadows;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.DriedRose;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.EtherealChains;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.HornOfPlenty;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.LloydsBeacon;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.MasterThievesArmband;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.SandalsOfNature;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.TalismanOfForesight;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.TimekeepersHourglass;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.Bag;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfAccuracy;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfElements;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfEvasion;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfForce;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfFuror;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfHaste;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfMagic;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfMight;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfSharpshooting;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfTenacity;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfWealth;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.Scroll;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfIdentify;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfLullaby;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfMagicalInfusion;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfRage;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfRecharging;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.moonshinepixel.moonshinepixeldungeon.items.traps.TrapPlacer;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.Wand;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfDisintegration;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfFrost;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfLightning;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfPrismaticLight;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfRegrowth;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfVenom;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Boomerang;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.CurareDart;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Dart;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Javelin;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Tamahawk;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.Ammo;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.ArmorPiercingBullet;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.plants.BlandfruitBush;
import com.moonshinepixel.moonshinepixeldungeon.plants.Blindweed;
import com.moonshinepixel.moonshinepixeldungeon.plants.Dreamfoil;
import com.moonshinepixel.moonshinepixeldungeon.plants.Earthroot;
import com.moonshinepixel.moonshinepixeldungeon.plants.Rotberry;
import com.moonshinepixel.moonshinepixeldungeon.plants.Starflower;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Generator {

	public static enum Category {
		WEAPON	( 100,	Weapon.class ),
		WEP_T1	( 0, 	Weapon.class),
		WEP_T2	( 0,	Weapon.class),
		WEP_T3	( 0, 	Weapon.class),
		WEP_T4	( 0, 	Weapon.class),
		WEP_T5	( 0, 	Weapon.class),
		WEP_MT1	( 0, 	Weapon.class),
		GUN		( 60, 	Gun.class),
		AMMO	( 0, 	Ammo.class),
		ARMOR	( 60,	Armor.class ),
		POTION	( 500,	Potion.class ),
		SCROLL	( 400,	Scroll.class ),
		WAND	( 40,	Wand.class ),
		RING	( 15,	Ring.class ),
		ARTIFACT( 15,   Artifact.class),
		SEED	( 50,	Plant.Seed.class ),
		FOOD	( 0,	Food.class ),
		BOMB	( 0,	Bomb.class ),
		TRAP	( 0,	TrapPlacer.class ),
		UTILITY	( 100,	Item.class ),
		GOLD	( 500,	Gold.class );
		
		public Class<?>[] classes;
		public float[] probs;
		
		public float prob;
		public Class<? extends Item> superClass;
		
		private Category( float prob, Class<? extends Item> superClass ) {
			this.prob = prob;
			this.superClass = superClass;
		}
		
		public static int order( Item item ) {
			for (int i=0; i < values().length; i++) {
				if (values()[i].superClass.isInstance( item )) {
					return i;
				}
			}
			
			return item instanceof Bag ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
		}
	}

    private static final float[][] floorSetTierProbs = new float[][] {
            {0, 35, 20,  8,  2, 35},
            {0, 12.5f, 50, 20,  5, 12.5f},
            {0, 5, 40, 40, 10, 5},
            {0,  2.5f, 20, 50, 25,  2.5f},
            {0,  1,  8, 20, 70,  1},
            {0,  1,  0,  0,  0,  1},
            {0,  0, 20,  8,  2, 70}
    };
    private static final float[][] floorSetGunsProbs = new float[][] {
            {0, 70, 20,  8,  2},
            {0, 25, 50, 20,  5},
            {0, 10, 40, 40, 10},
            {0,  5, 20, 50, 25},
            {0,  2,  8, 20, 70},
            {0,  1,  0,  0,  0},
            {0, 70, 20,  8,  2}
    };
	
	private static HashMap<Category,Float> categoryProbs = new LinkedHashMap<>();

	private static final float[] INITIAL_ARTIFACT_PROBS = new float[]{ 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1};
	
	static {
		
		Category.GOLD.classes = new Class<?>[]{
			Gold.class };
		Category.GOLD.probs = new float[]{ 1 };
		
		Category.SCROLL.classes = new Class<?>[]{
			ScrollOfIdentify.class,
			ScrollOfTeleportation.class,
			ScrollOfRemoveCurse.class,
			ScrollOfUpgrade.class,
			ScrollOfRecharging.class,
			ScrollOfMagicMapping.class,
			ScrollOfRage.class,
			ScrollOfTerror.class,
			ScrollOfLullaby.class,
			ScrollOfMagicalInfusion.class,
			ScrollOfPsionicBlast.class,
			ScrollOfMirrorImage.class };
		Category.SCROLL.probs = new float[]{ 30, 10, 20, 0, 15, 15, 12, 8, 8, 0, 4, 10 };
		
		Category.POTION.classes = new Class<?>[]{
			PotionOfHealing.class,
			PotionOfExperience.class,
			PotionOfToxicGas.class,
			PotionOfParalyticGas.class,
			PotionOfLiquidFlame.class,
			PotionOfLevitation.class,
			PotionOfStrength.class,
			PotionOfMindVision.class,
			PotionOfPurity.class,
			PotionOfInvisibility.class,
			PotionOfMight.class,
			PotionOfFrost.class,
			PotionOfStorm.class};
		Category.POTION.probs = new float[]{ 45, 4, 15, 10, 15, 10, 0, 20, 12, 10, 0, 10, 10 };

		//TODO: add last ones when implemented
		Category.WAND.classes = new Class<?>[]{
			WandOfMagicMissile.class,
			WandOfLightning.class,
			WandOfDisintegration.class,
			WandOfFireblast.class,
			WandOfVenom.class,
			WandOfBlastWave.class,
			//WandOfLivingEarth.class,
			WandOfFrost.class,
			WandOfPrismaticLight.class,
			//WandOfWarding.class,
			WandOfTransfusion.class,
			WandOfCorruption.class,
			WandOfRegrowth.class };
		Category.WAND.probs = new float[]{ 5, 4, 4, 4, 4, 3, /*3,*/ 3, 3, /*3,*/ 3, 3, 3 };

		//see generator.randomWeapon
        Category.WEAPON.classes = new Class<?>[]{};
        Category.WEAPON.probs = new float[]{};


        Category.AMMO.classes = new Class<?>[]{
                Bullet.class,
				BleedingBullet.class,
				ArmorPiercingBullet.class
        };
        Category.AMMO.probs = new float[]{ 4, 0, 0};

        Category.BOMB.classes = new Class<?>[]{
                Bomb.class,
				AshBomb.class,
				IncendiaryBomb.class,
                ShrapnelBomb.class,
				ClusterBomb.class
        };
        Category.BOMB.probs = new float[]{ 12, 1, 1, 1, 2 };

        Category.TRAP.classes = new Class<?>[]{
                TrapPlacer.class
        };
        Category.TRAP.probs = new float[]{ 1 };

        Category.UTILITY.classes = new Class<?>[]{
        };
        Category.UTILITY.probs = new float[]{};

        Category.GUN.classes = new Class<?>[]{
                GunslingerPistol.class,
				Pistol.class,
				Blunderbuss.class,
                Fusil.class,
				Mortair.class
        };

        //see generator.randomGun
        Category.GUN.probs = new float[]{0, 0, 0, 0, 0};

		Category.WEP_T1.classes = new Class<?>[]{
			WornShortsword.class,
			Knuckles.class,
			Dagger.class,
			MagesStaff.class,
			Boomerang.class,
			Dart.class
		};
		Category.WEP_T1.probs = new float[]{ 1, 1, 1, 0, 0, 1 };

		Category.WEP_T2.classes = new Class<?>[]{
			Shortsword.class,
			HandAxe.class,
			Spear.class,
			Quarterstaff.class,
			Dirk.class,
			IncendiaryDart.class
		};
		Category.WEP_T2.probs = new float[]{ 6, 5, 5, 4, 4, 6 };

		Category.WEP_T3.classes = new Class<?>[]{
			Sword.class,
			Mace.class,
			Scimitar.class,
			RoundShield.class,
			Sai.class,
			Whip.class,
			Shuriken.class,
			CurareDart.class
		};
		Category.WEP_T3.probs = new float[]{ 6, 5, 5, 4, 4, 4, 6, 6 };

		Category.WEP_T4.classes = new Class<?>[]{
			Longsword.class,
			BattleAxe.class,
			Flail.class,
			RunicBlade.class,
			AssassinsBlade.class,
			Javelin.class
		};
		Category.WEP_T4.probs = new float[]{ 6, 5, 5, 4, 4, 6 };

		Category.WEP_T5.classes = new Class<?>[]{
				Greatsword.class,
				WarHammer.class,
				Glaive.class,
				Greataxe.class,
				Greatshield.class,
				Tamahawk.class
		};
		Category.WEP_T5.probs = new float[]{ 6, 5, 5, 4, 4, 6 };

		Category.WEP_MT1.classes = new Class<?>[]{
				Scythe.class,
				Claw.class,
				GiantShuriken.class,
				SwitchHook.class,
				BambooSpear.class
		};
		Category.WEP_MT1.probs = new float[]{ 1, 1, 1, 1, 1 };

		//see Generator.randomArmor
		Category.ARMOR.classes = new Class<?>[]{
			ClothArmor.class,
			LeatherArmor.class,
			MailArmor.class,
			ScaleArmor.class,
			PlateArmor.class };
		Category.ARMOR.probs = new float[]{ 0, 0, 0, 0, 0 };
		
		Category.FOOD.classes = new Class<?>[]{
			Food.class,
			Pasty.class,
			MysteryMeat.class };
		Category.FOOD.probs = new float[]{ 4, 1, 0 };
			
		Category.RING.classes = new Class<?>[]{
			RingOfAccuracy.class,
			RingOfEvasion.class,
			RingOfElements.class,
			RingOfForce.class,
			RingOfFuror.class,
			RingOfHaste.class,
			RingOfMagic.class, //currently removed from drop tables, pending rework
			RingOfMight.class,
			RingOfSharpshooting.class,
			RingOfTenacity.class,
			RingOfWealth.class};
		Category.RING.probs = new float[]{ 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 };

		Category.ARTIFACT.classes = new Class<?>[]{
			CapeOfThorns.class,
			ChaliceOfBlood.class,
			CloakOfShadows.class,
			HornOfPlenty.class,
			MasterThievesArmband.class,
			SandalsOfNature.class,
			TalismanOfForesight.class,
			TimekeepersHourglass.class,
			UnstableSpellbook.class,
			AlchemistsToolkit.class, //currently removed from drop tables, pending rework.
			DriedRose.class, //starts with no chance of spawning, chance is set directly after beating ghost quest.
			LloydsBeacon.class,
			EtherealChains.class
			};
		Category.ARTIFACT.probs = INITIAL_ARTIFACT_PROBS.clone();
		
		Category.SEED.classes = new Class<?>[]{
			Firebloom.Seed.class,
			Icecap.Seed.class,
			Sorrowmoss.Seed.class,
			Blindweed.Seed.class,
			Sungrass.Seed.class,
			Earthroot.Seed.class,
			Fadeleaf.Seed.class,
			Rotberry.Seed.class,
			BlandfruitBush.Seed.class,
			Dreamfoil.Seed.class,
			Stormvine.Seed.class,
			Starflower.Seed.class};
		Category.SEED.probs = new float[]{ 12, 12, 12, 12, 12, 12, 12, 0, 4, 12, 12, 1 };
	}
	
	public static void reset() {
		for (Category cat : Category.values()) {
			categoryProbs.put( cat, cat.prob );
		}
	}
	
	public static Item random() {
		return random( Random.chances( categoryProbs ) );
	}
	
	public static Item random( Category cat ) {
		try {
			
			categoryProbs.put( cat, categoryProbs.get( cat ) / 2 );
			
			switch (cat) {
			case ARMOR:
				return randomArmor();
			case WEAPON:
				return randomWeapon();
            case GUN:
                return randomGun();
			case ARTIFACT:
				Item item = randomArtifact();
				//if we're out of artifacts, return a ring instead.
				return item != null ? item : random(Category.RING);
			case UTILITY:
				return random(Random.oneOf(Category.BOMB,Category.BOMB,Category.TRAP));
			default:
				return ((Item)cat.classes[Random.chances( cat.probs )].newInstance()).random();
			}
			
		} catch (Exception e) {

			MoonshinePixelDungeon.reportException(e);
			return null;
			
		}
	}
	
	public static Item random( Class<? extends Item> cl ) {
		try {
			
			return ((Item) ClassReflection.newInstance(cl)).random();
			
		} catch (Exception e) {

			MoonshinePixelDungeon.reportException(e);
			return null;
			
		}
	}

	public static Armor randomArmor(){
		return randomArmor(Dungeon.depth / 5);
	}
	
	public static Armor randomArmor(int floorSet) {

		floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

		try {
			int targ;
			do {
				targ = Random.chances(floorSetTierProbs[floorSet]);
			} while (targ>4);
			Armor a = (Armor)Category.ARMOR.classes[targ].newInstance();
			a.random();
			return a;
		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}

    public static final Category[] wepTiers = new Category[]{
            Category.WEP_T1,
            Category.WEP_T2,
            Category.WEP_T3,
            Category.WEP_T4,
            Category.WEP_T5,
            Category.WEP_MT1
    };

    public static Weapon randomWeapon(){
        return randomWeapon(Dungeon.depth / 5);
    }

    public static Weapon randomWeapon(int floorSet) {

        floorSet = (int)GameMath.gate(0, floorSet, floorSetTierProbs.length-1);

        try {
            Category c = wepTiers[Random.chances(floorSetTierProbs[floorSet])];
            Weapon w = (Weapon)c.classes[Random.chances(c.probs)].newInstance();
            w.random();
            return w;
        } catch (Exception e) {
            MoonshinePixelDungeon.reportException(e);
            return null;
        }
    }
    public static Gun randomGun(){
        return randomGun(Dungeon.depth / 5);
    }

    public static Gun randomGun(int floorSet) {

        floorSet = (int)GameMath.gate(0, floorSet, floorSetGunsProbs.length-1);

        try {
            Category c = Category.GUN;
            int gunTier = Random.chances(floorSetGunsProbs[floorSet]);
//            gunTier=gunTier==4?3:gunTier;
            Gun w = (Gun)c.classes[gunTier].newInstance();
            w.random();
            return w;
        } catch (Exception e) {
            MoonshinePixelDungeon.reportException(e);
            return null;
        }
    }

	//enforces uniqueness of artifacts throughout a run.
	public static Artifact randomArtifact() {

		try {
			Category cat = Category.ARTIFACT;
			int i = Random.chances( cat.probs );

			//if no artifacts are left, return null
			if (i == -1){
				return null;
			}

			Artifact artifact = (Artifact)cat.classes[i].newInstance();

			//remove the chance of spawning this artifact.
			cat.probs[i] = 0;
			spawnedArtifacts.add(cat.classes[i].getSimpleName());

			artifact.random();

			return artifact;

		} catch (Exception e) {
			MoonshinePixelDungeon.reportException(e);
			return null;
		}
	}

	public static boolean removeArtifact(Artifact artifact) {
		if (spawnedArtifacts.contains(artifact.getClass().getSimpleName()))
			return false;

		Category cat = Category.ARTIFACT;
		for (int i = 0; i < cat.classes.length; i++)
			if (cat.classes[i].equals(artifact.getClass())) {
				if (cat.probs[i] == 1){
					cat.probs[i] = 0;
					spawnedArtifacts.add(artifact.getClass().getSimpleName());
					return true;
				} else
					return false;
			}

		return false;
	}

	//resets artifact probabilities, for new dungeons
	public static void initArtifacts() {
		Category.ARTIFACT.probs = INITIAL_ARTIFACT_PROBS.clone();

		//checks for dried rose quest completion, adds the rose in accordingly.
		if (Ghost.Quest.completed()) Category.ARTIFACT.probs[10] = 1;

		spawnedArtifacts = new ArrayList<String>();
	}

	private static ArrayList<String> spawnedArtifacts = new ArrayList<String>();

	private static final String ARTIFACTS = "artifacts";

	//used to store information on which artifacts have been spawned.
	public static void storeInBundle(Bundle bundle) {
		bundle.put( ARTIFACTS, spawnedArtifacts.toArray(new String[spawnedArtifacts.size()]));
	}

	public static void restoreFromBundle(Bundle bundle) {
		initArtifacts();

		if (bundle.contains(ARTIFACTS)) {
			Collections.addAll(spawnedArtifacts, bundle.getStringArray(ARTIFACTS));
			Category cat = Category.ARTIFACT;

			for (String artifact : spawnedArtifacts)
				for (int i = 0; i < cat.classes.length; i++)
					if (cat.classes[i].getSimpleName().equals(artifact))
						cat.probs[i] = 0;
		}
	}
}
