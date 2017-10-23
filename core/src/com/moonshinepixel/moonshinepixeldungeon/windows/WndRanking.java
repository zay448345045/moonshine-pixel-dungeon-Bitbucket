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

import com.moonshinepixel.moonshinepixeldungeon.Rankings;
import com.moonshinepixel.moonshinepixeldungeon.ui.BadgesList;
import com.moonshinepixel.moonshinepixeldungeon.ui.ScrollPane;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.Statistics;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Belongings;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.Icons;
import com.moonshinepixel.moonshinepixeldungeon.ui.ItemSlot;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Badges;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.HeroSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

import java.util.Locale;

public class WndRanking extends WndTabbed {
	
	private static final int WIDTH			= 115;
	private static final int HEIGHT			= 144;
	
	private Thread thread;
	private int score = 0;
	private int challenges = 0;
	private String error = null;
	
	private Image busy;

	public WndRanking( final Rankings.Record rec ) {
		
		super();

		score=rec.score;

		resize( WIDTH, HEIGHT );
		
		thread = new Thread() {
			@Override
			public void run() {
				try {
					Badges.loadGlobal();
					Rankings.INSTANCE.loadGameData( rec );
					Dungeon.challenges = rec.challenges;

				} catch ( Exception e ) {
					error = Messages.get(WndRanking.class, "error");
				}
			}
		};
		thread.start();

		busy = Icons.BUSY.get();
		busy.origin.set( busy.width / 2, busy.height / 2 );
		busy.angularSpeed = 720;
		busy.x = (WIDTH - busy.width) / 2;
		busy.y = (HEIGHT - busy.height) / 2;
		add( busy );
	}
	
	@Override
	public void update() {
		super.update();
		
		if (thread != null && !thread.isAlive()) {
			thread = null;
			if (error == null) {
				remove( busy );
				createControls();
			} else {
				hide();
				Game.scene().add( new WndError( error ) );
			}
		}
	}
	
	private void createControls() {
		
		String[] labels =
			{Messages.get(this, "stats"), Messages.get(this, "items"), Messages.get(this, "badges")};
		Group[] pages =
			{new StatsTab(), new ItemsTab(), new BadgesTab()};
		
		for (int i=0; i < pages.length; i++) {
			
			add( pages[i] );
			
			Tab tab = new RankingTab( labels[i], pages[i] );
			add( tab );
		}

		layoutTabs();
		
		select( 0 );
	}

	private class RankingTab extends LabeledTab {
		
		private Group page;
		
		public RankingTab( String label, Group page ) {
			super( label );
			this.page = page;
		}
		
		@Override
		protected void select( boolean value ) {
			super.select( value );
			if (page != null) {
				page.visible = page.active = selected;
			}
		}
	}
	
	private class StatsTab extends Group {
		
		private int GAP	= 3;
		
		public StatsTab() {
			super();
			
//			if (Dungeon.challenges > 0) GAP--;
			
			String heroClass = Dungeon.hero.className();
			
			IconTitle title = new IconTitle();
			title.icon( HeroSprite.avatar( Dungeon.hero.heroClass, Dungeon.hero.tier() ) );
			if(Dungeon.hero.givenName().equals(Dungeon.hero.className())) {
				title.label(Messages.get(this, "title", Dungeon.hero.lvl, heroClass).toUpperCase(Locale.ENGLISH));
			} else{
//				title.label(Messages.get(this, "titlenamed_1", Dungeon.hero.lvl).toUpperCase(Locale.ENGLISH)+Dungeon.hero.givenName()+"\n"+Messages.get(this, "titlenamed_2", Dungeon.hero.className()).toUpperCase(Locale.ENGLISH));
				title.label(Dungeon.hero.givenName()+"\n"+Messages.get(this, "title", Dungeon.hero.lvl, heroClass).toUpperCase(Locale.ENGLISH));
			}
			title.color(Window.SHPX_COLOR);
			title.setRect( 0, 0, WIDTH, 0 );
			add( title );
			
			float pos = title.bottom();
//			System.out.println(Dungeon.challenges);
			if (Dungeon.challenges > 0) {
				RedButton btnCatalogus = new RedButton( Messages.get(this, "challenges") ) {
					@Override
					protected void onClick() {
						Game.scene().add( new WndChallenges( Dungeon.challenges, false ) );
					}
				};
//				btnCatalogus.setRect( 0, pos, btnCatalogus.reqWidth() + 2, btnCatalogus.reqHeight() + 2 );
				btnCatalogus.setRect( 0, pos, WIDTH-2, btnCatalogus.reqHeight() + 2 );
				btnCatalogus.setPos(WIDTH/2-btnCatalogus.width()/2,pos);
				add( btnCatalogus );

				pos = btnCatalogus.bottom();
			}

			pos += GAP + GAP;
			
			pos = statSlot( this, Messages.get(this, "str"), Integer.toString( Dungeon.hero.STR ), pos );
			pos = statSlot( this, Messages.get(this, "health"), Integer.toString( Dungeon.hero.HT ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, Messages.get(this, "duration"), Integer.toString( (int) Statistics.duration ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, Messages.get(this, "depth"), Integer.toString( Statistics.deepestFloor ), pos );
			pos = statSlot( this, Messages.get(this, "enemies"), Integer.toString( Statistics.enemiesSlain ), pos );
			pos = statSlot( this, Messages.get(this, "gold"), Integer.toString( Statistics.goldCollected ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, Messages.get(this, "food"), Integer.toString( Statistics.foodEaten ), pos );
			pos = statSlot( this, Messages.get(this, "alchemy"), Integer.toString( Statistics.potionsCooked ), pos );
			pos = statSlot( this, Messages.get(this, "ankhs"), Integer.toString( Statistics.ankhsUsed ), pos );

			pos += GAP;

			pos = statSlot( this, Messages.get(this, "score"), Integer.toString( score ), pos );
		}
		
		private float statSlot( Group parent, String label, String value, float pos ) {
			
			RenderedText txt = PixelScene.renderText( label, 7 );
			txt.y = pos;
			parent.add( txt );
			
			txt = PixelScene.renderText( value, 7 );
			txt.x = WIDTH * 0.65f;
			txt.y = pos;
			PixelScene.align(txt);
			parent.add( txt );
			
			return pos + GAP + txt.baseLine();
		}
	}
	
	private class ItemsTab extends Group {
		
		private float pos;
		
		public ItemsTab() {
			super();
			
			Belongings stuff = Dungeon.hero.belongings;
			if (stuff.weapon != null) {
				addItem( stuff.weapon );
			}
			if (stuff.armor != null) {
				addItem( stuff.armor );
			}
			if (stuff.misc1 != null) {
				addItem( stuff.misc1);
			}
			if (stuff.misc2 != null) {
				addItem( stuff.misc2);
			}

			pos = 0;
			for (int i = 0; i < 4; i++){
				if (Dungeon.quickslot.getItem(i) != null){
					QuickSlotButton slot = new QuickSlotButton(Dungeon.quickslot.getItem(i));

					slot.setRect( pos, 116, 28, 28 );

					add(slot);

				} else {
					ColorBlock bg = new ColorBlock( 28, 28, 0x9953564D );
					bg.x = pos;
					bg.y = 116;
					add(bg);
				}
				pos += 29;
			}
		}
		
		private void addItem( Item item ) {
			ItemButton slot = new ItemButton( item );
			slot.setRect( 0, pos, width, ItemButton.HEIGHT );
			add( slot );
			
			pos += slot.height() + 1;
		}
	}
	
	private class BadgesTab extends Group {
		
		public BadgesTab() {
			super();
			
			camera = WndRanking.this.camera;
			
			ScrollPane list = new BadgesList( false );
			add( list );
			
			list.setSize( WIDTH, HEIGHT );
		}
	}
	
	private class ItemButton extends Button<GameAction> {
		
		public static final int HEIGHT	= 28;
		
		private Item item;
		
		private ItemSlot slot;
		private ColorBlock bg;
		private RenderedText name;
		
		public ItemButton( Item item ) {
			
			super();

			this.item = item;
			
			slot.item( item );
			if (item.cursed && item.cursedKnown) {
				bg.ra = +0.2f;
				bg.ga = -0.1f;
			} else if (!item.isIdentified()) {
				bg.ra = 0.1f;
				bg.ba = 0.1f;
			}
		}
		
		@Override
		protected void createChildren() {
			
			bg = new ColorBlock( HEIGHT, HEIGHT, 0x9953564D );
			add( bg );
			
			slot = new ItemSlot();
			add( slot );
			
			name = PixelScene.renderText( "?", 7 );
			add( name );
			
			super.createChildren();
		}
		
		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;
			
			slot.setRect( x, y, HEIGHT, HEIGHT );
			PixelScene.align(slot);
			
			name.x = slot.right() + 2;
			name.y = y + (height - name.baseLine()) / 2;
			PixelScene.align(name);
			
			String str = Messages.titleCase( item.name() );
			name.text( str );
			if (name.width() > width - name.x) {
				do {
					str = str.substring( 0, str.length() - 1 );
					name.text( str + "..." );
				} while (name.width() > width - name.x);
			}
			
			super.layout();
		}
		
		@Override
		protected void onTouchDown() {
			bg.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
		};
		
		protected void onTouchUp() {
			bg.brightness( 1.0f );
		};
		
		@Override
		protected void onClick() {
			Game.scene().add( new WndItem( null, item ) );
		}
	}

	private class QuickSlotButton extends ItemSlot{

		public static final int HEIGHT	= 28;

		private Item item;
		private ColorBlock bg;

		QuickSlotButton(Item item){
			super(item);
			this.item = item;
		}

		@Override
		protected void createChildren() {
			bg = new ColorBlock( HEIGHT, HEIGHT, 0x9953564D );
			add( bg );

			super.createChildren();
		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			super.layout();
		}

		@Override
		protected void onTouchDown() {
			bg.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
		};

		protected void onTouchUp() {
			bg.brightness( 1.0f );
		};

		@Override
		protected void onClick() {
			Game.scene().add(new WndItem(null, item));
		}
	}
}
