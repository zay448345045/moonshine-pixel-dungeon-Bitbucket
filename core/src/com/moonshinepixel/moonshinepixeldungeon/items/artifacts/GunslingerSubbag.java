package com.moonshinepixel.moonshinepixeldungeon.items.artifacts;

import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.items.EquipableItem;
import com.moonshinepixel.moonshinepixeldungeon.items.bombs.*;
import com.moonshinepixel.moonshinepixeldungeon.items.craftingitems.Scrap;
import com.moonshinepixel.moonshinepixeldungeon.plants.Blindweed;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroSubClass;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.items.Generator;
import com.moonshinepixel.moonshinepixeldungeon.items.potions.PotionOfLiquidFlame;
import com.moonshinepixel.moonshinepixeldungeon.items.weapon.missiles.ammo.bullets.Bullet;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndBombCraft;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndDisassemble;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndModify;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GunslingerSubbag extends Artifact {

    public Item lastItem = null;

    {
        image = ItemSpriteSheet.ARTIFACT_GUNSLINGER_SUBBAG;

        levelCap = 10;
    }
    public static final String AC_CRAFT = "CRAFT";
    public static final String AC_DISASSEMBLE = "DISASSEMBLE";
    public static final String AC_MODIFY = "MODIFY";
    public static final String AC_BULLETMAKE = "BULLETMAKE";


    public GunslingerSubbag() {
        super();
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped( hero ) && level() < levelCap && !cursed) {
            actions.add(AC_DISASSEMBLE);
            actions.add(AC_BULLETMAKE);
            if (hero.subClass == HeroSubClass.BOMBERMAN)
                actions.add(AC_CRAFT);
            if (hero.subClass == HeroSubClass.GANGSTER)
                actions.add(AC_MODIFY);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action ) {

        super.execute(hero, action);

        if (action.equals(AC_CRAFT)){
            GameScene.show( new WndBombCraft(Dungeon.hero ) );
        }
        if (action.equals(AC_DISASSEMBLE)){
            GameScene.show( new WndDisassemble(Dungeon.hero ) );
        }
        if (action.equals(AC_MODIFY)){
            GameScene.show( new WndModify(Dungeon.hero ) );
        }
        if (action.equals(AC_BULLETMAKE)){
            Scrap scrap = hero.belongings.getItem(Scrap.class);
            if (scrap!=null){
                if (scrap.quantity()>=2){
                    scrap.detach(hero.belongings.backpack, 2);
                    Bullet bull = (Bullet) Generator.random(Generator.Category.AMMO);
                    bull.quantity(Random.NormalIntRange(4,6));
                    bull.give();
                    GLog.i(Messages.get(this, "bulletmade", bull.quantity(), bull.name()));
                    hero.busy();
                    hero.sprite.operate(hero.pos);
                    hero.spend(3);
                } else {
                    GLog.n(Messages.get(this, "nescrap"));
                }
            } else {
                GLog.n(Messages.get(this, "nescrap"));
            }
        }
    }

    @Override
    public void activate(Char ch) {
    }
    @Override
    public boolean doUnequip( Hero hero, boolean collect, boolean single ) {

        if (cursed) {
            GLog.w(Messages.get(EquipableItem.class, "unequip_cursed"));
            return false;
        }

        if (single) {
            hero.spendAndNext( time2equip( hero ) );
        } else {
            hero.spend( time2equip( hero ) );
        }

        if (!collect || !collect( hero.belongings.backpack )) {
            onDetach();
            Dungeon.quickslot.clearItem(this);
            updateQuickslot();
            if (collect) Dungeon.level.drop( this, hero.pos );
        }

        if (hero.belongings.misc1 == this) {
            hero.belongings.misc1 = null;
        } else {
            hero.belongings.misc2 = null;
        }

        return true;
    }

    public void craft(Bomb item1, Item item2 ) {
        Bomb result = null;
        Hero hero = Dungeon.hero;
        String error = "craftfailed";
        String defError = "craftfailed";
        String succes = "craftsucces";
        if (item2.getClass()== Bullet.class){
            if (item2.quantity()>=5) {
                for (int i = 0; i < 5; i++) {
                    item2.detach(Dungeon.hero.belongings.backpack);
                }
                result = new ShrapnelBomb();
                succes=Messages.get(this, "successhrapnel", item1.name(), item2.name(), result.name());
            } else {
                error = Messages.get(this, "notenoughbullets", item1.name(), item2.name());
            }
        } else if (item2.getClass()== PotionOfLiquidFlame.class){
            item2.detach(Dungeon.hero.belongings.backpack);
            result = new IncendiaryBomb();
            succes=Messages.get(this, "succesfire", item1.name(), item2.name(), result.name());
        } else if (item2.getClass()== Blindweed.Seed.class){
            item2.detach(Dungeon.hero.belongings.backpack);
            result = new StunBomb();
            succes=Messages.get(this, "successtun", item1.name(), item2.name(), result.name());
        }/* else if (item2.getClass()== Scrap.class){
                item2.detach(Dungeon.hero.belongings.backpack);
                result = new AshBomb();
                succes=Messages.get(this, "succesash", item1.name(), item2.name(), result.name());
        }*/
        if (result!=null){
            item1.detach(Dungeon.hero.belongings.backpack);
            GLog.h(succes);
            if (!result.collect()){
                Dungeon.level.drop( result, hero.pos ).sprite.drop( hero.pos );
            }
            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
            Dungeon.hero.busy();
        } else{
            if (error.equals(defError)){
                Dungeon.hero.sprite.operate(Dungeon.hero.pos);
                Dungeon.hero.busy();
                GLog.i(Messages.get(this, error, item1.name(), item2.name()));
            } else {
                GLog.i(error);
            }
        }
    }

    @Override
    public int price(boolean levelKnown, boolean cursedKnown) {
        return 0;
    }

    @Override
    public int price() {
        return 0;
    }
}
