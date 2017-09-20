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
package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special;

import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.MerchantsBeacon;
import com.moonshinepixel.moonshinepixeldungeon.items.Torch;
import com.moonshinepixel.moonshinepixeldungeon.items.Weightstone;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.Bomb;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Fusil;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.BattleAxe;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.Longsword;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.Sword;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.CurareDart;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.IncendiaryDart;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Javelin;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Tamahawk;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.BleedingBullet;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.plants.Plant;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Belongings;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.LeatherArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.MailArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.PlateArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.armor.ScaleArmor;
import com.moonshinepixel.moonshinepixeldungeon.items.artifacts.TimekeepersHourglass;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.PotionBandolier;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.ScrollHolder;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.SeedPouch;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.WandHolster;
import com.moonshinepixel.moonshinepixeldungeon.items.food.SmallRation;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Blunderbuss;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Pistol;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.Potion;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfHealing;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.Scroll;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfIdentify;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.Wand;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.Ankh;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Honeypot;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.Stylus;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.Bag;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.Greatsword;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.HandAxe;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.Mace;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.Shortsword;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.WarHammer;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.Shuriken;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShopRoom extends SpecialRoom {

	private ArrayList<Item> itemsToSpawn;
	
	@Override
	public int minWidth() {
		if (itemsToSpawn == null) itemsToSpawn = generateItems();
		return Math.max(7, (int)(Math.sqrt(itemsToSpawn.size())+3));
	}
	
	@Override
	public int minHeight() {
		if (itemsToSpawn == null) itemsToSpawn = generateItems();
		return Math.max(7, (int)(Math.sqrt(itemsToSpawn.size())+3));
	}
	
	public void paint( Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		placeShopkeeper( level );

		placeItems( level );
		
		for (Room.Door door : connected.values()) {
			door.set( Room.Door.Type.REGULAR );
		}

	}

	protected void placeShopkeeper( Level level ) {

		int pos = level.pointToCell(center());

		Mob shopkeeper = new Shopkeeper();
		shopkeeper.pos = pos;
		level.mobs.add( shopkeeper );

	}

	protected void placeItems( Level level ){

		if (itemsToSpawn == null)
			itemsToSpawn = generateItems();

		Point itemPlacement = new Point(entrance());
		if (itemPlacement.y == top){
			itemPlacement.y++;
		} else if (itemPlacement.y == bottom) {
			itemPlacement.y--;
		} else if (itemPlacement.x == left){
			itemPlacement.x++;
		} else {
			itemPlacement.x--;
		}

		for (Item item : itemsToSpawn) {

			if (itemPlacement.x == left+1 && itemPlacement.y != top+1){
				itemPlacement.y--;
			} else if (itemPlacement.y == top+1 && itemPlacement.x != right-1){
				itemPlacement.x++;
			} else if (itemPlacement.x == right-1 && itemPlacement.y != bottom-1){
				itemPlacement.y++;
			} else {
				itemPlacement.x--;
			}

			int cell = level.pointToCell(itemPlacement);

			if (level.heaps.get( cell ) != null) {
				do {
					cell = level.pointToCell(random());
				} while (level.heaps.get( cell ) != null || level.findMob( cell ) != null);
			}

			level.drop( item, cell ).type = Heap.Type.FOR_SALE;
		}

	}
	
	protected static ArrayList<Item> generateItems() {

		ArrayList<Item> itemsToSpawn = new ArrayList<>();
		
		switch (Dungeon.depth) {
		case 6:
			itemsToSpawn.add( (Random.Int( 2 ) == 0 ? new Shortsword().identify() : new HandAxe()).identify() );
			itemsToSpawn.add( Random.Int( 2 ) == 0 ?
					new IncendiaryDart().quantity(Random.NormalIntRange(2, 4)) :
					new CurareDart().quantity(Random.NormalIntRange(1, 3)));
			itemsToSpawn.add( new LeatherArmor().identify() );
			itemsToSpawn.add( new Pistol().identify() );
			itemsToSpawn.add( new Bullet().random(2) );
			break;
			
		case 11:
			itemsToSpawn.add( (Random.Int( 2 ) == 0 ? new Sword().identify() : new Mace()).identify() );
			itemsToSpawn.add( Random.Int( 2 ) == 0 ?
					new CurareDart().quantity(Random.NormalIntRange(2, 5)) :
					new Shuriken().quantity(Random.NormalIntRange(3, 6)));
			itemsToSpawn.add( new MailArmor().identify() );
			itemsToSpawn.add( new Blunderbuss().identify() );
			if (Random.Int(4)==0){
//				itemsToSpawn.add(new BleedingBullet().random(2));
				itemsToSpawn.add(new Bullet().random(2));
			} else {
				itemsToSpawn.add(new Bullet().random(2));
			}
			break;
			
		case 16:
			itemsToSpawn.add( (Random.Int( 2 ) == 0 ? new Longsword().identify() : new BattleAxe()).identify() );
			itemsToSpawn.add( Random.Int( 2 ) == 0 ?
					new Shuriken().quantity(Random.NormalIntRange(4, 7)) :
					new Javelin().quantity(Random.NormalIntRange(3, 6)));
			itemsToSpawn.add( new ScaleArmor().identify() );
			itemsToSpawn.add( new Fusil().identify() );
            if (Random.Int(2)==0){
//                itemsToSpawn.add(new BleedingBullet().random(2f));
				itemsToSpawn.add(new Bullet().random(2));
            } else {
                itemsToSpawn.add(new Bullet().random(2f));
            }
			break;
			
		case 21:
			itemsToSpawn.add( Random.Int( 2 ) == 0 ? new Greatsword().identify() : new WarHammer().identify() );
			itemsToSpawn.add( Random.Int(2) == 0 ?
					new Javelin().quantity(Random.NormalIntRange(4, 7)) :
					new Tamahawk().quantity(Random.NormalIntRange(4, 7)));
			itemsToSpawn.add( new PlateArmor().identify() );
			itemsToSpawn.add( new Torch() );
			itemsToSpawn.add( new Torch() );
			itemsToSpawn.add( new Torch() );
            if (Random.Int(4)!=0){
//                itemsToSpawn.add(new BleedingBullet().random(2f));
				itemsToSpawn.add(new Bullet().random(2));
            } else {
                itemsToSpawn.add(new Bullet().random(2f));
            }
            if (Random.Int(4)!=0){
//                itemsToSpawn.add(new BleedingBullet().random(2f));
				itemsToSpawn.add(new Bullet().random(2));
            } else {
                itemsToSpawn.add(new Bullet().random(2f));
            }
			break;
		}

		itemsToSpawn.add( new MerchantsBeacon() );


		itemsToSpawn.add(ChooseBag(Dungeon.hero.belongings));


		itemsToSpawn.add( new PotionOfHealing() );
		for (int i=0; i < 3; i++)
			itemsToSpawn.add( Generator.random( Generator.Category.POTION ) );

		itemsToSpawn.add( new ScrollOfIdentify() );
		itemsToSpawn.add( new ScrollOfRemoveCurse() );
		itemsToSpawn.add( new ScrollOfMagicMapping() );
		itemsToSpawn.add( Generator.random( Generator.Category.SCROLL ) );

		for (int i=0; i < 2; i++)
			itemsToSpawn.add( Random.Int(2) == 0 ?
					Generator.random( Generator.Category.POTION ) :
					Generator.random( Generator.Category.SCROLL ) );


		itemsToSpawn.add( new SmallRation() );
		itemsToSpawn.add( new SmallRation() );

		itemsToSpawn.add( new Bomb().random() );
		switch (Random.Int(5)){
			case 1:
				itemsToSpawn.add( new Bomb() );
				break;
			case 2:
				itemsToSpawn.add( new Bomb().random() );
				break;
			case 3:
			case 4:
				itemsToSpawn.add( new Honeypot() );
				break;
		}


		if (Dungeon.depth == 6) {
			itemsToSpawn.add( new Ankh() );
			itemsToSpawn.add( new Weightstone() );
		} else {
			itemsToSpawn.add(Random.Int(2) == 0 ? new Ankh() : new Weightstone());
		}


		TimekeepersHourglass hourglass = Dungeon.hero.belongings.getItem(TimekeepersHourglass.class);
		if (hourglass != null){
			int bags = 0;
			//creates the given float percent of the remaining bags to be dropped.
			//this way players who get the hourglass late can still max it, usually.
			switch (Dungeon.depth) {
				case 6:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.20f ); break;
				case 11:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.25f ); break;
				case 16:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.50f ); break;
				case 21:
					bags = (int)Math.ceil(( 5-hourglass.sandBags) * 0.80f ); break;
			}

			for(int i = 1; i <= bags; i++){
				itemsToSpawn.add( new TimekeepersHourglass.sandBag());
				hourglass.sandBags ++;
			}
		}

		Item rare;
		switch (Random.Int(10)){
			case 0:
				rare = Generator.random( Generator.Category.WAND );
				rare.level( 0 );
				break;
			case 1:
				rare = Generator.random(Generator.Category.RING);
				rare.level( 1 );
				break;
			case 2:
				rare = Generator.random( Generator.Category.ARTIFACT ).identify();
				break;
			default:
				rare = new Stylus();
		}
		rare.cursed = rare.cursedKnown = false;
		Item itm = rare;
		itm.level(itm.level()>0?itm.level():-itm.level());
		itemsToSpawn.add( rare );

		//hard limit is 63 items + 1 shopkeeper, as shops can't be bigger than 8x8=64 internally
		if (itemsToSpawn.size() > 63)
			throw new RuntimeException("Shop attempted to carry more than 63 items!");

		Random.shuffle(itemsToSpawn);
		return itemsToSpawn;
	}

	protected static Bag ChooseBag(Belongings pack){

		int seeds = 0, scrolls = 0, potions = 0, wands = 0;

		//count up items in the main bag, for bags which haven't yet been dropped.
		for (Item item : pack.backpack.items) {
			if (!Dungeon.limitedDrops.seedBag.dropped() && item instanceof Plant.Seed)
				seeds++;
			else if (!Dungeon.limitedDrops.scrollBag.dropped() && item instanceof Scroll)
				scrolls++;
			else if (!Dungeon.limitedDrops.potionBag.dropped() && item instanceof Potion)
				potions++;
			else if (!Dungeon.limitedDrops.wandBag.dropped() && item instanceof Wand)
				wands++;
		}

		//then pick whichever valid bag has the most items available to put into it.
		//note that the order here gives a perference if counts are otherwise equal
		if (seeds >= scrolls && seeds >= potions && seeds >= wands && !Dungeon.limitedDrops.seedBag.dropped()) {
			Dungeon.limitedDrops.seedBag.drop();
			return new SeedPouch();

		} else if (scrolls >= potions && scrolls >= wands && !Dungeon.limitedDrops.scrollBag.dropped()) {
			Dungeon.limitedDrops.scrollBag.drop();
			return new ScrollHolder();

		} else if (potions >= wands && !Dungeon.limitedDrops.potionBag.dropped()) {
			Dungeon.limitedDrops.potionBag.drop();
			return new PotionBandolier();

		} else if (!Dungeon.limitedDrops.wandBag.dropped()) {
			Dungeon.limitedDrops.wandBag.drop();
			return new WandHolster();
		}

		return null;
	}

}
