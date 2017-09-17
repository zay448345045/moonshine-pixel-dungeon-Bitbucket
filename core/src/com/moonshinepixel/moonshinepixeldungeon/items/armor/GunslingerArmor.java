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
package com.moonshinepixel.moonshinepixeldungeon.items.armor;

import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Invisibility;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.effects.particles.ElmoParticle;
import com.moonshinepixel.moonshinepixeldungeon.items.guns.Gun;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.GunslingerBullet;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class GunslingerArmor extends ClassArmor {
	
	{
		image = ItemSpriteSheet.ARMOR_GUNSLINGER;
	}

    @Override
    public void doSpecial() {
	    Hero hero = Dungeon.hero;
        if (!(hero.belongings.weapon instanceof Gun)) {
            GLog.w( Messages.get(this, "invalid_weapon") );
        } else if (((Gun)hero.belongings.weapon)._load.getLoadArrray()[0]==GunslingerBullet.class){
            GLog.w( Messages.get(this, "already_loaded") );
        } else if (((Gun)hero.belongings.weapon)._load.curLoad()==((Gun)hero.belongings.weapon)._load.maxLoad()){
            GLog.w( Messages.get(this, "full_clip") );
        } else {
            Gun gun = (Gun)curUser.belongings.weapon;
            curUser = hero;
            Invisibility.dispel();
            this.doSpecial(gun.ammoType());
        }
    }

    public void doSpecial(String ammotype) {
        Gun gun = (Gun)curUser.belongings.weapon;
        gun._load.addDown(GunslingerBullet.class);
        updateQuickslot();
        curUser.HP -= (curUser.HP / 3);

        //curUser.spend( Actor.TICK );
        curUser.sprite.operate( curUser.pos );
        curUser.busy();

        curUser.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
        Sample.INSTANCE.play( Assets.SND_READ );
    }
}