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

import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.Invisibility;
import com.moonshinepixel.moonshinepixeldungeon.items.BrokenSeal;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

abstract public class ClassArmor extends Armor {

	protected static final String AC_SPECIAL = "SPECIAL";
	
	{
		levelKnown = true;
		cursedKnown = true;
		defaultAction = AC_SPECIAL;

		bones = false;
	}

	private int armorTier;
	
	public ClassArmor() {
		super( 6 );
	}
	
	public static ClassArmor upgrade (Hero owner, Armor armor ) {
		
		ClassArmor classArmor = null;
		
		switch (owner.heroClass) {
		case WARRIOR:
			classArmor = new WarriorArmor();
			BrokenSeal seal = armor.checkSeal();
			if (seal != null) {
				classArmor.affixSeal(seal);
			}
			break;
		case ROGUE:
			classArmor = new RogueArmor();
			break;
		case MAGE:
			classArmor = new MageArmor();
			break;
		case HUNTRESS:
			classArmor = new HuntressArmor();
			break;
			case GUNSLINGER:
			classArmor = new GunslingerArmor();
			break;
		}

		classArmor.level(armor.level());
		classArmor.armorTier = armor.tier;
		classArmor.inscribe( armor.glyph );
		
		return classArmor;
	}

	private static final String ARMOR_TIER	= "armortier";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARMOR_TIER, armorTier );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		
		armorTier = bundle.getInt( ARMOR_TIER );
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.remove( AC_DETACH );
		if (hero.HP >= 3 && isEquipped( hero )) {
			actions.add( AC_SPECIAL );
		}
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_SPECIAL)) {
			
			if (hero.HP < 3) {
				GLog.w( Messages.get(this, "low_hp") );
			} else if (!isEquipped( hero )) {
				GLog.w( Messages.get(this, "not_equipped") );
			} else {
				curUser = hero;
				Invisibility.dispel();
				doSpecial();
			}
			
		}
	}

	abstract public void doSpecial();

	@Override
	public int STRReq(int lvl) {
		lvl = Math.max(0, lvl);
		float effectiveTier = armorTier;
		if (glyph != null) effectiveTier += glyph.tierSTRAdjust();
		effectiveTier = Math.max(0, effectiveTier);

		//strength req decreases at +1,+3,+6,+10,etc.
		return (8 + Math.round(effectiveTier * 2)) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	@Override
	public int DRMax(int lvl){
		int effectiveTier = armorTier;
		if (glyph != null) effectiveTier += glyph.tierDRAdjust();
		effectiveTier = Math.max(0, effectiveTier);

		return effectiveTier * (2 + lvl);
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int price(boolean levelKnown, boolean cursedKnown) {
		return 0;
	}

	@Override
	public Item damage(float durability) {
		return this;
		//Class armor have infinity durability
	}
}
