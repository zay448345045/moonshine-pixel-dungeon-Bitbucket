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
package com.moonshinepixel.moonshinepixeldungeon.scenes;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.actors.Actor;
import com.moonshinepixel.moonshinepixeldungeon.actors.Char;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.moonshinepixel.moonshinepixeldungeon.input.PDInputProcessor;
import com.moonshinepixel.moonshinepixeldungeon.items.Heap;
import com.moonshinepixel.moonshinepixeldungeon.sprites.CharSprite;
import com.moonshinepixel.moonshinepixeldungeon.tiles.DungeonTilemap;
import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.watabou.noosa.Camera;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.noosa.TouchArea;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public class CellSelector extends TouchArea<GameAction> {
	public Listener listener = null;
	
	public boolean enabled;

	private float mouseZoom;
	
	private float dragThreshold;

	private NoosaInputProcessor.Key<GameAction> pressedKey;
	private float pressedKeySpeedFactor = 0.05f;
	
	public CellSelector( DungeonTilemap map ) {
		super( map );
		camera = map.camera();

		mouseZoom = camera.zoom;
		
		dragThreshold = PixelScene.defaultZoom * DungeonTilemap.SIZE / 2;
	}
	
	@Override
	protected void onClick( NoosaInputProcessor.Touch touch ) {
		if (dragging) {
			
			dragging = false;
			
		} else {
			
			PointF p = Camera.main.screenToCamera( (int)touch.current.x, (int)touch.current.y );
			for (Char mob : Dungeon.level.mobs){
				if (mob.sprite != null && mob.sprite.overlapsPoint( p.x, p.y)){
					select( mob.pos );
					return;
				}
			}

			for (Heap heap : Dungeon.level.heaps.values()){
				if (heap.sprite != null && heap.sprite.overlapsPoint( p.x, p.y)){
					select( heap.pos );
					return;
				}
			}
			
			select( ((DungeonTilemap)target).screenToTile(
				(int)touch.current.x,
				(int)touch.current.y,
					true ) );
		}
	}

	@Override
	public boolean onKeyDown(NoosaInputProcessor.Key<GameAction> key) {

		try {
			switch (key.action) {
				case ZOOM_IN:
					zoom(camera.zoom + 1);
					return true;
				case ZOOM_OUT:
					zoom(camera.zoom - 1);
					return true;
				case ZOOM_DEFAULT:
					zoom(PixelScene.defaultZoom);
					return true;
			}

			boolean handled = true;
			int x = 0, y = 0;
			switch (key.action) {
				case MOVE_UP:
					y = -1;
					break;
				case MOVE_DOWN:
					y = 1;
					break;
				case MOVE_LEFT:
					x = -1;
					break;
				case MOVE_RIGHT:
					x = 1;
					break;
				case MOVE_TOP_LEFT:
					x = -1;
					y = -1;
					break;
				case MOVE_TOP_RIGHT:
					x = 1;
					y = -1;
					break;
				case MOVE_BOTTOM_LEFT:
					x = -1;
					y = 1;
					break;
				case MOVE_BOTTOM_RIGHT:
					x = 1;
					y = 1;
					break;
				case OPERATE:
					break;
				default:
					handled = false;
					break;
			}

			if (handled) {
				CharSprite.setMoveInterval(Math.max(0.1f, 0.1f + pressedKeySpeedFactor));
				Point point = DungeonTilemap.tileToPoint(Dungeon.hero.pos);
				point.x += x;
				point.y += y;
				pressedKey = key;
				select(DungeonTilemap.pointToTile(point));
			}

			return handled;
		} catch (Exception e){
			return false;
		}
	}

	@Override
	public boolean onKeyUp( PDInputProcessor.Key<GameAction> key ) {
		try {
			if (pressedKey != null && key.action == pressedKey.action) {
				resetKeyHold();
			}
			switch (key.code) {
				case PDInputProcessor.MODIFIER_KEY:
					mouseZoom = zoom(Math.round(mouseZoom));
					return true;
				default:
					return false;
			}
		} catch (Throwable e){
			return false;
		}
	}

	public void processKeyHold(){
		if (pressedKey != null) {
			enabled = true;
			pressedKeySpeedFactor -= 0.025f;
			CharSprite.setMoveInterval(Math.max(0.1f, 0.1f + pressedKeySpeedFactor));
			onKeyDown(pressedKey);
		}
	}

	public void resetKeyHold(){
		pressedKeySpeedFactor = 0.05f;
		pressedKey = null;
		CharSprite.setMoveInterval(0.1f);
	}

	private float zoom( float value ) {

		value = GameMath.gate( PixelScene.minZoom, value, PixelScene.maxZoom );
		MoonshinePixelDungeon.zoom((int) (value - PixelScene.defaultZoom));
		camera.zoom( value );

		//Resets character sprite positions with the new camera zoom
		//This is important as characters are centered on a 16x16 tile, but may have any sprite size
		//This can lead to none-whole coordinate, which need to be aligned with the zoom
		for (Char c : Actor.chars()){
			if (c.sprite != null && !c.sprite.isMoving){
				c.sprite.point(c.sprite.worldToCamera(c.pos));
			}
		}

		return value;
	}

	public void select( int cell ) {
		if (enabled && listener != null && cell != -1) {
			
			listener.onSelect( cell );
			GameScene.ready();

		} else {
			
			GameScene.cancel();
			
		}
	}

	private boolean pinching = false;
	private NoosaInputProcessor.Touch another;
	private float startZoom;
	private float startSpan;
	
	@Override
	protected void onTouchDown( NoosaInputProcessor.Touch t ) {

		if (t != touch && another == null) {
					
			if (!touch.down) {
				touch = t;
				onTouchDown( t );
				return;
			}
			
			pinching = true;
			
			another = t;
			startSpan = PointF.distance( touch.current, another.current );
			startZoom = camera.zoom;

			dragging = false;
		} else if (t != touch) {
			reset();
		}
	}
	
	@Override
	protected void onTouchUp( NoosaInputProcessor.Touch t ) {
		if (pinching && (t == touch || t == another)) {
			
			pinching = false;
			
			zoom(Math.round( camera.zoom ));
			
			dragging = true;
			if (t == touch) {
				touch = another;
			}
			another = null;
			lastPos.set( touch.current );
		}
	}
	
	private boolean dragging = false;
	private PointF lastPos = new PointF();

	@Override
	public boolean onMouseScroll(int scroll) {
		mouseZoom -= scroll / 3f;
		if (PDInputProcessor.modifier) {
			mouseZoom = zoom( mouseZoom );
		} else {
			zoom( Math.round( mouseZoom ) );
			mouseZoom = GameMath.gate( PixelScene.minZoom, mouseZoom, PixelScene.maxZoom );
		}
		return true;
	}

	@Override
	protected void onDrag( NoosaInputProcessor.Touch t ) {
		 
		camera.target = null;

		if (pinching) {

			float curSpan = PointF.distance( touch.current, another.current );
			if (startSpan != 0){
				camera.zoom( GameMath.gate(
					PixelScene.minZoom,
					startZoom * curSpan / startSpan,
					PixelScene.maxZoom ) );
			}

		} else {
		
			if (!dragging && PointF.distance( t.current, t.start ) > dragThreshold) {
				
				dragging = true;
				lastPos.set( t.current );
				
			} else if (dragging) {
				camera.scroll.offset( PointF.diff( lastPos, t.current ).invScale( camera.zoom ) );
				lastPos.set( t.current );
			}
		}
		
	}
	
	public void cancel() {
		
		if (listener != null) {
			listener.onSelect( null );
		}
		
		GameScene.ready();
	}

	@Override
	public void reset() {
		super.reset();
		another = null;
		if (pinching){
			pinching = false;

			zoom( Math.round( camera.zoom ) );
		}
	}

	public void enable(boolean value){
		if (enabled != value){
			enabled = value;
		}
	}

	public interface Listener {
		void onSelect( Integer cell );
		String prompt();
	}
}
