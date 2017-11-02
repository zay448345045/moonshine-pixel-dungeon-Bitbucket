package com.moonshinepixel.moonshinepixeldungeon.items.grimoires;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.buffs.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.Hero;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.effects.MagicMissile;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.wands.WandOfBlastWave;
import com.moonshinepixel.moonshinepixeldungeon.mechanics.Ballistica;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.scenes.CellSelector;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.sprites.ItemSpriteSheet;
import com.moonshinepixel.moonshinepixeldungeon.ui.BuffIndicator;
import com.moonshinepixel.moonshinepixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class GrimoireOfWind extends ElementalGrimoire {

    {
        image= ItemSpriteSheet.GRIMOIREOFWIND;
    }

    @Override
    public void utilityEffect(Hero caster) {
        if (Dungeon.hero.getSouls()>33*utilityLevel()) {
            GameScene.selectCell(utility);
        } else {
            GLog.i(Messages.get(ElementalGrimoire.class,"nosouls"));
        }
    }

    @Override
    public int utilityLevel() {
        return level()<3?1:level()<7?2:3;
    }

    @Override
    public void attackEffect(Hero caster) {
        if (Dungeon.hero.getSouls()>66*utilityLevel()) {
            GameScene.selectCell(attack);
        } else {
            GLog.i(Messages.get(ElementalGrimoire.class,"nosouls"));
        }
    }

    @Override
    public int attackLevel() {
        return level()<4?1:level()<8?2:3;
    }

    @Override
    public int buffLevel() {
        System.out.println(level()<5?1:level()<9?2:3);
        return level()<5?1:level()<9?2:3;
    }

    @Override
    public void buffEffect(Hero caster) {
        Buff.affect(caster,SlyphBuff.class).set(buffLevel());
    }

    private CellSelector.Listener utility = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell!=null){
                Sample.INSTANCE.play(Assets.SND_ZAP);
                Ballistica leap = new Ballistica(curUser.pos,cell,Ballistica.STOP_CHARS|Ballistica.STOP_TERRAIN|Ballistica.STOP_TARGET);
                WandOfBlastWave.throwChar(curUser,leap,utilityLevel()*2>leap.path.size()?leap.path.size():utilityLevel()*2, false);
                curUser.spend(1);
                cd[0]+=4;
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Item.class, "prompt");
        }
    };
    private CellSelector.Listener attack = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell!=null){
                Sample.INSTANCE.play(Assets.SND_ZAP);
                if (attackLevel()==1) {
                    Ballistica spell = new Ballistica(curUser.pos,cell,Ballistica.STOP_CHARS|Ballistica.STOP_TERRAIN);
                    Char targ = Actor.findChar(spell.path.get(1));
                    if (targ!=null){
                        int oppositeHero = targ.pos + (targ.pos - curUser.pos);
                        Ballistica trajectory = new Ballistica(targ.pos, oppositeHero, Ballistica.MAGIC_BOLT);
                        WandOfBlastWave.throwChar(targ,trajectory,3);
                    }
                    MagicMissile.boltFromChar( curUser.sprite.parent,
                            MagicMissile.FORCE,
                            curUser.sprite,
                            new Ballistica(curUser.pos, spell.path.get(1) + (spell.path.get(1) - curUser.pos), Ballistica.MAGIC_BOLT).collisionPos,
                            null);
                }
                else if (attackLevel()==2) {
                    Ballistica spell = new Ballistica(curUser.pos,cell,Ballistica.STOP_CHARS|Ballistica.STOP_TERRAIN);
                    HashSet<Mob> targs = new HashSet();
                    for (Mob ch: Dungeon.level.mobs){
                        if (Dungeon.level.adjacent(ch.pos,spell.path.get(1))&&Dungeon.level.adjacent(ch.pos,curUser.pos)){
                            targs.add(ch);
                        }
                    }
                    for (Mob targ:targs) {
                        int oppositeHero = targ.pos + (targ.pos - curUser.pos);
                        Ballistica trajectory = new Ballistica(targ.pos, oppositeHero, Ballistica.MAGIC_BOLT);
                        WandOfBlastWave.throwChar(targ, trajectory, 3);
                    }
                    for (int c:PathFinder.NEIGHBOURS8){
                        if (Dungeon.level.adjacent(spell.path.get(1),curUser.pos+c)||curUser.pos+c==spell.path.get(1))
                            MagicMissile.boltFromChar( curUser.sprite.parent,
                                    MagicMissile.FORCE,
                                    curUser.sprite,
                                    new Ballistica(curUser.pos, curUser.pos+c + (curUser.pos+c - curUser.pos), Ballistica.MAGIC_BOLT).collisionPos,
                                    null);
                    }
                }
                else if (attackLevel()==3) {
                    HashSet<Mob> targs = new HashSet();
                    for (Mob ch: Dungeon.level.mobs){
                        if (Dungeon.level.adjacent(ch.pos,curUser.pos)){
                            targs.add(ch);
                        }
                    }
                    for (Mob targ:targs) {
                        int oppositeHero = targ.pos + (targ.pos - curUser.pos);
                        Ballistica trajectory = new Ballistica(targ.pos, oppositeHero, Ballistica.MAGIC_BOLT);
                        WandOfBlastWave.throwChar(targ, trajectory, 3);
                    }
                    for (int c:PathFinder.NEIGHBOURS8){
                        MagicMissile.boltFromChar( curUser.sprite.parent,
                                MagicMissile.FORCE,
                                curUser.sprite,
                                new Ballistica(curUser.pos, curUser.pos+c + (curUser.pos+c - curUser.pos), Ballistica.MAGIC_BOLT).collisionPos,
                                null);
                    }
                }
                curUser.spend(1);
                cd[0]+=2;
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Item.class, "prompt");
        }
    };

    public class SlyphBuff extends GrimoireBuff {
        @Override
        protected int soulUsage() {
            return (int)Math.pow(3.5f,level);
        }

        protected boolean blockBuff(Buff buff){
            if (buff instanceof Paralysis || buff instanceof Poison || buff instanceof Roots || buff instanceof Charm){
                if (level>1){
                    return Random.Int(3)==0;
                }
                if (level>2){
                    return Random.Int(3)>0;
                }
            }
            return false;
        }

        public float evadeRoll(float roll){
            return (float)(roll*Math.pow(1.3,Random.Float(1,level)));
        }

        @Override
        protected int ico() {
            return BuffIndicator.SLYPH;
        }
    }
}
