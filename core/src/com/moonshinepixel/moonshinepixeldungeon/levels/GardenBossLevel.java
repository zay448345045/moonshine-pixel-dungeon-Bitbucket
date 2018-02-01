package com.moonshinepixel.moonshinepixeldungeon.levels;

import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.Challenges;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.ForestSpirit;
import com.moonshinepixel.moonshinepixeldungeon.actors.mobs.Mob;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.items.Item;
import com.moonshinepixel.moonshinepixeldungeon.items.WeaponKit;
import com.moonshinepixel.moonshinepixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.Painter;
import com.moonshinepixel.moonshinepixeldungeon.levels.painters.SewerPainter;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.Room;
import com.moonshinepixel.moonshinepixeldungeon.levels.rooms.bossRooms.GardenBossRoom;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.plants.Plant;
import com.moonshinepixel.moonshinepixeldungeon.scenes.GameScene;
import com.moonshinepixel.moonshinepixeldungeon.ui.HealthIndicator;
import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.moonshinepixel.moonshinepixeldungeon.utils.ImageToMap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class GardenBossLevel extends Level {

    {
        color1 = 0x8f7e35;
        color2 = 0x7b6932;
        lightaffected=false;
    }

    public boolean[] watr;
    public boolean[] gras;

    private ForestSpirit boss = new ForestSpirit();
    private boolean bosshide = true;

    public static final int ROOMSIZE = 5;

    private GardenBossRoom[][] rooms = new GardenBossRoom[8][8];

    private enum State{START,BATTLE,WIN}

    private State state = State.START;
    private ArrayList<Item> storedItems = new ArrayList<>();

    @Override
    public String tilesTex() {
        return Assets.TILES_GARDEN;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_PRISON;
    }

    public void decorate(){
        for (int i = 0; i<length;i++){
            switch (map[i]){
                case Terrain.GRASS:
                    gras[i]=true;
                case Terrain.WATER:
                    watr[i]=true;
            }
        }
    }

    @Override
    protected boolean build() {
        setSize(49, 49);
        Arrays.fill(map,Terrain.EMPTY);
//        Painter.fill(this,0,0,48,48,Terrain.EMPTY);
        gras=new boolean[length];
        watr=new boolean[length];
        new SewerPainter()
                .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 5)
                .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 4).paint(this,null);
        decorate();
        map = ImageToMap.mapFromImage(Assets.MAP_GARDENBOSS_1);
        buildFlagMaps();
        cleanWalls();

        entrance = 15+29*width();
        return true;
    }

    private GardenBossRoom setRoom(int gridX, int gridY, GardenBossRoom from){
        if (rooms[gridX][gridY]!=null){
            clearRoom(rooms[gridX][gridY]);
        }
        GardenBossRoom r = new GardenBossRoom();
        rooms[gridX][gridY]=r;
        r.drawed=false;
        Point center = new Point((ROOMSIZE+1)*(gridX+1)-(ROOMSIZE+1)/2,(ROOMSIZE+1)*(gridY+1)-(ROOMSIZE+1)/2);
        r.set(center.x-2,center.y-2,center.x+2,center.y+2);
        r.x=gridX;
        r.y=gridY;
        if (from==null)from=r;
        r.fromDir=from.x<r.x?GardenBossRoom.LEFT:from.x>r.x?GardenBossRoom.RIGHT:from.y<r.y?GardenBossRoom.UP:from.y>r.y?GardenBossRoom.DOWN:0;
        if (gridX>0&&(Random.Int(2)==0||from.x<r.x))r.dirs|=GardenBossRoom.LEFT;
        if (gridX<7&&(Random.Int(2)==0||from.x>r.x))r.dirs|=GardenBossRoom.RIGHT;
        if (gridY>0&&(Random.Int(2)==0||from.y<r.y))r.dirs|=GardenBossRoom.UP;
        if (gridY<7&&(Random.Int(2)==0||from.y>r.y))r.dirs|=GardenBossRoom.DOWN;
        return r;
    }

    public GardenBossRoom draw(GardenBossRoom r){
        if (r!=null) {
            r.drawed = true;
            for (int x = 0; x < 7; x++) {
                for (int y = 0; y < 7; y++) {
                    Room rr = rooms[x][y];
                    if (rr != null && rr != r) {
                        Painter.fill(this, rr, Terrain.WALL);
                        rooms[x][y] = null;
                    }
                }
            }
            clearEntities(r);
            HashSet<Point> cells = new HashSet<>();
            if ((r.dirs & GardenBossRoom.LEFT) != 0) {
                GardenBossRoom nr = setRoom(r.x - 1, r.y, r);
                nr.paint(this);
                GameScene.updateMap(nr);
                cells.add(nr.center());
            }
            if ((r.dirs & GardenBossRoom.RIGHT) != 0) {
                GardenBossRoom nr = setRoom(r.x + 1, r.y, r);
                nr.paint(this);
                GameScene.updateMap(nr);
                cells.add(nr.center());
            }
            if ((r.dirs & GardenBossRoom.UP) != 0) {
                GardenBossRoom nr = setRoom(r.x, r.y - 1, r);
                nr.paint(this);
                GameScene.updateMap(nr);
                cells.add(nr.center());
            }
            if ((r.dirs & GardenBossRoom.DOWN) != 0) {
                GardenBossRoom nr = setRoom(r.x, r.y + 1, r);
                nr.paint(this);
                GameScene.updateMap(nr);
                cells.add(nr.center());
            }

            if (bosshide && Random.Int(8) == 0) {
                boss.pos = pointToCell(Random.element(cells));
                GameScene.add(boss);
                ScrollOfTeleportation.appear(boss, boss.pos);
                bosshide = false;
                boss.enable();
                boss.say();
            }
            BArray.setFalse(visited);
            BArray.setFalse(mapped);
            try {
                Dungeon.observe();
            } catch (Exception ignored) {
            }
            buildFlagMaps();
        }
        return r;
    }

    public void startBattle(){
        boss.yell( Messages.get(boss, "notice") );
        seal();
        Dungeon.hero.viewDistance=viewDistance=2;

        changeMap(ImageToMap.mapFromImage(Assets.MAP_GARDENBOSS_2));
        state=State.BATTLE;

        GardenBossRoom r = setRoom(3,3,null);
        r.dirs=GardenBossRoom.ALL;
//        connect(r);
        r.paint(this);
        draw(r);

        Dungeon.hero.interrupt();
        Dungeon.hero.pos=pointToCell(r.center());
        Dungeon.hero.sprite.interruptMotion();
        Dungeon.hero.sprite.place(Dungeon.hero.pos);
        Camera.main.focusOn(Dungeon.hero.sprite);
        clearEntities(r);
        GameScene.flash(0xFFFFFF);
        Sample.INSTANCE.play(Assets.SND_BLAST);
        buildFlagMaps();
        cleanWalls();
        GameScene.updateMap();
    }

    @Override
    public void unseal() {
        super.unseal();
        win();
    }

    public void win(){
        Dungeon.hero.viewDistance=viewDistance=8;
        state=State.WIN;
        clearEntities(null);
        for (Item itm : storedItems){
            itm.drop(15+15*width());
        }
        Dungeon.hero.interrupt();
        Dungeon.hero.pos=15+15*width();
        Dungeon.hero.sprite.interruptMotion();
        Dungeon.hero.sprite.place(Dungeon.hero.pos);
        changeMap(ImageToMap.mapFromImage(Assets.MAP_GARDENBOSS_3));
        GameScene.flash(0xFFFFFF);
        Sample.INSTANCE.play(Assets.SND_BLAST);
        new WeaponKit().drop(15+15*width()).sprite.drop();
    }


    @Override
    public void press(int cell, Char ch) {
        super.press(cell, ch);
        if (ch==Dungeon.hero){
            switch (state){
                case START:
                    if (cell == 15 + 15 * width()) startBattle();
                    break;

                case BATTLE:
                    GardenBossRoom r = room(cell);
                    if (r!=null){
                        if (!r.drawed) {
                            int a=0;
                            draw(r);
                        }
                    }
            }
        }
    }

    @Override
    public GardenBossRoom room(int pos){
        Point p = new Point(PathFinder.pos2x(pos),PathFinder.pos2y(pos));
        for (GardenBossRoom[] ra:rooms){
            for (GardenBossRoom r:ra){
                if (r!=null&&r.inside(p))return r;
            }
        }
        return null;
    }

    private void clearEntities(Room safeArea){
        for (Heap heap : heaps.values()){
            if ((safeArea == null || !safeArea.inside(cellToPoint(heap.pos)))&&!Dungeon.visible[heap.pos]){
                storedItems.addAll(heap.items);
                heap.destroy();
            }
        }
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[Dungeon.level.mobs.size()])){
            if (!Dungeon.visible[mob.pos]) {
                if (HealthIndicator.instance.target() == mob) {
                    HealthIndicator.instance.target(null);
                    HealthIndicator.instance.visible = false;
                }
                if (mob != boss && (safeArea == null || !safeArea.inside(cellToPoint(mob.pos)))) {
                    mob.destroy();
                    if (mob.sprite != null)
                        mob.sprite.killAndErase();
                } else if ((safeArea == null || !safeArea.inside(cellToPoint(mob.pos)))) {
                    Actor.remove(boss);
                    mobs.remove(boss);
                    boss.sprite.kill();
                    bosshide = true;
                }
            }
        }
        for (Plant plant : plants.values()){
            if ((safeArea == null || !safeArea.inside(cellToPoint(plant.pos)))&&!Dungeon.visible[plant.pos]){
                plants.remove(plant.pos);
            }
        }
    }
    private void clearRoom(Room room){
        for (Heap heap : heaps.values()){
            if (room.inside(cellToPoint(heap.pos))){
                storedItems.addAll(heap.items);
                heap.destroy();
            }
        }
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[Dungeon.level.mobs.size()])){
            if (HealthIndicator.instance.target()==mob) {
                HealthIndicator.instance.target(null);
                HealthIndicator.instance.visible = false;
            }
            if (mob != boss && room.inside(cellToPoint(mob.pos))){
                mob.destroy();
                if (mob.sprite != null)
                    mob.sprite.killAndErase();
            } else if((room.inside(cellToPoint(mob.pos)))){
                Actor.remove(boss);
                mobs.remove(boss);
                boss.sprite.kill();
                bosshide=true;
            }
        }
        for (Plant plant : plants.values()){
            if (room.inside(cellToPoint(plant.pos))){
                plants.remove(plant.pos);
            }
        }
    }

    private void changeMap(int[] map){
        this.map = map.clone();
        buildFlagMaps();
        cleanWalls();

        exit = entrance = 0;
        for (int i = 0; i < length(); i ++)
            if (map[i] == Terrain.ENTRANCE)
                entrance = i;
            else if (map[i] == Terrain.EXIT)
                exit = i;

        visited = mapped = new boolean[length()];
        for (Blob blob: blobs.values()){
            blob.fullyClear();
        }
        addVisuals(); //this also resets existing visuals

        GameScene.resetMap();
        Dungeon.observe();
    }


    private static final String STATE	        = "state";
    private static final String STORED_ITEMS    = "storeditems";
    private static final String ROOMS           = "rooms";
    private static final String BOSS            = "boss";
    private static final String WATR            = "watr";
    private static final String GRAS            = "gras";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put( STATE, state );
        bundle.put( BOSS, boss );
        bundle.put( STORED_ITEMS, storedItems);
        bundle.put(WATR, watr);
        bundle.put( GRAS, gras);
        for(int x = 0; x<8;x++){
            for (int y = 0; y<8;y++){
                if (rooms[x][y]!=null)
                    bundle.put(ROOMS+x+y, rooms[x][y]);
            }
        }
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        state = bundle.getEnum(STATE, State.class);

        for (Bundlable item : bundle.getCollection(STORED_ITEMS)) {
            storedItems.add((Item) item);
        }
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                if (bundle.contains(ROOMS + x + y)) {
                    rooms[x][y] = (GardenBossRoom) bundle.get(ROOMS + x + y);
                    rooms[x][y].postRestore(this);
                }
            }
        }
        boss=(ForestSpirit) bundle.get(BOSS);
        bosshide=true;
        gras=bundle.getBooleanArray(GRAS);
        watr=bundle.getBooleanArray(WATR);
        for (Mob m : mobs) {
            if (m instanceof ForestSpirit) {
                boss = (ForestSpirit)m;
                bosshide=false;
                break;
            }
        }

    }

    @Override
    public boolean amnesia() {
        return super.amnesia()||state==State.BATTLE;
    }

    @Override
    protected void createMobs() {

    }

    @Override
    protected void createItems() {

    }

    @Override
    public boolean mapable() {
        return state!=State.BATTLE;
    }

    @Override
    public boolean teleportable() {
        return state!=State.BATTLE;
    }
}
