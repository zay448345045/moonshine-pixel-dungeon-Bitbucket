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
package com.moonshinepixel.moonshinepixeldungeon;

import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.ExplosiveGas;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Amok;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Awareness;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Light;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.MindVision;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Yog;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Blacksmith;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Ghost;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Imp;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.npcs.Wandmaker;
import com.moonshinepixel.moonshinepixeldungeon.items.Ankh;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.Potion;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.Scroll;
import com.moonshinepixel.moonshinepixeldungeon.levels.*;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.SpecialRoom;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.scenes.StartScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.QuickSlotButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.Runes;
import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.moonshinepixel.moonshinepixeldungeon.utils.DungeonSeed;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndResurrect;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Dungeon {

	//enum of items which have limited spawns, records how many have spawned
	//could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
	//TODO: this is fairly brittle when it comes to bundling, should look into a more flexible solution.
	public static enum limitedDrops{
		//limited world drops
		strengthPotions,
		upgradeScrolls,
		arcaneStyli,

		//all unlimited health potion sources (except guards, which are at the bottom.
		swarmHP,
		batHP,
		warlockHP,
		scorpioHP,
		cookingHP,
		//blandfruit, which can technically be an unlimited health potion source
		blandfruitSeed,

		//doesn't use Generator, so we have to enforce one armband drop here
		armband,

		//containers
		dewVial,
		seedBag,
		scrollBag,
		potionBag,
		wandBag,

		guardHP;

		public int count = 0;

		//for items which can only be dropped once, should directly access count otherwise.
		public boolean dropped(){
			return count != 0;
		}
		public void drop(){
			count = 1;
		}
	}

	public static int challenges;
	public static int devoptions;

	public static Hero hero;
	public static Level level;

	public static QuickSlot quickslot = new QuickSlot();

	public static int depth;



	public static int storyline = 0; //0 = shattered, 1 = moonshine

	//this array used for depth-based damage and mobs(Mimic, Statue)
    public static final int[] fakedepth = new int[]{1,
             1, 2, 3, 4, 5,
             6, 7, 8, 9,10,
            11,12,13,14,15,
            16,17,18,19,20,
            21,22,23,24,25,
            26, 1, 1, 1, 1,
             1, 2, 3, 4, 5,
			 6, 7, 8, 9,10
    };
    //this array used for calculating score(max visited depth)
    public static final int[] scoreDepth = new int[]{0,
             1, 2, 3, 4, 5,
             6, 7, 8, 9,10,
            11,12,13,14,15,
            16,17,18,19,20,
            21,22,23,24,25,
            26, 1, 1, 1, 1,
             1, 2, 3, 4, 5,
			 6, 7, 8, 9,10
    };
    //this array used for graphical displaying of depth
    public static final String[] showDepth = new String[]{"0",
             "1", "2", "3", "4", "5",
             "6", "7", "8", "9","10",
            "11","12","13","14","15",
            "16","17","18","19","20",
            "21","22","23","24","25",
            "26", "?", "?", "?", "?",
             "1", "2", "3", "4", "5",
			 "6", "7", "8", "9","10"
    };
    public static boolean[] visitedDepth = new boolean[100];

    public static boolean[] returnedDepth = new boolean[100];

	public static int gold;
	
	public static HashSet<Integer> chapters;
	
	// Hero's field of view
	public static boolean[] visible;

	public static SparseArray<ArrayList<Item>> droppedItems;

	public static int version;

	public static int rune;

	public static long seed;

	public static boolean customseed;

	public static Hero.Gender gender = Hero.Gender.MALE; //Yep, this is sexism)
	
	public static void init() {

		version = Game.versionCode;
		challenges = MoonshinePixelDungeon.challenges();
		devoptions = MoonshinePixelDungeon.devOptions();
        Arrays.fill(visitedDepth, false);
        Arrays.fill(returnedDepth, false);
		seed = DungeonSeed.seed();
		customseed=MoonshinePixelDungeon.customSeed();

		rune = Runes.random();

		Actor.clear();
		Actor.resetNextID();

		Random.seed( seed );

			Scroll.initLabels();
			Potion.initColors();
			Ring.initGems();

			SpecialRoom.initForRun();

		Random.seed();
		
		Statistics.reset();
		Journal.reset();

		quickslot.reset();
		QuickSlotButton.reset();

		storyline=MoonshinePixelDungeon.storyline();
		depth = storyline==0?0:MoonshinePixelDungeon.previewmode?34:30;
		gold = 0;

		droppedItems = new SparseArray<ArrayList<Item>>();

		for (limitedDrops a : limitedDrops.values())
			a.count = 0;
		
		chapters = new HashSet<Integer>();
		
		Ghost.Quest.reset();
		Wandmaker.Quest.reset();
		Blacksmith.Quest.reset();
		Imp.Quest.reset();

		Generator.initArtifacts();
		hero = new Hero();
		hero.live();
		hero.gender= MoonshinePixelDungeon.lastGender()==0? Hero.Gender.MALE: Hero.Gender.FEMALE;
		
		Badges.reset();
		
		StartScene.curClass.initHero( hero );
	}

	public static boolean isChallenged( int mask ) {
		return (challenges & mask) != 0;
	}
	
	public static Level newLevel() {

		
		Dungeon.level = null;
		Actor.clear();
		
		depth++;
		if (scoreDepth[depth] > Statistics.deepestFloor) {
			Statistics.deepestFloor = scoreDepth[depth];
			
			if (Statistics.qualifiedForNoKilling) {
				Statistics.completedWithNoKilling = true;
			} else {
				Statistics.completedWithNoKilling = false;
			}
		}
		visitedDepth[depth]=true;
		
		Level level;
		switch (depth) {
		case 1:
		case 2:
		case 3:
		case 4:
			level = new SewerLevel();
			break;
		case 5:
			level = new SewerBossLevel();
			break;
		case 6:
		case 7:
		case 8:
		case 9:
			level = new PrisonLevel();
			break;
		case 10:
			level = new PrisonBossLevel();
			break;
		case 11:
		case 12:
		case 13:
		case 14:
			level = new CavesLevel();
			break;
		case 15:
			level = new CavesBossLevel();
			break;
		case 16:
		case 17:
		case 18:
		case 19:
			level = new CityLevel();
			break;
		case 20:
			level = new CityBossLevel();
			break;
		case 21:
			level = new LastShopLevel();
			break;
		case 22:
		case 23:
		case 24:
			level = new HallsLevel();
			break;
		case 25:
			level = new HallsBossLevel();
			break;
		case 26:
			level = new LastLevel();
			break;
		case 27:
			level = new TestLevel();
			break;
		case 31:
		case 32:
		case 33:
		case 34:
			level = new GardenLevel();
			break;
		case 35:
			level = new GardenBossLevel();
			break;
		default:
			level = new DeadEndLevel();
			Statistics.deepestFloor--;
		}
		
		level.create();
		
		Statistics.qualifiedForNoKilling = !bossLevel();
		
		return level;
	}
	
	public static void resetLevel() {
		
		Actor.clear();
		
		level.reset();
		switchLevel( level, level.entrance );
	}

	public static long seedCurDepth(){
		return seedForDepth(depth);
	}

	public static long seedForDepth(int depth){
		Random.seed( seed );
		for (int i = 0; i < depth; i ++)
			Random.Long(); //we don't care about these values, just need to go through them
		long result = Random.Long();
		Random.seed();
		return result;
	}
	
	public static boolean shopOnLevel() {
		return depth == 6 || depth == 11 || depth == 16;
	}
	
	public static boolean bossLevel() {
		return bossLevel( depth );
	}
	
	public static boolean bossLevel( int depth ) {
		return depth == 5 || depth == 10 || depth == 15 || depth == 20 || depth == 25;
	}
	
	@SuppressWarnings("deprecation")
	public static void switchLevel( final Level level, int pos ) {
		
		Dungeon.level = level;
		Actor.init();

		PathFinder.setMapSize(level.width(), level.height());
		visible = new boolean[level.length()];
		
		Actor respawner = level.respawner();
		if (respawner != null) {
			Actor.add( level.respawner() );
		}

		hero.pos = pos != -1 ? pos : level.exit;
		
		Light light = hero.buff( Light.class );
		hero.viewDistance = light == null || !level.lightaffected ? level.viewDistance : Math.max( Light.DISTANCE, level.viewDistance );
		
		hero.curAction = hero.lastAction = null;
		
		observe();
		try {
			saveAll();
		} catch (IOException e) {
			MoonshinePixelDungeon.reportException(e);
			/*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
			But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
		}
	}

	public static void dropToChasm( Item item ) {
		int depth = Dungeon.depth + 1;
		ArrayList<Item> dropped = (ArrayList<Item>)Dungeon.droppedItems.get( depth );
		if (dropped == null) {
			Dungeon.droppedItems.put( depth, dropped = new ArrayList<Item>() );
		}
		dropped.add( item );
	}

	public static boolean posNeeded() {
		//2 POS each floor set
		int posLeftThisSet = 2 - (limitedDrops.strengthPotions.count - (depth / 5) * 2);
		if (posLeftThisSet <= 0) return false;

		int floorThisSet = (depth % 5);

		//pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
		int targetPOSLeft = 2 - floorThisSet/2;
		if (floorThisSet % 2 == 1 && Random.Int(2) == 0) targetPOSLeft --;

		if (targetPOSLeft < posLeftThisSet) return true;
		else return false;

	}
	
	public static boolean souNeeded() {
		//3 SOU each floor set
		int souLeftThisSet = 3 - (limitedDrops.upgradeScrolls.count - (depth / 5) * 3);
		if (souLeftThisSet <= 0) return false;

		int floorThisSet = (depth % 5);
		//chance is floors left / scrolls left
		return Random.Int(5 - floorThisSet) < souLeftThisSet;
	}
	
	public static boolean asNeeded() {
		//1 AS each floor set
		int asLeftThisSet = 1 - (limitedDrops.arcaneStyli.count - (depth / 5));
		if (asLeftThisSet <= 0) return false;

		int floorThisSet = (depth % 5);
		//chance is floors left / scrolls left
		return Random.Int(5 - floorThisSet) < asLeftThisSet;
	}
	
	private static final String RG_GAME_FILE	= "game.dat";
	private static final String RG_DEPTH_FILE	= "depth%d.dat";
	
	private static final String WR_GAME_FILE	= "warrior.dat";
	private static final String WR_DEPTH_FILE	= "warrior%d.dat";
	
	private static final String MG_GAME_FILE	= "mage.dat";
	private static final String MG_DEPTH_FILE	= "mage%d.dat";
	
	private static final String RN_GAME_FILE	= "ranger.dat";
    private static final String RN_DEPTH_FILE	= "ranger%d.dat";

	private static final String GS_GAME_FILE	= "gunslinger.dat";
    private static final String GS_DEPTH_FILE	= "gunslinger%d.dat";
	
	private static final String VERSION		= "version";
	private static final String SEED		= "seed";
	private static final String CHALLENGES	= "challenges";
	private static final String DEVOPTIONS	= "devoptions";
	private static final String HERO		= "hero";
	private static final String GOLD		= "gold";
	private static final String DEPTH		= "depth";
	private static final String STORYLINE	= "storyline";
	private static final String VISITEDDEPTH= "visitedDepthArr";
	private static final String RETURNEDDEPTH= "returnedDepthArr";
	private static final String DROPPED     = "dropped%d";
	private static final String LEVEL		= "level";
	private static final String LIMDROPS    = "limiteddrops";
	private static final String DV			= "dewVial";
	private static final String WT			= "transmutation";
	private static final String CHAPTERS	= "chapters";
	private static final String QUESTS		= "quests";
	private static final String BADGES		= "badges";
	private static final String ISSEED		= "customseed";
	private static final String RUNE		= "rune";

	public static String gameFile( HeroClass cl ) {
		switch (cl) {
		case WARRIOR:
			return WR_GAME_FILE;
		case MAGE:
			return MG_GAME_FILE;
		case HUNTRESS:
			return RN_GAME_FILE;
        case GUNSLINGER:
			return GS_GAME_FILE;
		default:
			return RG_GAME_FILE;
		}
	}
	
	private static String depthFile( HeroClass cl ) {
		switch (cl) {
		case WARRIOR:
			return WR_DEPTH_FILE;
		case MAGE:
			return MG_DEPTH_FILE;
		case HUNTRESS:
			return RN_DEPTH_FILE;
        case GUNSLINGER:
			return GS_DEPTH_FILE;
		default:
			return RG_DEPTH_FILE;
		}
	}
	
	public static void saveGame( String fileName ) throws IOException {
		try {
			Bundle bundle = new Bundle();

			version = Game.versionCode;
			bundle.put( VERSION, version );
			bundle.put( SEED, seed );
			bundle.put( CHALLENGES, challenges );
			bundle.put( DEVOPTIONS, devoptions );
			bundle.put( HERO, hero );
			bundle.put( GOLD, gold );
			bundle.put( DEPTH, depth );
			bundle.put( STORYLINE, storyline );
			bundle.put( VISITEDDEPTH, visitedDepth );
			bundle.put( RETURNEDDEPTH, returnedDepth );
			bundle.put( ISSEED, customseed );
			bundle.put( RUNE, rune );

			for (int d : droppedItems.keyArray()) {
				bundle.put(Messages.format(DROPPED, d), droppedItems.get(d));
			}

			quickslot.storePlaceholders( bundle );

			int[] dropValues = new int[limitedDrops.values().length];
			for (limitedDrops value : limitedDrops.values())
				dropValues[value.ordinal()] = value.count;
			bundle.put ( LIMDROPS, dropValues );
			
			int count = 0;
			int ids[] = new int[chapters.size()];
			for (Integer id : chapters) {
				ids[count++] = id;
			}
			bundle.put( CHAPTERS, ids );
			
			Bundle quests = new Bundle();
			Ghost		.Quest.storeInBundle( quests );
			Wandmaker	.Quest.storeInBundle( quests );
			Blacksmith	.Quest.storeInBundle( quests );
			Imp			.Quest.storeInBundle( quests );
			bundle.put( QUESTS, quests );
			
			SpecialRoom.storeRoomsInBundle( bundle );
			
			Statistics.storeInBundle( bundle );
			Journal.storeInBundle( bundle );
			Generator.storeInBundle( bundle );
			
			Scroll.save( bundle );
			Potion.save( bundle );
			Ring.save( bundle );

			Actor.storeNextID( bundle );
			
			Bundle badges = new Bundle();
			Badges.saveLocal( badges );
			bundle.put( BADGES, badges );
			
			OutputStream output = Game.instance.openFileOutput( fileName );
			Bundle.write( bundle, output );
			output.close();
			
		} catch (IOException e) {
			GamesInProgress.setUnknown( hero.heroClass );
			MoonshinePixelDungeon.reportException(e);
		}
	}
	
	public static void saveLevel() throws IOException {
		Bundle bundle = new Bundle();
		bundle.put( LEVEL, level );
		
		OutputStream output = Game.instance.openFileOutput( Messages.format( depthFile( hero.heroClass ), depth ) );
		Bundle.write( bundle, output );
		output.close();
	}
	
	public static void saveAll() throws IOException {
		if (hero.isAlive()) {
			
			Actor.fixTime();
			saveGame( gameFile( hero.heroClass ) );
			saveLevel();

			GamesInProgress.set( hero.heroClass, depth, hero.lvl, challenges != 0 );

		} else if (WndResurrect.instance != null) {
			
			WndResurrect.instance.hide();
			Hero.reallyDie( WndResurrect.causeOfDeath );
			
		}
	}
	
	public static void loadGame( HeroClass cl ) throws IOException {
		loadGame( gameFile( cl ), true );
	}

	public static void loadGame( String fileName ) throws IOException {
		loadGame( fileName, false );
	}
	
	public static void loadGame( String fileName, boolean fullLoad ) throws IOException {
		
		Bundle bundle = gameBundle( fileName );

		version = bundle.getInt( VERSION );

		seed = bundle.contains( SEED ) ? bundle.getLong( SEED ) : DungeonSeed.seed();

		Generator.reset();

		Actor.restoreNextID( bundle );

		quickslot.reset();
		QuickSlotButton.reset();

		Dungeon.challenges = bundle.getInt( CHALLENGES );

		Dungeon.devoptions = bundle.getInt( DEVOPTIONS );

		Dungeon.level = null;
		Dungeon.depth = -1;
		
		Scroll.restore( bundle );
		Potion.restore( bundle );
		Ring.restore( bundle );

		rune=bundle.contains(RUNE)?bundle.getInt(RUNE):Runes.random();

		quickslot.restorePlaceholders( bundle );
		
		if (fullLoad) {
			int[] dropValues = bundle.getIntArray(LIMDROPS);
			for (limitedDrops value : limitedDrops.values())
				value.count = value.ordinal() < dropValues.length ?
						dropValues[value.ordinal()] : 0;

			chapters = new HashSet<Integer>();
			int ids[] = bundle.getIntArray( CHAPTERS );
			if (ids != null) {
				for (int id : ids) {
					chapters.add( id );
				}
			}
			
			Bundle quests = bundle.getBundle( QUESTS );
			if (!quests.isNull()) {
				Ghost.Quest.restoreFromBundle( quests );
				Wandmaker.Quest.restoreFromBundle( quests );
				Blacksmith.Quest.restoreFromBundle( quests );
				Imp.Quest.restoreFromBundle( quests );
			} else {
				Ghost.Quest.reset();
				Wandmaker.Quest.reset();
				Blacksmith.Quest.reset();
				Imp.Quest.reset();
			}
			
			SpecialRoom.restoreRoomsFromBundle(bundle);
		}
		
		Bundle badges = bundle.getBundle(BADGES);
		if (!badges.isNull()) {
			Badges.loadLocal( badges );
		} else {
			Badges.reset();
		}
		
		hero = null;
		hero = (Hero)bundle.get( HERO );
		
		gold = bundle.getInt( GOLD );
		depth = bundle.getInt( DEPTH );
        storyline = bundle.getInt( STORYLINE );
		visitedDepth = bundle.getBooleanArray(VISITEDDEPTH);
		returnedDepth = bundle.getBooleanArray(RETURNEDDEPTH);
		customseed = MoonshinePixelDungeon.customSeed() || bundle.getBoolean(ISSEED);

		Statistics.restoreFromBundle( bundle );
		Journal.restoreFromBundle( bundle );
		Generator.restoreFromBundle( bundle );

		droppedItems = new SparseArray<ArrayList<Item>>();
		for (int i=2; i <= Statistics.deepestFloor + 1; i++) {
			ArrayList<Item> dropped = new ArrayList<Item>();
			if (bundle.contains(Messages.format( DROPPED, i )))
				for (Bundlable b : bundle.getCollection( Messages.format( DROPPED, i ) ) ) {
					dropped.add( (Item)b );
				}
			if (!dropped.isEmpty()) {
				droppedItems.put( i, dropped );
			}
		}
	}
	
	public static Level loadLevel( HeroClass cl ) throws IOException {
		
		Dungeon.level = null;
		Actor.clear();
		
		InputStream input = Game.instance.openFileInput( Messages.format( depthFile( cl ), depth ) ) ;
		Bundle bundle = Bundle.read( input );
		input.close();
		
		return (Level)bundle.get( "level" );
	}
	
	public static void deleteGame( HeroClass cl, boolean deleteLevels ) {
		
		Game.instance.deleteFile( gameFile( cl ) );
		
		if (deleteLevels) {
			int depth = 1;
			while (Game.instance.deleteFile( Messages.format( depthFile( cl ), depth ) )) {
				depth++;
			}
		}
		
		GamesInProgress.delete( cl );
	}
	
	public static Bundle gameBundle( String fileName ) throws IOException {
		
		InputStream input = Game.instance.openFileInput( fileName );
		Bundle bundle = Bundle.read( input );
		input.close();
		
		return bundle;
	}
	
	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		info.depth = bundle.getInt( DEPTH );
		info.version = bundle.getInt( VERSION );
		info.challenges = (bundle.getInt( CHALLENGES ) != 0);
		Hero.preview( info, bundle.getBundle( HERO ) );
	}

	public static void fail( Class cause ) {
		if (hero.belongings.getItem( Ankh.class ) == null) {
			if (!cheated()) {
				Rankings.INSTANCE.submit(false, cause);
			}
		}
	}

	public static void win( Class cause, boolean surface ) {

		hero.belongings.identify();

		if (!cheated()) {
			MoonshinePixelDungeon.moonstones(MoonshinePixelDungeon.moonstones()+(Challenges.moonstones(challenges)*(surface?2:1)));
			if (challenges != 0) {
				Badges.validateChampion();
			}
			
			Rankings.INSTANCE.submit(true, cause);
		}
	}

	public static void observe(){
		observe( hero.viewDistance+1 );
	}
	
	public static void observe( int dist ) {
        if (level == null) {
            return;
        }
        level.updateFieldOfView(hero, visible);
		if (level.amnesia()){
            level.visited = visible;
        }
		if (hero.buff(MindVision.class) != null || hero.buff(Awareness.class) != null) {
			BArray.or( level.visited, visible, 0, visible.length, level.visited );

			GameScene.updateFog();
		} else {

			int cx = hero.pos % level.width();
			int cy = hero.pos / level.width();

			int ax = Math.max( 0, cx - dist );
			int bx = Math.min( cx + dist, level.width() - 1 );
			int ay = Math.max( 0, cy - dist );
			int by = Math.min( cy + dist, level.height() - 1 );

			int len = bx - ax + 1;
			int pos = ax + ay * level.width();

			for (int y = ay; y <= by; y++, pos+=level.width()) {
				BArray.or( level.visited, visible, pos, len, level.visited );
			}
			GameScene.updateFog(ax, ay, len, by - ay);
		}


		GameScene.afterObserve();
	}

	//we store this to avoid having to re-allocate the array with each pathfind
	private static boolean[] passable;

	private static void setupPassable(){
		if (passable == null || passable.length != Dungeon.level.length())
			passable = new boolean[Dungeon.level.length()];
		else
			BArray.setFalse(passable);
	}

	public static PathFinder.Path findPath(Char ch, int from, int to, boolean pass[], boolean[] visible ) {
		return findPath(ch,from,to,pass,visible,false);
	}
	public static PathFinder.Path findPath(Char ch, int from, int to, boolean pass[], boolean[] visible, boolean ignoreAllies ) {

		setupPassable();
		if (ch.flying || ch.buff( Amok.class ) != null) {
			BArray.or( pass, Level.getAvoid(), passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		for (Char c : Actor.chars()) {
			if (visible[c.pos]) {
				if (ignoreAllies) {
                    if (c instanceof Mob)
                        if (!((Mob) c).ally)
                            passable[c.pos] = false;
				} else
					passable[c.pos] = false;
			}
		}

		return PathFinder.find( from, to, passable );

	}
	
	public static int findStep(Char ch, int from, int to, boolean pass[], boolean[] visible ) {

		if (level.adjacent( from, to )) {
			return Actor.findChar( to ) == null && (pass[to] || Level.getAvoid(to)) ? to : -1;
		}

		setupPassable();
		if (ch.flying || ch.buff( Amok.class ) != null) {
			BArray.or( pass, Level.getAvoid(), passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}
		
		for (Char c : Actor.chars()) {
			if (visible[c.pos]) {
				passable[c.pos] = false;
			}
		}
		
		return PathFinder.getStep( from, to, passable );

	}
	
	public static int flee( Char ch, int cur, int from, boolean pass[], boolean[] visible ) {

		setupPassable();
		if (ch.flying) {
			BArray.or( pass, Level.getAvoid(), passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}
		
		for (Char c : Actor.chars()) {
			if (visible[c.pos]) {
				passable[c.pos] = false;
			}
		}
		passable[cur] = true;
		
		return PathFinder.getStepBack( cur, from, passable );
		
	}

	public static void spark(int cell){
        Blob explGas = Dungeon.level.blobs.get( ExplosiveGas.class );
		if(Blob.volumeAt(cell, ExplosiveGas.class)>0) {
            explGas.explode(cell);
        }
	}
	public static int upgLimit(){
		return Statistics.deepestFloor+1;
	}

	public static boolean hasPatch(int from, int to){
		boolean[] passable = BArray.or(Level.getPassable(),Level.getAvoid(),null);
		return PathFinder.buildDistanceMap(from,to,passable);
	}

	public static boolean cheated(){
		System.out.println(devoptions+"|"+customseed);
		return devoptions>1||customseed;
	}
}
