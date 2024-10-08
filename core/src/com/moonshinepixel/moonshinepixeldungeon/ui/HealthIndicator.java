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

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;

public class HealthIndicator extends HealthBar {
	
	public static HealthIndicator instance;
	
	private Char target;
	
	public HealthIndicator() {
		super();
		
		instance = this;
	}
	
	@Override
	public void update() {
		super.update();
		
		if (target != null && target.renderHPBar && target.isAlive() && target.sprite!=null && target.sprite.visible && target.sprite.showHPBar) {
			CharSprite sprite = target.sprite;
			width = sprite.width;
			x = sprite.x;
			y = sprite.y - 3;
			level( target );
			visible = true;
		} else {
			visible = false;
		}
	}
	
	public void target( Char ch ) {
		if (ch != null && ch.isAlive()) {
			target = ch;
		} else {
			target = null;
		}
	}
	
	public Char target() {
		return target;
	}
}
