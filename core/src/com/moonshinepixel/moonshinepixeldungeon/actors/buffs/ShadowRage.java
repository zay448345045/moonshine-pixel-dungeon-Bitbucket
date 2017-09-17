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

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.GooWarn;
import com.moonshinepixel.moonshinepixeldungeon.items.SoulVial;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;

public class ShadowRage extends Buff {
	
	public float LEVEL	= 1f;

	{
		type = buffType.POSITIVE;
	}
	
	@Override
	public boolean act() {
	    if (target instanceof Hero) {
            Hero hero = (Hero)target;
            SoulVial sv = hero.belongings.getItem(SoulVial.class);
            if (sv!=null) {
                if (sv.getVolume() >= 3) {
                    sv.setVolume(sv.getVolume() - 1);
                    LEVEL = 6f * (sv.getVolume() / 666f);
                    if (LEVEL<1) detach();
                } else {
                    sv.setVolume(0);
                    detach();
                }
            } else {
                detach();
            }
        } else {
	        detach();
        }
		
		spend( TICK );
		
		return true;
	}

	public void spendCharge(int charge){
        if (target instanceof Hero) {
            Hero hero = (Hero)target;
            SoulVial sv = hero.belongings.getItem(SoulVial.class);
            if (sv!=null) {
                if (sv.getVolume() >= charge) {
                    sv.setVolume(sv.getVolume() - charge);
                    LEVEL = 6f * (sv.getVolume() / 666f);
                    if (LEVEL<1) detach();
                    GameScene.add(Blob.seed(target.pos, 2, GooWarn.class));
                } else {
                    sv.setVolume(0);
                    detach();
                }
            } else {
                detach();
            }
        }
    }

    @Override
    public boolean attachTo(Char target) {
        if (target instanceof Hero){
            Hero hero = (Hero)target;
            SoulVial sv = hero.belongings.getItem(SoulVial.class);
            if (sv!=null) {
                if (sv.getVolume()>0){
                    LEVEL = 6f * (sv.getVolume() / 666f);
                    if (LEVEL<1) return false;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return super.attachTo(target);
    }

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add( CharSprite.State.DARKENED );
		else if (target.invisible == 0) target.sprite.remove( CharSprite.State.DARKENED );
	}

    @Override
    public int icon() {
        return BuffIndicator.CORRUPT;
    }

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
}
