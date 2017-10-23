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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.effects.Fireball;
import com.moonshinepixel.moonshinepixeldungeon.effects.Flare;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.effects.BannerSprites;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.RenderedTextMultiline;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class WelcomeScene extends PixelScene {

	private static int LATEST_UPDATE = 49;

	@Override
	public void create() {
		super.create();

		final int previousVersion = MoonshinePixelDungeon.version();
//		final int previousVersion = -1;

		if (MoonshinePixelDungeon.versionCode == previousVersion) {
			MoonshinePixelDungeon.switchNoFade(TitleScene.class);
			return;
		}

		uiCamera.visible = false;

		int w = Camera.main.width;
		int h = Camera.main.height;

		final Image title = BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON );
		title.brightness(0.6f);
		add( title );

		float topRegion = Math.max(95f, h*0.45f);

		title.x = (w - title.width()) / 2f;
		if (MoonshinePixelDungeon.landscape())
			title.y = (topRegion - title.height()) / 2f;
		else
			title.y = 16 + (topRegion - title.height() - 16) / 2f;

		align(title);

		Image signs = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_SIGNS ) ) {
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = Math.max(0f, (float)Math.sin( time += Game.elapsed ));
				if (time >= 1.5f*Math.PI) time = 0;
			}
			@Override
			public void draw() {
				Gdx.gl.glBlendFunc( GL20.GL_SRC_ALPHA, GL20.GL_ONE );
				super.draw();
				Gdx.gl.glBlendFunc( GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}
		};
		signs.x = title.x + (title.width() - signs.width())/2f;
		signs.y = title.y;
		add( signs );
		Image clouds = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_CLOUDS_1 ) ) {
			private float time = (float)(Random.Float()*Math.PI);
			@Override
			public void update() {
				super.update();
				y=title.y + title.height()*(17f/90f)*1.2f+(float)Math.sin( time += Game.elapsed )*3;
			}
		};
		clouds.x = title.x + title.width()*(3f/132f);
		clouds.y = title.y + title.height()*1.2f;
		add( clouds );


		final Flare fl = new Flare( 7, 48 ).color( 0xe2e2e2, true );
		fl.angularSpeed = +20;
		fl.alpha(0.5f);
		add(fl);

		Image clouds2 = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_CLOUDS_2 ) ) {
			private float time = (float)(Random.Float()*Math.PI);
			@Override
			public void update() {
				super.update();
				fl.x=this.x+17*(this.width()/58f);
				fl.y=this.y+7*(this.width()/58f);
				y=title.y + title.height()*(17f/90f)*1.2f+(float)Math.sin( time += Game.elapsed )*3;
			}
		};
		clouds2.x = title.x + title.width()*(79f/132f);
		clouds2.y = title.y + title.height()*(17f/90f)*1.2f;
		final float ry = clouds2.y;
		add( clouds2 );

		DarkRedButton okay = new DarkRedButton(Messages.get(this, "continue")){
			@Override
			protected void onClick() {
				super.onClick();
				updateVersion(previousVersion);
				MoonshinePixelDungeon.switchScene(TitleScene.class);
			}
		};

		if (previousVersion != 0){
			DarkRedButton changes = new DarkRedButton(Messages.get(this, "changelist")){
				@Override
				protected void onClick() {
					super.onClick();
					updateVersion(previousVersion);
					MoonshinePixelDungeon.switchScene(ChangesScene.class);
				}
			};
			okay.setRect(title.x, h-20, (title.width()/2)-2, 16);
			okay.textColor(0xBBBB33);
			add(okay);

			changes.setRect(okay.right()+2, h-20, (title.width()/2)-2, 16);
			changes.textColor(0xBBBB33);
			add(changes);
		} else {
			okay.setRect(title.x, h-20, title.width(), 16);
			okay.textColor(0xBBBB33);
			add(okay);
		}

		RenderedTextMultiline text = renderMultiline(6);
		String message;
		if (previousVersion == 0) {
			message = Messages.get(this, "welcome_msg");
		} else if (previousVersion <= MoonshinePixelDungeon.versionCode) {
			if (previousVersion < LATEST_UPDATE){
				message = Messages.get(this, "update_intro");
				message += "\n\n" + Messages.get(this, "update_msg");
			} else {
				//TODO: change the messages here in accordance with the type of patch.
				message = Messages.get(this, "patch_intro");
				message +="\n";
				 message += "\n" + Messages.get(this, "patch_bugfixes");
				 message += "\n" + Messages.get(this, "patch_content");
//				message += "\n" + Messages.get(this, "patch_translations");
//				message += "\n" + Messages.get(this, "patch_balance");
//				 message += "\n" + Messages.get(this, "patch_balance_big");
//				message += "\n" + Messages.get(this, "no_save");
//				message += "\n" + "Old androids saves are no longer openable, also wiped old android ranking";

			}
		} else {
			message = Messages.get(this, "what_msg");
		}
		text.text(message, w-20);
		float textSpace = h - title.y - (title.height() - 10) - okay.height() - 2;
		text.setPos((w - text.width()) / 2f, title.y+(title.height() - 10) + ((textSpace - text.height()) / 2));
		add(text);

	}

	private void updateVersion(int previousVersion){
		MoonshinePixelDungeon.version(MoonshinePixelDungeon.versionCode);
	}

	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.setPos( x, y );
		add( fb );
	}

	private class DarkRedButton extends RedButton{
		{
			bg.brightness(0.4f);
		}

		DarkRedButton(String text){
			super(text);
		}

		@Override
		protected void onTouchDown() {
			bg.brightness(0.5f);
			Sample.INSTANCE.play( Assets.SND_CLICK );
		}

		@Override
		protected void onTouchUp() {
			super.onTouchUp();
			bg.brightness(0.4f);
		}
	}
}
