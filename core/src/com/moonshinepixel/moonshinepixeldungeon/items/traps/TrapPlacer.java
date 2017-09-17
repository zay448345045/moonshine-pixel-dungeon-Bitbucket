package com.moonshinepixel.moonshinepixeldungeon.items.traps;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.*;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.messages.traps.TrapObject;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.sprites.MissileSprite;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.scenes.CellSelector;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSprite;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.levels.features.Door;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class TrapPlacer extends Item {

    private static final String AC_PLACE = "place";

    public Class<? extends Trap> trap = WornTrap.class;

    {
        image = ItemSpriteSheet.TRAP;

        defaultAction = AC_PLACE;

        stackable=true;
    }
    public static HashSet<Class<? extends Trap>> tounPickupable = new HashSet(Arrays.asList(AlarmTrap.class, CursingTrap.class, DisarmingTrap.class, DistortionTrap.class,GrimTrap.class,GuardianTrap.class,PitfallTrap.class,SummoningTrap.class,WarpingTrap.class));
    public static Trap convertTrap(Trap trap){
        return trap;
    }

    public static void pickTrap(int cell){
        if (Dungeon.level.traps.get(cell)!=null) {
            if (Dungeon.level.traps.get(cell).active) {
                if (Actor.findChar(cell) == null) {
                    float chance = Random.Int(100);
                    if (tounPickupable.contains(Dungeon.level.traps.get(cell).getClass())) chance = 0;
                    if (chance > 49) {
                        Trap trap = Dungeon.level.traps.get(cell);
                        Dungeon.level.set(cell, Terrain.EMPTY);
                        Heap itm = Dungeon.level.drop(new TrapPlacer(trap), cell);
                        itm.sprite.drop();
                        Dungeon.level.traps.remove(cell);
                        GameScene.updateMap(cell);
                    }
                    Dungeon.hero.busy();
                    int pos = Dungeon.hero.pos;
                    Dungeon.hero.sprite.move(pos, cell);
                    Dungeon.hero.pos=cell;
                    if (Dungeon.level.map[pos] == Terrain.OPEN_DOOR) {
                        Door.leave( pos );
                    }
                    Dungeon.level.press(Dungeon.hero.pos,Dungeon.hero);
                    Dungeon.hero.spendAndNext(5);
                }
            }
        }
    }

    public TrapPlacer(Trap trap){
        super();
        this.trap=trap.getClass();
        GLOW=new ItemSprite.Glowing(trap.getRGBColor());
    }
    public TrapPlacer(Class<? extends Trap> trap){
        super();
        this.trap=trap;
        try {
            GLOW = new ItemSprite.Glowing(trap.newInstance().getRGBColor());
        } catch (Exception e){
            MoonshinePixelDungeon.reportException(e);
            GLOW=new ItemSprite.Glowing(0x000000);
        }
    }
    public TrapPlacer(){
        super();
        this.trap=WornTrap.class;
        GLOW=new ItemSprite.Glowing(0x000000);
    }
    private ItemSprite.Glowing GLOW = new ItemSprite.Glowing(0x00000);
    @Override
    public ItemSprite.Glowing glowing() {
        return GLOW;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_PLACE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_PLACE)){
            GameScene.selectCell( trapPlacer );
        }
    }

    protected static final CellSelector.Listener trapPlacer = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null) {
//                if (((TrapPlacer)curItem).tiles.contains(Dungeon.level.map[target]))
                ((TrapPlacer)curItem).setTrap( target );
//                else GLog.w(Messages.get(TrapPlacer.class, "invalidcell"));
            }
        }
        @Override
        public String prompt() {
            return Messages.get(Item.class, "prompt");
        }
    };
    HashSet<Integer> tiles = new HashSet<>(Arrays.asList(Terrain.EMPTY,Terrain.EMPTY_SP,Terrain.WATER,Terrain.INACTIVE_TRAP,Terrain.TRAP,Terrain.EMPTY_DECO,Terrain.GRASS,Terrain.HIGH_GRASS,Terrain.SECRET_TRAP,Terrain.EMBERS,Terrain.SIGN));
    public void setTrap(int targ){
        final Ballistica ball = new Ballistica(curUser.pos,targ,Ballistica.PROJECTILE);
        final int cell = ball.collisionPos;
        final Item proto = this;
        ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                reset(curUser.pos, cell, proto, new Callback() {
                    @Override
                    public void call() {
                        if (tiles.contains(Dungeon.level.map[cell])) {
                            Dungeon.level.press(cell, null);
                            Dungeon.level.setTrapObj(new TrapObject(trap), cell);
                            if (Actor.findChar(cell)!=null) Dungeon.level.press(cell,Actor.findChar(cell));
                        } else {
                            Heap itm = Dungeon.level.drop(proto,cell);
                            itm.sprite.drop();
                            GLog.w(Messages.get(TrapPlacer.class, "invalidcell"));
                        }
                        curUser.spendAndNext(1);

                    }
                });
        detach(Dungeon.hero.belongings.backpack);
        curUser.sprite.zap(cell);
        curUser.busy();
    }

    @Override
    public String desc() {
        try {
            return Messages.get(this, "desc", trap.newInstance().name);
        } catch (Exception e){
            MoonshinePixelDungeon.reportException(e);
            return Messages.get(this, "desc", new WornTrap().name);
        }
    }

    @Override
    public String name() {
        try {
            return Messages.get(this, "name", trap.newInstance().name);
        } catch (Exception e){
            MoonshinePixelDungeon.reportException(e);
            return Messages.get(this, "name", new WornTrap().name);
        }
    }

    private final String TRAP = "trap";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TRAP, trap);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        trap=bundle.getClass(TRAP);
    }


    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public Item random() {
        Class<? extends Trap>[] traps = new Class[]{
                BlazingTrap.class,
                ChillingTrap.class,
                ConfusionTrap.class,
                DisintegrationTrap.class,
                ExplosiveTrap.class,
                FireTrap.class,
                FlashingTrap.class,
                FlockTrap.class,
                GrippingTrap.class,
                LightningTrap.class,
                OozeTrap.class,
                ParalyticTrap.class,
                PoisonTrap.class,
                RockfallTrap.class,
                SpearTrap.class,
                TeleportationTrap.class,
                ToxicTrap.class,
                VenomTrap.class,
                WeakeningTrap.class,
                WornTrap.class
        };
        float[] probs = new float[]{
                1,
                1,
                2,
                1,
                1,
                3,
                2,
                3,
                3,
                1,
                1,
                2,
                3,
                2,
                1,
                1,
                2,
                1,
                2,
                0
        };
        trap=traps[Random.chances(probs)];
        quantity=Random.chances(new float[]{0,3,2,1});
        return super.random();
    }

    @Override
    public boolean isSimilar(Item item) {
        boolean ret = super.isSimilar(item);
        if (ret) {
            if (((TrapPlacer) item).trap == trap) ret = true; else ret=false;
        }
        return ret;
    }

    @Override
    public int price() {
        return 25*quantity;
    }
}
