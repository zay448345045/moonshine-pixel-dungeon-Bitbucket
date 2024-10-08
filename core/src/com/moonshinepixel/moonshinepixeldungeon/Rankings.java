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

import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Belongings;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.items.bags.Bag;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.Potion;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.Scroll;
import com.moonshinepixel.moonshinepixeldungeon.ui.QuickSlotButton;
import com.moonshinepixel.moonshinepixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public enum Rankings {
	
	INSTANCE;
	
	public static final int TABLE_SIZE	= 11;
	
	public static final String RANKINGS_FILE = "rankings.dat";
	
	public ArrayList<Record> records;
	public static HashMap<String,Dynasty> dynasties;
	public int lastRecord;
	public int totalNumber;
	public int wonNumber;


	public static Dynasty main(){
		return new Dynasty("None","",INSTANCE.records);
	}

	public void beginDynasty(String name){
		Dynasty dyn = dynasty(name);
		dyn.active=true;
		dyn.playing=false;
		dyn.add(records.get(lastRecord));
		dyn.challenges=records.get(lastRecord).challenges;
		save();
	}

	public static ArrayList<String> activeDynastiesIDS(){
		ArrayList<String> dyns = new ArrayList<>();
		for (Dynasty d:dynasties.values()){
			if (!d.id().equals(""))
			dyns.add(d.id());
		}
		return dyns;
	}

	public static boolean[] playedDynasties(ArrayList<String> dyns){
		boolean[] ret = new boolean[dyns.size()];
		for (int i = 0; i<dyns.size(); i++){
			if (!dyns.get(i).equals(""))
			ret[i]=dynasties.get(dyns.get(i)).playing;
		}
		return ret;
	}

	public static String[] dynArListToNames(ArrayList<String> dyns){
		String[] ret = new String[dyns.size()];
		for (int i = 0; i<dyns.size(); i++){
			ret[i]=dynasties.get(dyns.get(i)).name;
		}
		return ret;
	}

	private Dynasty dynasty(String name){
		String id = UUID.randomUUID().toString();
		Dynasty din = new Dynasty(name,id);
		dynasties.put(id,din);
		return din;
	}

	public void submit( boolean win, Class cause ) {
		load();
		
		Record rec = new Record();

		Dynasty dyn = null;
		if (!Dungeon.dynastyID.equals("")){
			dyn=dynasties.get(Dungeon.dynastyID);
			dyn.add(rec);
		}

		rec.cause = cause;
		rec.win		= win;
		rec.heroClass	= Dungeon.hero.heroClass;
		rec.armorTier	= Dungeon.hero.tier();
		rec.herolevel	= Dungeon.hero.lvl;
		rec.depth		= Dungeon.fakedepth[Dungeon.depth];
		rec.challenges	= Dungeon.challenges;
		rec.score	    = score( win );

		if (dyn!=null){
			rec.score*=Math.pow(1.2,dyn.records().size());
		}

		INSTANCE.saveGameData(rec);


		rec.gameID = UUID.randomUUID().toString();
		
		records.add( rec );
		
		Collections.sort( records, scoreComparator );
		
		lastRecord = records.indexOf( rec );
		
		totalNumber++;
		if (win) {
			wonNumber++;
		}

		Badges.validateGamesPlayed();

		save();
	}

	private int score( boolean win ) {
		return (int)((Statistics.goldCollected + Dungeon.hero.lvl * (win ? 26 : Dungeon.fakedepth[Dungeon.depth] ) * 100) * (win ? 2 : 1) * (Challenges.score(Dungeon.challenges)));
	}

	public static final String HERO = "hero";
	public static final String STATS = "stats";
	public static final String BADGES = "badges";
	public static final String HANDLERS = "handlers";

	public void saveGameData(Record rec){
		rec.gameData = new Bundle();

		rec.version=Dungeon.version;
		rec.versionName=MoonshinePixelDungeon.version;

		Belongings belongings = Dungeon.hero.belongings;

		//save the hero and belongings
		ArrayList<Item> allItems = (ArrayList<Item>) belongings.backpack.items.clone();
		//remove items that won't show up in the rankings screen
		for (Item item : belongings.backpack.items.toArray( new Item[0])) {
			if (item instanceof Bag){
				for (Item bagItem : ((Bag) item).items.toArray( new Item[0])){
					if (Dungeon.quickslot.contains(bagItem)) belongings.backpack.items.add(bagItem);
				}
				belongings.backpack.items.remove(item);
			} else if (!Dungeon.quickslot.contains(item))
				belongings.backpack.items.remove(item);
		}
		rec.gameData.put( HERO, Dungeon.hero );

		//save stats
		Bundle stats = new Bundle();
		Statistics.storeInBundle(stats);
		rec.gameData.put( STATS, stats);

		//save badges
		Bundle badges = new Bundle();
		Badges.saveLocal(badges);
		rec.gameData.put( BADGES, badges);

		//save handler information
		Bundle handler = new Bundle();
		Scroll.saveSelectively(handler, belongings.backpack.items);
		Potion.saveSelectively(handler, belongings.backpack.items);
		//include worn rings
		if (belongings.misc1 != null) belongings.backpack.items.add(belongings.misc1);
		if (belongings.misc2 != null) belongings.backpack.items.add(belongings.misc2);
		Ring.saveSelectively(handler, belongings.backpack.items);
		rec.gameData.put( HANDLERS, handler);

		rec.seed=Dungeon.seed;
		//restore items now that we're done saving
		belongings.backpack.items = allItems;
	}

	public void loadGameData(Record rec){
		Bundle data = rec.gameData;

		Dungeon.hero = null;
		Dungeon.level = null;
		Generator.reset();
		Dungeon.quickslot.reset();
		QuickSlotButton.reset();

		Bundle handler = data.getBundle(HANDLERS);
		Scroll.restore(handler);
		Potion.restore(handler);
		Ring.restore(handler);

		Badges.loadLocal(data.getBundle(BADGES));

		Dungeon.hero = (Hero)data.get(HERO);

		Dungeon.challenges = rec.challenges;

		//System.out.println(rec.challenges);

		Statistics.restoreFromBundle(data.getBundle(STATS));
		
		Dungeon.challenges = rec.challenges;
		Dungeon.seed = rec.seed;

	}

	private static final String RECORDS	= "records";
	private static final String DYNASTIES= "dynasties";
	private static final String LATEST	= "latest";
	private static final String TOTAL	= "total";
	private static final String WON     = "won";

	public void save() {
		Bundle bundle = new Bundle();
		bundle.put( RECORDS, records );
		bundle.put( DYNASTIES, dynasties.values() );
		bundle.put( LATEST, lastRecord );
		bundle.put( TOTAL, totalNumber );
		bundle.put( WON, wonNumber );

		try {
			OutputStream output = Game.instance.openFileOutput( RANKINGS_FILE );
			Bundle.write( bundle, output );
			output.close();
		} catch (IOException e) {
			MoonshinePixelDungeon.reportException(e);
		}

	}
	
	public void load() {
		
		if (records != null&&dynasties!=null) {
			return;
		}
		
		records = new ArrayList<>();
		dynasties = new HashMap<>();

		try {
			InputStream input = Game.instance.openFileInput( RANKINGS_FILE );
			Bundle bundle = Bundle.read( input );
			input.close();
			
			for (Bundlable record : bundle.getCollection( RECORDS )) {
				records.add( (Record)record );
			}

			if (bundle.contains(DYNASTIES)) {
				for (Bundlable dynasty : bundle.getCollection(DYNASTIES)) {
					dynasties.put(((Dynasty) dynasty).ID, (Dynasty) dynasty);
				}
			}

			lastRecord = bundle.getInt( LATEST );
			
			totalNumber = bundle.getInt( TOTAL );
			if (totalNumber == 0) {
				totalNumber = records.size();
			}

			wonNumber = bundle.getInt( WON );
			if (wonNumber == 0) {
				for (Record rec : records) {
					if (rec.win) {
						wonNumber++;
					}
				}
			}

			dynasties.put("",main());
		} catch (IOException e) {
		}
	}

	public static class Record implements Bundlable {
		
		//pre 0.4.1
		public String gameFile;
		private static final String FILE    = "gameFile";

		private static final String CAUSE   = "cause";
		private static final String WIN		= "win";
		private static final String SCORE	= "score";
		private static final String TIER	= "tier";
		private static final String LEVEL	= "level";
		private static final String DEPTH	= "depth";
		private static final String DATA	= "gameData";
		private static final String ID      = "gameID";
		private static final String CHALLENGES= "challenges";
		private static final String NAME	= "name";
		private static final String SEED	= "seed";
		private static final String VERSION	= "ver";
		private static final String VERSIONC= "verc";
		private static final String DYNASTY = "dynasty";

		public Class cause;
		public boolean win;
		public String name;
		public String dynasty="";

		public HeroClass heroClass;
		public int armorTier;
		public int herolevel;
		public int depth;
		public int challenges;
		public long seed;

		public Bundle gameData;
		public String gameID;

		public int score;
		public int version;
		public String versionName;

		public String desc(){
			if (cause == null) {
				return Messages.get(this, "something");
			} else {
				String result = Messages.get(cause, "rankings_desc", (Messages.get(cause, "name")));
				if (result.contains("!!!NO TEXT FOUND!!!")){
					return Messages.get(this, "something");
				} else {
					return result;
				}
			}
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {

			if (bundle.contains( CAUSE )) {
				cause   = bundle.getClass( CAUSE );
			} else {
				cause = null;
			}

			if (bundle.contains(DYNASTY))dynasty=bundle.getString(DYNASTY);

			win		= bundle.getBoolean( WIN );
			score	= bundle.getInt( SCORE );

			heroClass	= HeroClass.restoreInBundle( bundle );
			armorTier	= bundle.getInt( TIER );

			if (bundle.contains(FILE))  gameFile = bundle.getString(FILE);
			if (bundle.contains(DATA))  gameData = bundle.getBundle(DATA);
			if (bundle.contains(ID))    gameID = bundle.getString(ID);

			depth = bundle.getInt( DEPTH );
			challenges = bundle.getInt( CHALLENGES );
			if(bundle.contains(SEED)) {
				seed = bundle.getLong(SEED);
			} else {
				seed = DungeonSeed.convertFromCode("NODATAERR");
			}
			herolevel = bundle.getInt( LEVEL );
			name = bundle.getString( NAME );
			if (bundle.contains(VERSIONC)){
				version=bundle.getInt(VERSIONC);
			} else {
				version=-1;
			}
			if (bundle.contains(VERSION)){
				versionName=bundle.getString(VERSION);
			} else {
				versionName="Unknown";
			}
		}

		@Override
		public void storeInBundle( Bundle bundle ) {

			if (cause != null) bundle.put( CAUSE, cause );

			bundle.put( WIN, win );
			bundle.put( SCORE, score );

			heroClass.storeInBundle( bundle );
			bundle.put( TIER, armorTier );
			bundle.put( LEVEL, herolevel );
			bundle.put( DEPTH, depth );
			bundle.put( CHALLENGES, challenges );
			bundle.put( NAME, name);
			bundle.put( SEED, seed);
			bundle.put( VERSION, versionName);
			bundle.put( VERSIONC, version);
			bundle.put( DYNASTY, dynasty);

			if (gameData != null) bundle.put( DATA, gameData );
			bundle.put( ID, gameID );
		}
	}

	public static class Dynasty implements Bundlable {

		public boolean active = false;
		public boolean playing = false;
		public int 	   challenges = 0;

		public Dynasty(){
			this("","");
		}

		public Dynasty( String name,  String id){
			this(name,id,null);
		}

		public Dynasty( String name,  String id, ArrayList<Record> records){
			this.name=name;
			this.ID =id;
			this.records=records!=null?records:new ArrayList<Record>();
		}

		private ArrayList<Record> records;

		private String name;
		private int score=-1;
		private boolean messy = false;
		private String ID;

		public int score(){
			if (score==-1||messy){
				score=0;
				messy=false;
				for (Record r:records)score+=r.score;
			}
			return score;
		}

		public String name() {
			return name;
		}

		public Dynasty add(Record r){
			records.add(r);
			messy=true;
			return this;
		}

		public String id() {
			return ID;
		}

		public ArrayList<Record> records() {
			return (ArrayList<Record>)records.clone();
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			bundle.put( RECORDS, records );
			bundle.put( "name", name );
			bundle.put( "ID", ID);
			bundle.put( "act", active);
			bundle.put( "pln", playing);
			bundle.put( "cln", challenges);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			records=new ArrayList<>();
			for (Bundlable record : bundle.getCollection( RECORDS )) {
				add( (Record)record );
			}
			score();
			name=bundle.getString("name");
			ID=bundle.getString("ID");
			active=bundle.getBoolean("active");
			playing=bundle.getBoolean("pln");
			challenges=bundle.getInt("cln");
		}
	}

	private static final Comparator<Record> scoreComparator = new Comparator<Rankings.Record>() {
		@Override
		public int compare( Record lhs, Record rhs ) {
			return (int)Math.signum( rhs.score - lhs.score );
		}
	};
}
