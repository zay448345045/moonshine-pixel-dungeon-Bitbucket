package com.moonshinepixel.moonshinepixeldungeon.tiles;

import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;

public class GridTileMap extends DungeonTilemap {

	public GridTileMap() {
		super("visual_grid.png");

		map( Dungeon.level.map, Dungeon.level.width() );
	}

	private int gridSetting = -1;

	@Override
	public synchronized void updateMap() {
		gridSetting = MoonshinePixelDungeon.visualGrid();
		super.updateMap();
	}

	@Override
	protected int getTileVisual(int pos, int tile, boolean flat) {
		if (gridSetting == -1 || (pos % mapWidth) % 2 != (pos / mapWidth) % 2){
			return -1;
		} else if (DungeonTileSheet.floorTile(tile) || tile == Terrain.HIGH_GRASS) {
			return gridSetting;
		} else if (DungeonTileSheet.doorTile(tile)){
			if (DungeonTileSheet.wallStitcheable(map[pos - mapWidth])){
				return 12 + gridSetting;
			} else if ( tile == Terrain.OPEN_DOOR){
				return 8 + gridSetting;
			} else {
				return 4 + gridSetting;
			}
		} else {
			return -1;
		}
	}

	@Override
	protected boolean needsRender(int pos) {
		return data[pos] != -1;
	}

}
