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

import com.moonshinepixel.moonshinepixeldungeon.items.traps.TrapPlacer;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroSubClass;
import com.moonshinepixel.moonshinepixeldungeon.scenes.CellSelector;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;

import java.util.HashSet;

public class DismoundIndicator extends Tag {

	private Image icon;

	public DismoundIndicator() {
		super(0xCDD5C0);
//		hotKey = GameAction.RESUME;

		setSize( 24, 24 );

		visible = false;

	}

	@Override
	protected void createChildren() {
		super.createChildren();

		icon = Icons.get( Icons.WRENCH );
		add( icon );
	}

	@Override
	protected void layout() {
		super.layout();

		icon.x = x+1 + (width - icon.width) / 2f;
		icon.y = y + (height - icon.height) / 2f;
		PixelScene.align(icon);
	}

	@Override
	protected void onClick() {

        HashSet<Integer> candidates = new HashSet<>();
        for (int ofs : PathFinder.NEIGHBOURS8){
            if (Dungeon.level.traps.get(Dungeon.hero.pos+ofs)!=null){
                if (Dungeon.level.traps.get(Dungeon.hero.pos+ofs).active)
                    if (!TrapPlacer.tounPickupable.contains(Dungeon.level.traps.get(Dungeon.hero.pos+ofs)))
                        candidates.add(Dungeon.hero.pos+ofs);
            }
        }

        if (!candidates.isEmpty()){
            if (candidates.size()>1)
                GameScene.selectCell( picker );
            else TrapPlacer.pickTrap(candidates.toArray(new Integer[0])[0]);
        }

	}

    protected static final CellSelector.Listener picker = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
                if (Dungeon.level.adjacent(target,Dungeon.hero.pos)){
                    if (Dungeon.level.traps.get(target)!=null){
                        TrapPlacer.pickTrap(target);
                        return;
                    }
                }
                GameScene.selectCell( picker );
            }
        }
        @Override
        public String prompt() {
            return Messages.get(DismoundIndicator.class, "prompt");
        }
    };

	@Override
	public void update() {
		if (!Dungeon.hero.isAlive())
			visible = false;
		else if (Dungeon.hero.subClass == HeroSubClass.FREERUNNER){
		    HashSet<Integer> candidates = new HashSet<>();
		    for (int ofs : PathFinder.NEIGHBOURS8){
                if (Dungeon.level.traps.get(Dungeon.hero.pos+ofs)!=null){
                    if (Dungeon.level.traps.get(Dungeon.hero.pos+ofs).active)
                        if (!TrapPlacer.tounPickupable.contains(Dungeon.level.traps.get(Dungeon.hero.pos+ofs)))
                            candidates.add(Dungeon.hero.pos+ofs);
                }
            }
            if (!visible) {
                visible = !candidates.isEmpty();
                if (visible)
                    flash();
            } else {
                visible = !candidates.isEmpty();
            }
		} else {
		    visible=false;
        }
		super.update();
	}
}
