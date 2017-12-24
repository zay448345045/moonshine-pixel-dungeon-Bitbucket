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
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments.Projecting;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.HighGrass;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Scythe extends MeleeWeapon {

    private boolean secondUse = false;
    private float   dmgMod = 1f;

    private String AC_MOW = "MOW";

	{
		image = ItemSpriteSheet.SCYTHE;
        DLY=1.5f;
		tier = Random.NormalIntRange(2,5);
        defaultAction = AC_MOW;

//        isDouble=true;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
	    if (!secondUse) {
            secondUse=true;
            HashSet<Char> targets = new HashSet();
            HashSet<Char> posTargs = new HashSet();
            PathFinder.buildDistanceMap(attacker.pos, BArray.or(Level.getPassable(), Level.getAvoid(), null));
            for (Mob ch : Dungeon.level.mobs) {
                if (!(ch == attacker) && !(ch == defender) && Dungeon.level.adjacent(attacker.pos,ch.pos) && Dungeon.level.adjacent4(defender.pos,ch.pos) && !(attacker == Dungeon.hero && ch.ally)) {
                    posTargs.add(ch);
                }
            }
            if ((enchantment instanceof Projecting)) {
                PathFinder.buildDistanceMap(defender.pos, BArray.or(Level.getPassable(), Level.getAvoid(), null));
                for (Char ch : posTargs) {
                    if (PathFinder.distance[ch.pos] <= reachFactor(attacker instanceof Hero ? (Hero) attacker : null)) {
                        targets.add(ch);
                    }
                }
            } else {
                targets = posTargs;
            }
            dmgMod = 1 - targets.size() * (75 / 8 / 100);
            for (Char enemy : targets) {
                attacker.attack(enemy);
            }
            secondUse=false;
        }
        damage *= dmgMod;
		return super.proc(attacker, defender, damage);
	}

    @Override
    public int reachFactor(Hero hero) {
        return RCH;
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
