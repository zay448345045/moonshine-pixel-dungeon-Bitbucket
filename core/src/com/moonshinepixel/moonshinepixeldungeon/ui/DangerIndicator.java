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
package com.moonshinepixel.moonshinepixeldungeon.ui;

import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;

public class DangerIndicator extends Tag {
	
	public static final int COLOR	= 0xFF4C4C;
	
	private BitmapText number;
	private Image icon;
	
	private int enemyIndex = 0;
	
	private int lastNumber = -1;

	public DangerIndicator() {
		super( 0xFF4C4C );

		hotKey = GameAction.TAG_DANGER;

		setSize( 24, 16 );
		
		visible = false;
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();
		
		number = new BitmapText( PixelScene.pixelFont);
		add( number );
		
		icon = Icons.SKULL.get();
		add( icon );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		icon.x = right() - 10;
		icon.y = y + (height - icon.height) / 2;
		
		placeNumber();
	}
	
	private void placeNumber() {
		number.x = right() - 11 - number.width();
		number.y = y + (height - number.baseLine()) / 2f;
		PixelScene.align(number);
	}
	
	@Override
	public void update() {
		
		if (Dungeon.hero.isAlive()) {
			int v =  Dungeon.hero.visibleEnemies();

			if (v != lastNumber) {
				lastNumber = v;
				if (visible = lastNumber > 0) {
					number.text( Integer.toString( lastNumber ) );
					number.measure();
					placeNumber();

					flash();
				}
			}
		} else {
			visible = false;
		}
		
		super.update();
	}
	
	@Override
	protected void onClick() {
		if (Dungeon.hero.visibleEnemies() > 0) {

			Mob target = Dungeon.hero.visibleEnemy(enemyIndex++);

			HealthIndicator.instance.target(target == HealthIndicator.instance.target() ? null : target);

			if (Dungeon.hero.curAction == null) {
				Camera.main.target = null;
				Camera.main.focusOn(target.sprite);
			}
		}
	}
}
