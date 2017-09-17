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
package com.moonshinepixel.moonshinepixeldungeon.levels.traps;

import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Trap implements Bundlable {

	//trap colors
	public static final int RED     = 0;
	public static final int ORANGE  = 1;
	public static final int YELLOW  = 2;
	public static final int GREEN   = 3;
	public static final int TEAL    = 4;
	public static final int VIOLET  = 5;
	public static final int WHITE   = 6;
	public static final int GREY    = 7;
	public static final int BLACK   = 8;

	//trap shapes
	public static final int DOTS        = 0;
	public static final int WAVES       = 1;
	public static final int GRILL       = 2;
	public static final int STARS       = 3;
	public static final int DIAMOND     = 4;
	public static final int CROSSHAIR   = 5;
	public static final int LARGE_DOT   = 6;

	public String name = Messages.get(this, "name");

	public int color;
	public int shape;

	public int pos;

	public boolean visible;
	public boolean active = true;
	public boolean friendly = false;

	public int getRGBColor(){
		switch (color){
			case RED: return 0xFF0000;
			case ORANGE: return 0xFF9922;
			case YELLOW: return 0xFFFF00;
			case GREEN: return 0x00FF00;
			case TEAL: return 0x10F2F2;
			case VIOLET: return 0xFF00FF;
			case WHITE: return 0xFFFFFF;
			case GREY: return 0x999999;
			case BLACK: return 0x000000;
			default: return 0x000000;
		}
	}

	public Trap set(int pos){
		this.pos = pos;
		return this;
	}

	public Trap reveal() {
		visible = true;
		GameScene.updateMap(pos);
		return this;
	}

	public Trap hide() {
		visible = false;
		GameScene.updateMap(pos);
		return this;
	}

	public void trigger() {
		if (active) {
			if (Dungeon.visible[pos]) {
				Sample.INSTANCE.play(Assets.SND_TRAP);
			}
			disarm();
			reveal();
			activate();
		}
	}

	public abstract void activate();

	protected void disarm(){
		Dungeon.level.disarmTrap(pos);
		active = false;
	}

	private static final String POS	= "pos";
	private static final String VISIBLE	= "visible";
	private static final String ACTIVE = "active";
	private static final String FRIENDLY = "friendly";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
		visible = bundle.getBoolean( VISIBLE );
		if (bundle.contains(ACTIVE)){
			active = bundle.getBoolean(ACTIVE);
		}
		if (bundle.contains(FRIENDLY)){
			friendly = bundle.getBoolean(FRIENDLY);
		}
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
		bundle.put( VISIBLE, visible );
		bundle.put( ACTIVE, active );
		bundle.put( FRIENDLY, friendly );
	}

	public String desc() {
	    String desc = Messages.get(this, "desc");
	    if (friendly)
	        desc+="\n" + Messages.get(Trap.class, "friendly");
		return desc;
	}
}
