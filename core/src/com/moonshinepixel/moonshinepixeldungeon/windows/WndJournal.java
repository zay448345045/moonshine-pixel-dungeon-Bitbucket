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
package com.moonshinepixel.moonshinepixeldungeon.windows;

import com.moonshinepixel.moonshinepixeldungeon.Journal;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.GoldenKey;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.IronKey;
import com.moonshinepixel.moonshinepixeldungeon.items.keys.SkeletonKey;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.RenderedTextMultiline;
import com.moonshinepixel.moonshinepixeldungeon.ui.ScrollPane;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.ui.Icons;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.Collections;

public class WndJournal extends Window {

	private static final int WIDTH      = 112;
	private static final int HEIGHT     = 160;

	private static final int ITEM_HEIGHT	= 17;
	
	private RedButton btnTitle;
	private RedButton btnCatalogues;
	private ScrollPane list;
	
	public WndJournal() {
		
		super();
		resize( WIDTH, HEIGHT );

		//does nothing, we're already in the journal
		btnTitle = new RedButton( Messages.get(this, "title"), 9 );
		btnTitle.textColor( Window.TITLE_COLOR );
		btnTitle.setRect(0, 0, WIDTH/2f - 1, btnTitle.reqHeight());
		PixelScene.align(btnTitle);
		add( btnTitle );

		btnCatalogues = new RedButton( Messages.get(WndCatalogs.class, "title"), 9 ){
			@Override
			protected void onClick() {
				hide();
				GameScene.show(new WndCatalogs());
			}
		};
		btnCatalogues.setRect(WIDTH/2f + 1, 0, WIDTH/2f - 1, btnCatalogues.reqHeight());
		PixelScene.align( btnCatalogues );
		add( btnCatalogues );
		
		Component content = new Component();
		
		Collections.sort( Journal.records );
		
		float pos = 0;

		//Keys
		for (int i = Dungeon.hero.belongings.ironKeys.length-1; i > 0; i--){
			if (Dungeon.hero.belongings.specialKeys[i] > 0){
				String text;
				if (i % 5 == 0)
					text = Messages.capitalize(Messages.get(SkeletonKey.class, "name"));
				else
					text = Messages.capitalize(Messages.get(GoldenKey.class, "name"));

				if (Dungeon.hero.belongings.specialKeys[i] > 1){
					text += " x" + Dungeon.hero.belongings.specialKeys[i];
				}
				ListItem item = new ListItem( Messages.titleCase(text), i );
				item.setRect( 0, pos, WIDTH, ITEM_HEIGHT );
				content.add( item );

				pos += item.height();
			}
			if (Dungeon.hero.belongings.ironKeys[i] > 0){
				String text = Messages.titleCase(Messages.get(IronKey.class, "name"));

				if (Dungeon.hero.belongings.ironKeys[i] > 1){
					text += " x" + Dungeon.hero.belongings.ironKeys[i];
				}

				ListItem item = new ListItem( text, i );
				item.setRect( 0, pos, WIDTH, ITEM_HEIGHT );
				content.add( item );

				pos += item.height();
			}

		}

		//Journal entries
		for (Journal.Record rec : Journal.records) {
			ListItem item = new ListItem( rec.feature.desc(), rec.depth );
			item.setRect( 0, pos, WIDTH, ITEM_HEIGHT );
			content.add( item );
			
			pos += item.height();
		}
		
		content.setSize( WIDTH, pos );
		
		list = new ScrollPane( content );
		add( list );

		list.setRect( 0, btnTitle.height() + 1, WIDTH, height - btnTitle.height() - 1 );
	}

	@Override
	protected void onKeyUp( NoosaInputProcessor.Key<GameAction> key ) {
		try {
			if (key.action == GameAction.JOURNAL) {
				hide();
			} else {
				super.onKeyUp(key);
			}
		} catch (Throwable ignored){

		}
	}
	
	private static class ListItem extends Component {
		
		private RenderedTextMultiline feature;
		private BitmapText depth;
		private ColorBlock line;
		private Image icon;
		
		public ListItem( String text, int d ) {
			super();
			
			feature.text( text );
			
			depth.text( Dungeon.showDepth[d] );
			depth.measure();
			
			if (d == Dungeon.depth) {
				feature.hardlight( TITLE_COLOR );
				depth.hardlight( TITLE_COLOR );
			}
		}
		
		@Override
		protected void createChildren() {
			feature = PixelScene.renderMultiline( 7 );
			add( feature );

			depth = new BitmapText( PixelScene.pixelFont);
			add( depth );

			line = new ColorBlock( 1, 1, 0xFF222222);
			add(line);
			
			icon = Icons.get( Icons.DEPTH );
			add( icon );
		}
		
		@Override
		protected void layout() {

			depth.x = (8 - depth.width())/2f;
			depth.y = y + 1.5f + (height() - 1 - depth.height()) / 2f;
			PixelScene.align(depth);

			icon.x = 8;
			icon.y = y + 1 + (height() - 1 - icon.height()) / 2f;
			PixelScene.align(icon);

			line.size(width, 1);
			line.x = 0;
			line.y = y;

			feature.maxWidth((int)(width - icon.width() - 8 - 1));
			feature.setPos(icon.x + icon.width() + 1, y + 1 + (height() - 1 - feature.height()) / 2f);
			PixelScene.align(feature);
		}
	}
}
