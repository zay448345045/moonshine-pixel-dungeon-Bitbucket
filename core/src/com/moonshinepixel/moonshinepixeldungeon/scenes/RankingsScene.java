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
package com.moonshinepixel.moonshinepixeldungeon.scenes;

import com.moonshinepixel.moonshinepixeldungeon.*;
import com.moonshinepixel.moonshinepixeldungeon.effects.Flare;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.ui.*;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndRanking;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndError;
import com.watabou.noosa.*;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Button;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

public class RankingsScene extends PixelScene {

	private static final float ROW_HEIGHT_MAX	= 20;
	private static final float ROW_HEIGHT_MIN	= 12;

	private static final float MAX_ROW_WIDTH    = 160;

	private static final float GAP	= 4;
	
	private Archs archs;

	@Override
	public void create() {
		
		super.create();
		
		Music.INSTANCE.play( Assets.THEME, true );
		Music.INSTANCE.volume( MoonshinePixelDungeon.musicVol() / 10f );

		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		archs = new Archs();
		archs.setSize( w, h );
		add( archs );
		
		Rankings.INSTANCE.load();

		RenderedText title = renderText( Messages.get(this, "title"), 9);
		title.hardlight(Window.SHPX_COLOR);
		title.x = (w - title.width()) / 2;
		title.y = GAP;
		align(title);
		add(title);
		
		if (Rankings.INSTANCE.records.size() > 0) {

			//attempts to give each record as much space as possible, ideally as much space as portrait mode
//			float rowHeight = GameMath.gate(ROW_HEIGHT_MIN, (uiCamera.height - 26)/Rankings.INSTANCE.records.size(), ROW_HEIGHT_MAX);
			float rowHeight = 20;

			float left = (w - Math.min( MAX_ROW_WIDTH, w )) / 2 + GAP;
//			float top = (h - rowHeight  * Rankings.INSTANCE.records.size()) / 2;
			float top = 14;

			int pos = 0;

			NinePatch panel = Chrome.get(Chrome.Type.TOAST);

			panel.size(w - left,h-(title.y+title.height-8));
			panel.x=(w-panel.width)/2;
			panel.y=top+2;
			add(panel);
			top+=panel.marginTop();
			ScrollPane pane = new ScrollPane(new Component());
			Component ranks = pane.content();
			ranks.clear();
			add(pane);

			for (Rankings.Record rec : Rankings.INSTANCE.records) {
				Record row = new Record( pos, pos == Rankings.INSTANCE.lastRecord, rec );
				float offset =
						rowHeight <= 14 ?
								pos %2 == 1?
										5 :
										-5
								: 0;
				if (MoonshinePixelDungeon.landscape()) {
					row.setRect(0, /*top + */pos * rowHeight, w - left * 2, rowHeight);
					row.setPos(panel.innerWidth() / 2 - row.width() / 2, row.top());
				} else {
					row.setRect(0, 2+pos * rowHeight, (panel.innerWidth()-pane.thumbWidth()*2)*.85f, rowHeight/2);
					row.setPos(0, row.top());
				}
				ranks.add(row);
				
				pos++;
			}

			ranks.setSize(panel.innerWidth(),(int)Math.ceil(pos*rowHeight));
			pane.setRect(panel.x+panel.marginLeft(),panel.y+panel.marginTop(),panel.innerWidth(),panel.innerHeight());

			RenderedText label = renderText( Messages.get(this, "total") + " ", 8 );
			label.hardlight( 0xCCCCCC );
			add( label );

			RenderedText won = renderText( Integer.toString( Rankings.INSTANCE.wonNumber ), 8 );
			won.hardlight( Window.SHPX_COLOR );
			add( won );

			RenderedText total = renderText( "/" + Rankings.INSTANCE.totalNumber, 8 );
			total.hardlight( 0xCCCCCC );
			total.x = (w - total.width()) / 2;
			total.y = top + pos * rowHeight + GAP;
			add( total );

			float tw = label.width() + won.width() + total.width();
			label.x = (w - tw) / 2;
			won.x = label.x + label.width();
			total.x = won.x + won.width();
			label.y = won.y = total.y = h - label.height() - GAP;

			align(label);
			align(total);
			align(won);

		} else {

			RenderedText noRec = renderText(Messages.get(this, "no_games"), 8);
			noRec.hardlight( 0xCCCCCC );
			noRec.x = (w - noRec.width()) / 2;
			noRec.y = (h - noRec.height()) / 2;
			align(noRec);
			add(noRec);

		}

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		MoonshinePixelDungeon.switchNoFade(TitleScene.class);
	}
	
	public static class Record extends Button<GameAction> {
		
		private static final float GAP	= 4;
		
		private static final int[] TEXT_WIN	= {0xFFFF88, 0xB2B25F};
		private static final int[] TEXT_LOSE= {0xDDDDDD, 0x888888};
		private static final int FLARE_WIN	= 0x888866;
		private static final int FLARE_LOSE	= 0x666666;
		
		private Rankings.Record rec;
		
		protected ItemSprite shield;
		private Flare flare;
		private BitmapText position;
		private RenderedTextMultiline desc;
		private Image steps;
		private BitmapText depth;
		private Image classIcon;
		private BitmapText level;

		public Record( int pos, boolean latest, Rankings.Record rec ) {
			super();

			this.rec = rec;

			if (latest) {
				flare = new Flare( 6, 24 );
				flare.angularSpeed = 90;
				flare.color( rec.win ? ColorMath.interpolate(FLARE_WIN,0xFF0000, (Challenges.score(rec.challenges)-1)/(Challenges.score(Challenges.MAX_VALUE))) : ColorMath.interpolate(FLARE_LOSE,0xFF0000, (Challenges.score(rec.challenges)-1)/(Challenges.score(Challenges.MAX_VALUE)-1)) );
				addToBack( flare );
			}

			if (pos != -1) {
				position.text(Integer.toString(pos + 1));
			} else
				position.text(" ");
			position.measure();

			desc.text( Messages.titleCase(rec.desc()) );

			int odd = pos % 2;

			if (rec.win) {
				shield.view( ItemSpriteSheet.AMULET, null );
				position.hardlight( TEXT_WIN[odd] );
				desc.hardlight( TEXT_WIN[odd] );
				depth.hardlight( TEXT_WIN[odd] );
				level.hardlight( TEXT_WIN[odd] );
			} else {
				position.hardlight( TEXT_LOSE[odd] );
				desc.hardlight( TEXT_LOSE[odd] );
				depth.hardlight( TEXT_LOSE[odd] );
				level.hardlight( TEXT_LOSE[odd] );

				if (rec.depth != 0){
					depth.text( Integer.toString(rec.depth) );
					depth.measure();
					steps.copy(Icons.DEPTH_LG.get());

					add(steps);
					add(depth);
				}

			}

			if (rec.herolevel != 0){
				level.text( Integer.toString(rec.herolevel) );
				level.measure();
				add(level);
			}

			classIcon.copy( Icons.get( rec.heroClass ) );
		}

		@Override
		protected void createChildren() {

			super.createChildren();

			int size = MoonshinePixelDungeon.landscape()?7:6;

			shield = new ItemSprite( ItemSpriteSheet.TOMB, null );
			add( shield );

			position = new BitmapText(pixelFont);
			position.alpha(0.8f);
			add( position );

			desc = renderMultiline( size );
			add( desc );

			depth = new BitmapText(pixelFont);
			depth.alpha(0.8f);

			steps = new Image();

			classIcon = new Image();
			add( classIcon );

			level = new BitmapText(pixelFont);
			level.alpha(0.8f);
		}

		@Override
		protected void layout() {

			super.layout();

			shield.x = x;
			shield.y = y + (height - shield.height()) / 2f;
			align(shield);

			position.x = shield.x + (shield.width - position.width()) / 2f;
			position.y = shield.y + (shield.height - position.height()) / 2f + 1;
			align(position);

			if (flare != null) {
				flare.point( shield.center() );
			}

			classIcon.x = x + width - classIcon.width;
			classIcon.y = shield.y;

			level.x = classIcon.x + (classIcon.width - level.width()) / 2f;
			level.y = classIcon.y + (classIcon.height - level.height()) / 2f + 1;
			align(level);

			steps.x = x + width - steps.width - classIcon.width;
			steps.y = shield.y;

			depth.x = steps.x + (steps.width - depth.width()) / 2f;
			depth.y = steps.y + (steps.height - depth.height()) / 2f + 1;
			align(depth);

			desc.maxWidth((int)(steps.x - (shield.x + shield.width + GAP)));
			desc.setPos(shield.x + shield.width + GAP, shield.y + (shield.height - desc.height()) / 2f + 1);
			align(desc);
		}

		@Override
		protected void onClick() {
			if (rec.gameData != null) {
				parent.add( new WndRanking( rec ) );
			} else {
				parent.add( new WndError( Messages.get(RankingsScene.class, "no_info") ) );
			}
		}
	}
}
