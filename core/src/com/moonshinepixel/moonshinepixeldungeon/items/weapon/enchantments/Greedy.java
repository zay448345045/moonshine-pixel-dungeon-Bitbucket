package com.moonshinepixel.moonshinepixeldungeon.items.weapon.enchantments;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.effects.Flare;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.Ring;
import com.moonshinepixel.moonshinepixeldungeon.items.rings.RingOfWealth;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.Weapon;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;

import java.util.ArrayList;

public class Greedy extends Weapon.Enchantment {

    private static ItemSprite.Glowing GOLDEN = new ItemSprite.Glowing( 0xefef00 );

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GOLDEN;
    }

    @Override
    public void onKill(Weapon weapon, Char attacker, Char defender) {
        super.onKill(weapon, attacker, defender);
        if (defender instanceof Mob){
            Mob m = (Mob)defender;
            if (m.hostile && !m.ally && Dungeon.hero.lvl <= m.maxLvl + 2){
                int rolls = 1;
                if (m.properties().contains(Char.Property.BOSS))             rolls = 15;
                else if (m.properties().contains(Char.Property.MINIBOSS))    rolls = 5;
                ArrayList<Item> bonus = RingOfWealth.tryRareDrop((RingOfWealth) new RingOfWealth().upgrade(Math.round(weapon.level()/2)), rolls);
                if (bonus != null){
                    for (Item b : bonus) Dungeon.level.drop( b , m.pos ).sprite.drop();
                    new Flare(8, 32).color(0xFFFF00, true).show(m.sprite, 2f);
                }
            }
        }
    }
}
