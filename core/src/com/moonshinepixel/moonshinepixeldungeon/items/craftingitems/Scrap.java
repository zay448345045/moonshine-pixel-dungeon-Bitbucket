package com.moonshinepixel.moonshinepixeldungeon.items.craftingitems;

import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;

public class Scrap extends Item {

    {
        image = ItemSpriteSheet.SCRAP;

        stackable = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}
