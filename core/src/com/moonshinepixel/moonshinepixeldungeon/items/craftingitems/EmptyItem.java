package com.moonshinepixel.moonshinepixeldungeon.items.craftingitems;

import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;

public class EmptyItem extends Item {

    {
        image = ItemSpriteSheet.EMPTY;

        stackable = false;
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
