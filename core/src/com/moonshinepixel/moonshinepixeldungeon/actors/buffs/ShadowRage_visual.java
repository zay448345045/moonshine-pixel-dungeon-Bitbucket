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
package com.moonshinepixel.moonshinepixeldungeon.actors.buffs;

import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.GooWarn;

public class ShadowRage_visual extends Buff {
	
	public float LEVEL	= 1f;

	{
		type = buffType.SILENT;
	}
	
	@Override
	public boolean act() {

        GameScene.add(Blob.seed(target.pos, 2, GooWarn.class));

        spend( TICK );
		
		return true;
	}

	@Override
	public void fx(boolean on) {
//		if (on) target.sprite.add( CharSprite.State.DARKENED );
//		else if (target.invisible == 0) target.sprite.remove( CharSprite.State.DARKENED );

//        if (on) target.sprite.add(CharSprite.State.BURNING);
//        else target.sprite.remove(CharSprite.State.BURNING);
	}

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}
}
