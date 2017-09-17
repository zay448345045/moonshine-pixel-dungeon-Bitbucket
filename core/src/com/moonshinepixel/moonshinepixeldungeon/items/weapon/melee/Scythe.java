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
package com.moonshinepixel.moonshinepixeldungeon.items.weapon.melee;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.EarthImbue;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.FireImbue;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.HighGrass;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;
import java.util.HashSet;

public class Scythe extends MeleeWeapon {

    private String AC_MOW = "MOW";

	{
		image = ItemSpriteSheet.SCYTHE;
        DLY=1.5f;
		tier = 2;
        defaultAction = AC_MOW;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
        HashSet<Char> targets = new HashSet();
        for (int pf : PathFinder.NEIGHBOURS8){
            int cell = attacker.pos+pf;
            Char ch = Actor.findChar(cell);
            if(ch!=null){
                if (Dungeon.level.adjacent(ch.pos,defender.pos)){
                    targets.add(ch);
                }
            }
            if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS){
//                HighGrass.trample(Dungeon.level,cell,null);
            }
        }
        for (Char enemy : targets){
            boolean visibleFight=Dungeon.visible[attacker.pos] || Dungeon.visible[defender.pos];
            if (attacker.hit(attacker,enemy,false)){
                int dmg = attacker.damageRoll();

                if (enchantment != null) {
                    enchantment.proc( this, attacker, defender, dmg );
                }

                int effectiveDamage = Math.max( dmg - defender.drRoll(), 0 );
                defender.defenseProc(attacker,effectiveDamage);
                if (attacker.buff(FireImbue.class) != null)
                    attacker.buff(FireImbue.class).proc(enemy);
                if (attacker.buff(EarthImbue.class) != null)
                    attacker.buff(EarthImbue.class).proc(enemy);

                enemy.sprite.bloodBurstA( attacker.sprite.center(), effectiveDamage );
                enemy.sprite.flash();
                enemy.damage(effectiveDamage,attacker);

                if (!enemy.isAlive() && visibleFight) {
                    if (enemy == Dungeon.hero) {

                        Dungeon.fail( getClass() );
                        GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name)) );

                    } else if (attacker == Dungeon.hero) {
                        GLog.i( Messages.capitalize(Messages.get(Char.class, "defeat", enemy.name)) );
                    }
                }
            }
        }
		return super.proc(attacker, defender, damage);
	}

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (this.isEquipped(hero))
        actions.add ( AC_MOW );
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_MOW)) {
            for (int pf : PathFinder.NEIGHBOURS8){
                int cell = curUser.pos+pf;
                if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS){
                    HighGrass.trample(Dungeon.level,cell,curUser);
                }
            }
        }

    }
}
