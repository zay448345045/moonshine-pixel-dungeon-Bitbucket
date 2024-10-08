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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.input.PDInputProcessor;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.standard.RitualSiteRoom;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.scenes.TitleScene;
import com.moonshinepixel.moonshinepixeldungeon.scenes.WelcomeScene;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.items.food.SmallRation;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.Shortsword;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.MassGraveRoom;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.moonshinepixel.moonshinepixeldungeon.messages.Languages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndError;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PDPlatformSupport;

import java.util.Locale;

public class MoonshinePixelDungeon extends Game<GameAction> {
	
	//old moonsh
	public static final int v0_0_0  = 41;
	public static final int v0_1_25  = 65;
	public static final int v0_1_32  = 72;
	public static final int v0_1_33  = 73;

	public MoonshinePixelDungeon(final PDPlatformSupport<GameAction> platformSupport) {
		super(WelcomeScene.class, platformSupport);

		Game.version = platformSupport.getVersion();
		Game.versionCode = platformSupport.getVersionCode();
		Game.previewmode=platformSupport.isPreviewmode();
		//v0.6.0
		com.watabou.utils.Bundle.addAlias(
				MassGraveRoom.Bones.class,
				"com.moonshinepixel.moonshinepixeldungeon.levels.painters.MassGravePainter$Bones" );
		com.watabou.utils.Bundle.addAlias(
				RitualSiteRoom.RitualMarker.class,
				"com.moonshinepixel.moonshinepixeldungeon.levels.painters.RitualSitePainter$RitualMarker" );
		com.watabou.utils.Bundle.addAlias(
				WeakFloorRoom.HiddenWell.class,
				"com.moonshinepixel.moonshinepixeldungeon.levels.painters.WeakFloorPainter$HiddenWell" );
		com.watabou.utils.Bundle.addAlias(
				Room.class,
				"com.moonshinepixel.moonshinepixeldungeon.levels.Room" );
		com.watabou.utils.Bundle.addAlias(
				Shortsword.class,
				"com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee.NewShortsword" );
		
		//v0.6.0a
		com.watabou.utils.Bundle.addAlias(
				SmallRation.class,
				"com.moonshinepixel.moonshinepixeldungeon.items.food.OverpricedRation" );

		com.watabou.utils.Bundle.exceptionReporter =
				new com.watabou.utils.Bundle.BundleExceptionCallback() {
					@Override
					public void call(Throwable t) {
						MoonshinePixelDungeon.reportException(t);
					}
				};
		fillerScene= TitleScene.class;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void create() {
		super.create();

		boolean landscape = Gdx.graphics.getWidth() > Gdx.graphics.getHeight();

		final Preferences prefs = Preferences.INSTANCE;
		if (prefs.getBoolean(Preferences.KEY_LANDSCAPE, false) != landscape) {
			landscape(!landscape);
		}
		fullscreen( prefs.getBoolean(Preferences.KEY_WINDOW_FULLSCREEN, false) );
		
		Music.INSTANCE.enable( music() );
		Sample.INSTANCE.enable( soundFx() );
		Sample.INSTANCE.volume( SFXVol()/10f );

		Sample.INSTANCE.load(
				Assets.SND_CLICK,
				Assets.SND_BADGE,
				Assets.SND_GOLD,

				Assets.SND_STEP,
				Assets.SND_WATER,
				Assets.SND_OPEN,
				Assets.SND_UNLOCK,
				Assets.SND_ITEM,
				Assets.SND_DEWDROP,
				Assets.SND_HIT,
				Assets.SND_MISS,

				Assets.SND_DESCEND,
				Assets.SND_EAT,
				Assets.SND_READ,
				Assets.SND_LULLABY,
				Assets.SND_DRINK,
				Assets.SND_SHATTER,
				Assets.SND_ZAP,
				Assets.SND_LIGHTNING,
				Assets.SND_LEVELUP,
				Assets.SND_DEATH,
				Assets.SND_CHALLENGE,
				Assets.SND_CURSED,
				Assets.SND_EVOKE,
				Assets.SND_TRAP,
				Assets.SND_TOMB,
				Assets.SND_ALERT,
				Assets.SND_MELD,
				Assets.SND_BOSS,
				Assets.SND_BLAST,
				Assets.SND_PLANT,
				Assets.SND_RAY,
				Assets.SND_BEACON,
				Assets.SND_TELEPORT,
				Assets.SND_CHARMS,
				Assets.SND_MASTERY,
				Assets.SND_PUFF,
				Assets.SND_ROCKS,
				Assets.SND_BURNING,
				Assets.SND_FALLING,
				Assets.SND_GHOST,
				Assets.SND_SECRET,
				Assets.SND_BONES,
				Assets.SND_BEE,
				Assets.SND_DEGRADE,
				Assets.SND_MIMIC );

		if (classicFont()) {
			RenderedText.setFont("pixelfont.ttf");
		} else {
			RenderedText.setFont( null );
		}

		ScreenOrientation(ScreenOrientation());
		Gdx.input.setCatchBackKey(true);

//		Unlocks.unlock(Unlocks.CHALLENGES);
//		Unlocks.unlock(Unlocks.ITEMRENAMING);
//		Unlocks.unlock(Unlocks.INVULNERABILITY);
//		Unlocks.unlock(Unlocks.TOMSTART);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
		boolean maximized = width >= mode.width || height >= mode.height;

		if (!maximized && !fullscreen()) {
			final Preferences prefs = Preferences.INSTANCE;
			prefs.put(Preferences.KEY_WINDOW_WIDTH, width);
			prefs.put(Preferences.KEY_WINDOW_HEIGHT, height);
		}
	}
	/*
	 * ---> Prefernces
	 */
	
	public static void landscape( boolean value ) {
		// FIXME
//		Game.instance.setRequestedOrientation( value ?
//			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
//			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
//		Preferences.INSTANCE.put( Preferences.KEY_LANDSCAPE, value );
	}
	
	public static boolean landscape() {
		return width > height;
	}

	public static void fullscreen(boolean value) {
		final Preferences prefs = Preferences.INSTANCE;
		if (value) {
			prefs.put(Preferences.KEY_WINDOW_FULLSCREEN, true);

			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			int w = prefs.getInt(Preferences.KEY_WINDOW_WIDTH, Preferences.DEFAULT_WINDOW_WIDTH);
			int h = prefs.getInt(Preferences.KEY_WINDOW_HEIGHT, Preferences.DEFAULT_WINDOW_HEIGHT);
			prefs.put(Preferences.KEY_WINDOW_FULLSCREEN, false);
			Gdx.graphics.setWindowedMode(w, h);
		}
	}

	public static boolean fullscreen() {
		return Gdx.graphics.isFullscreen();
	}
	
	public static void scale( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_SCALE, value );
	}
	
	public static int scale() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_SCALE, 0 );
	}

	public static void zoom( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_ZOOM, value );
	}
	
	public static int zoom() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_ZOOM, 0 );
	}
	
	public static void music( boolean value ) {
		Music.INSTANCE.enable( value );
		Music.INSTANCE.volume( musicVol()/10f );
		Preferences.INSTANCE.put( Preferences.KEY_MUSIC, value );
	}
	
	public static boolean music() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_MUSIC, true );
	}

	public static void musicVol( int value ){
		Preferences.INSTANCE.put( Preferences.KEY_MUSIC_VOL, value );
	}

	public static int musicVol(){
		return Preferences.INSTANCE.getInt( Preferences.KEY_MUSIC_VOL, 10, 0, 10 );
	}
	
	public static void soundFx( boolean value ) {
		Sample.INSTANCE.enable( value );
		Preferences.INSTANCE.put( Preferences.KEY_SOUND_FX, value );
	}
	
	public static boolean soundFx() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_SOUND_FX, true );
	}

	public static void SFXVol( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_SFX_VOL, value );
	}

	public static int SFXVol() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_SFX_VOL, 10, 0, 10 );
	}
	
	public static void brightness( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_BRIGHTNESS, value );
		GameScene.updateFog();
	}
	
	public static int brightness() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_BRIGHTNESS, 0, -2, 2 );
	}

	public static void visualGrid( int value ){
		Preferences.INSTANCE.put( Preferences.KEY_GRID, value );
		GameScene.updateMap();
	}

	public static int visualGrid() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_GRID, 0, -1, 3 );
	}

	public static void language(Languages lang) {
		Preferences.INSTANCE.put( Preferences.KEY_LANG, lang.code());
	}

	public static Languages language() {
		String code = Preferences.INSTANCE.getString(Preferences.KEY_LANG, null);
		if (code == null){
			Languages lang = Languages.matchLocale(Locale.getDefault());
			if (lang.status() == Languages.Status.REVIEWED)
				return lang;
			else
				return Languages.ENGLISH;
		}
		else return Languages.matchCode(code);
	}

	public static void classicFont(boolean classic){
		Preferences.INSTANCE.put(Preferences.KEY_CLASSICFONT, classic);
		if (classic) {
			RenderedText.setFont("pixelfont.ttf");
		} else {
			RenderedText.setFont( null );
		}
	}

	public static boolean classicFont(){
		return Preferences.INSTANCE.getBoolean(Preferences.KEY_CLASSICFONT,
				(language() != Languages.KOREAN && language() != Languages.CHINESE));
	}

	public static void lastClass( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_LAST_CLASS, value );
	}
	
	public static int lastClass() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_LAST_CLASS, 0, 0, 4 );
	}
	public static void lastSlot( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_LAST_SLOT, value );
	}

	public static int lastSlot() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_LAST_SLOT, 0, 0, Integer.MAX_VALUE );
	}

	public static void challenges( int value ) {
		Unlocks.put( Preferences.KEY_CHALLENGES, value );
	}

	public static int challenges() {
		return Unlocks.getInt( Preferences.KEY_CHALLENGES, 0, 0, Challenges.MAX_VALUE );
	}

	public static int challenges_OLD() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_CHALLENGES, 0, 0, Challenges.MAX_VALUE );
	}

	public static void quickSlots( int value ){ Preferences.INSTANCE.put( Preferences.KEY_QUICKSLOTS, value ); }

	public static int quickSlots(){
		if (Gdx.app.getType() == Application.ApplicationType.Desktop){
			return 4;
		} else {
			return Preferences.INSTANCE.getInt(Preferences.KEY_QUICKSLOTS, 4, 0, 4);
		}
	}

	@Override
	public void wndError(Exception e) {
		scene().add(new WndError("There was an error during changing scene:\n"+e.toString()));
		MoonshinePixelDungeon.reportException(e);
	}

	public static void flipToolbar(boolean value) {
		Preferences.INSTANCE.put(Preferences.KEY_FLIPTOOLBAR, value );
	}

	public static boolean flipToolbar(){ return Preferences.INSTANCE.getBoolean(Preferences.KEY_FLIPTOOLBAR, false); }

	public static void flipTags( boolean value) {
		Preferences.INSTANCE.put(Preferences.KEY_FLIPTAGS, value );
	}

	public static boolean flipTags(){ return Preferences.INSTANCE.getBoolean(Preferences.KEY_FLIPTAGS, false); }

	public static void toolbarMode( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_BARMODE, value );
	}

	public static String toolbarMode() {
		return Preferences.INSTANCE.getString(Preferences.KEY_BARMODE, !landscape() ? "SPLIT" : "GROUP");
	}
	
	public static void intro( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_INTRO, value );
	}
	
	public static boolean intro() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_INTRO, true );
	}

	public static void version( int value)  {
		Preferences.INSTANCE.put( Preferences.KEY_VERSION, value );
	}

	public static int version() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_VERSION, 0 );
	}

	public static void switchNoFade( Class<? extends PixelScene> c ) {
		PixelScene.noFade = true;
		switchScene( c );
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}

	public static void lastGender( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_LASTGENDER, value );
	}

	public static int lastGender() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_LASTGENDER, 0, 0, 1 );
	}


	public static void devOptions( int value ) {
		Unlocks.put( Preferences.KEY_DEVOPTIONS, value );
	}

	public static int devOptions() {
		return Unlocks.getInt( Preferences.KEY_DEVOPTIONS, 0, Integer.MIN_VALUE, Integer.MAX_VALUE );
	}

	public static int devOptions_OLD() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_DEVOPTIONS, 0, Integer.MIN_VALUE, Integer.MAX_VALUE );
	}

	public static void storyline( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_STORYLINE, value );
	}

	public static int storyline() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_STORYLINE, 0,0, previewmode?2:1 );
	}

	public static int devlevel(){
		return Game.previewmode?9:1;
	}

	public static String heroName() {
		return Preferences.INSTANCE.getString( Preferences.KEY_NAME, Messages.get(Hero.class, "name") );
	}

	public static void heroName(String value){
		Preferences.INSTANCE.put( Preferences.KEY_NAME, value );
	}

	public static void ScreenOrientation(boolean landcape){
		Preferences.INSTANCE.put(Preferences.KEY_ORIENTATION,landcape);
		((PDInputProcessor)Game.instance.getInputProcessor()).rotate(landcape);
	}

	public static boolean ScreenOrientation(){
		return Preferences.INSTANCE.getBoolean(Preferences.KEY_ORIENTATION,false);
	}
	public static void updateChecker( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_UPDATECHECK, value );
	}

	public static boolean updateChecker() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_UPDATECHECK, false );
	}

	public static void hideUpdNot( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_UPDATECVIS, value );
	}

	public static boolean hideUpdNot() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_UPDATECVIS, false );
	}

	public static void infoLevel( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_INFOSHOWN, value );
	}

	public static int infoLevel() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_INFOSHOWN, 0, 0, Integer.MAX_VALUE );
	}

	public static void startInGame( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_STARTINGAME, value );
	}

	public static boolean startInGame() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_STARTINGAME, false );
	}

	public static void hudType( int value ) {
		Preferences.INSTANCE.put( Preferences.KEY_HUDTYPE, value );
	}

	public static int hudType() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_HUDTYPE, 0, 0, Integer.MAX_VALUE );
	}

	public static int hudType_OLD() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_HUDTYPE, 0, 0, Integer.MAX_VALUE );
	}

	public static void buttonType( int value ) {
		Unlocks.put( Preferences.KEY_BUTTONTYPE, value );
	}

	public static int buttonType() {
		return Unlocks.getInt( Preferences.KEY_BUTTONTYPE, 0, 0, Integer.MAX_VALUE );
	}

	public static int buttonType_OLD() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_BUTTONTYPE, 0, 0, Integer.MAX_VALUE );
	}

	public static void unlocks( int value ) {
		Unlocks.put( Preferences.KEY_UNLOCKS, value );
	}

	public static int unlocks() {
		return Unlocks.getInt( Preferences.KEY_UNLOCKS, 0, 0, Integer.MAX_VALUE );
	}

	public static int unlocks_OLD() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_UNLOCKS, 0, 0, Integer.MAX_VALUE );
	}

	public static void moonstones( int value ) {
		Unlocks.put( Preferences.KEY_MOONSTONES, value );
	}

	public static int moonstones() {
		return Unlocks.getInt( Preferences.KEY_MOONSTONES, 0, 0, Integer.MAX_VALUE );
	}

	public static int moonstones_OLD() {
		return Preferences.INSTANCE.getInt( Preferences.KEY_MOONSTONES, 0, 0, Integer.MAX_VALUE );
	}

	public static void seed( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_SEED, value );
	}

	public static String seed() {
		return Preferences.INSTANCE.getString( Preferences.KEY_SEED, "" );
	}

	public static void customSeed( boolean value ) {
		Preferences.INSTANCE.put( Preferences.KEY_CUSTOMSEED, value );
	}

	public static boolean customSeed() {
		return Preferences.INSTANCE.getBoolean( Preferences.KEY_CUSTOMSEED, false );
	}

	public static void dynasty( String value ) {
		Preferences.INSTANCE.put( Preferences.KEY_DYNASTY, value );
	}

	public static String dynasty() {
		return Preferences.INSTANCE.getString( Preferences.KEY_DYNASTY, "" );
	}

	/*
	 * <--- Preferences
	 */

	public static void reportException( Throwable tr ) {
		Gdx.app.error("PD", tr.getMessage(), tr);
		tr.printStackTrace();
	}

}