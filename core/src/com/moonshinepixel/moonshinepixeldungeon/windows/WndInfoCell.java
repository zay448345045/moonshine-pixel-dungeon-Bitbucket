/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.moonshinepixel.moonshinepixeldungeon.windows;

import com.moonshinepixel.moonshinepixeldungeon.actors.blobs.Blob;
import com.moonshinepixel.moonshinepixeldungeon.levels.Level;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTerrainTilemap;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTilemap;
import com.moonshinepixel.moonshinepixeldungeon.ui.RenderedTextMultiline;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.tiles.CustomTiledVisual;
import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Image;

public class WndInfoCell extends Window {
	
	private static final float GAP	= 2;
	
	private static final int WIDTH = 120;
	
	public WndInfoCell( int cell ) {
		
		super();
		
		int tile = Dungeon.level.map[cell];
		if (Level.water[cell]) {
			tile = Terrain.WATER;
		} else if (Level.pit[cell]) {
			tile = Terrain.CHASM;
		}

		CustomTiledVisual customTile = null;
		Image customImage = null;
		int x = cell % Dungeon.level.width();
		int y = cell / Dungeon.level.width();
		for (CustomTiledVisual i : Dungeon.level.customTiles){
			if ((x >= i.tileX && x < i.tileX+i.tileW) &&
					(y >= i.tileY && y < i.tileY+i.tileH)){
				if ((customImage = i.image(x - i.tileX, y - i.tileY)) != null) {
					x -= i.tileX;
					y -= i.tileY;
					customTile = i;
					break;
				}
			}
		}


		String desc = "";

		IconTitle titlebar = new IconTitle();
		if (customTile != null){
			titlebar.icon(customImage);

			String customName = customTile.name(x, y);
			if (customName != null) {
				titlebar.label(customName);
			} else {
				titlebar.label(Dungeon.level.tileName(tile));
			}

			String customDesc = customTile.desc(x, y);
			if (customDesc != null) {
				desc += customDesc;
			} else {
				desc += Dungeon.level.tileDesc(tile);
			}

		} else {

			if (tile == Terrain.WATER) {
				Image water = new Image(Dungeon.level.waterTex());
				water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
				titlebar.icon(water);
			} else {
				titlebar.icon(DungeonTerrainTilemap.tile( cell, tile ));
			}
			titlebar.label(Dungeon.level.tileName(tile));
			desc += Dungeon.level.tileDesc(tile);

		}
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		RenderedTextMultiline info = PixelScene.renderMultiline(6);
		add(info);

		for (Blob blob:Dungeon.level.blobs.values()) {
			if (blob.volume > 0 && blob.cur[cell] > 0 && blob.tileDesc() != null) {
				if (desc.length() > 0) {
					desc += "\n\n";
				}
				desc += blob.tileDesc();
			}
		}
		
		info.text( desc );
		info.maxWidth(WIDTH);
		info.setPos(titlebar.left(), titlebar.bottom() + GAP);
		
		resize( WIDTH, (int)(info.top() + info.height()) );
	}
}
