package com.moonshinepixel.moonshinepixeldungeon.levels.rooms.connection;

import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.traps.DeadlySpearTrap;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class WellConnectorRoom extends ConnectionRoom {


    @Override
    public void paint(Level level) {
        Painter.fill(level,left+1,top+1,width()-2,height()-2,level.tunnelTile());
        if (width()>4&&height()>4)
            Painter.fill(level,left+2,top+2,width()-4,height()-4,level.feeling==Level.Feeling.CHASM?Terrain.STATUE_SP:Terrain.STATUE);
        if (width()>6&&height()>6){
            Painter.fill(level,left+3,top+3,width()-6,height()-6,level.feeling==Level.Feeling.CHASM?Terrain.CHASM:Terrain.WALL);
        }
    }
}
