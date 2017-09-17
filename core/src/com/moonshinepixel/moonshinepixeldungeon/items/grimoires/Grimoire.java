package com.moonshinepixel.moonshinepixeldungeon.items.grimoires;

import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;

public abstract class Grimoire extends Item {
    {
        stackable = false;
        unique=true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public abstract void validateMobKill(Mob mob);
}
