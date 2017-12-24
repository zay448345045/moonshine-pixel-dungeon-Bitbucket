package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.bossRooms;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.levels.GardenBossLevel;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.Trap;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.HashSet;

public class GardenBossRoom extends Room {
    public static final int UP      =1;
    public static final int DOWN    =2;
    public static final int LEFT    =4;
    public static final int RIGHT   =8;
    public static final int ALL     =15;

    public static final int SPACE   = GardenBossLevel.ROOMSIZE+1;

    public int dirs = 0;
    public int fromDir = 0;
    public int x = 0;
    public int y = 0;
    public boolean drawed = false;

    @Override
    public void paint(Level level) {
        super.paint(level);
        Painter.fill(level,this, Terrain.WALL);
        Painter.set(level,center(),Terrain.EMPTY);
        if ((dirs&UP)!=0)Painter.drawLine(level,center(),new Point(center().x,center().y-SPACE),Terrain.EMPTY);
        if ((dirs&DOWN)!=0)Painter.drawLine(level,center(),new Point(center().x,center().y+SPACE),Terrain.EMPTY);
        if ((dirs&LEFT)!=0)Painter.drawLine(level,center(),new Point(center().x-SPACE,center().y),Terrain.EMPTY);
        if ((dirs&RIGHT)!=0)Painter.drawLine(level,center(),new Point(center().x+SPACE,center().y),Terrain.EMPTY);
        GardenBossLevel lvl = (GardenBossLevel)level;
        for(Point p : getPoints()) {
            int cell = level.pointToCell(p);
            if (lvl.watr[cell]&&level.map[cell]==Terrain.EMPTY){
                level.map[cell]=Terrain.WATER;
            }
            if (lvl.gras[cell]&&level.map[cell]==Terrain.EMPTY){
                level.map[cell]=Terrain.HIGH_GRASS;
            }
            if (level.map[cell]==Terrain.EMPTY&& Random.Int(10)==0){
                level.map[cell]=Terrain.EMPTY_DECO;
            }
            if (level.map[cell]==Terrain.WALL&& Random.Int(10)==0){
                level.map[cell]=Terrain.WALL_DECO;
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("dir",dirs);
        bundle.put("x",x);
        bundle.put("y",y);
        bundle.put("fd",fromDir);
        bundle.put("dr",drawed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        dirs=bundle.getInt("dir");
        fromDir=bundle.getInt("fd");
        drawed=bundle.getBoolean("dr");
        x=bundle.getInt("x");
        y=bundle.getInt("y");
    }
}
