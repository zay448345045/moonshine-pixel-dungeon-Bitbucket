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
package com.moonshinepixel.moonshinepixeldungeon.ui;

import com.moonshinepixel.moonshinepixeldungeon.scenes.PixelScene;
import com.watabou.input.NoosaInputProcessor;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.TouchArea;
import com.watabou.noosa.Visual;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;

public class ScrollPane extends Component {

	protected static final int THUMB_COLOR		= 0xFF7b8073;
	protected static final float THUMB_ALPHA	= 0.5f;

	protected TouchController controller;
	protected Component content;
	protected ColorBlock thumb;

	protected float minX;
	protected float minY;
	protected float maxX;
	protected float maxY;

	public ScrollPane( Component content ) {
		super();

		this.content = content;
		addToBack( content );

		width = content.width();
		height = content.height();


		content.camera = new Camera(0, 0, 1, 1, PixelScene.defaultZoom);
		Camera.add( content.camera );
	}

	@Override
	public void destroy() {
		super.destroy();
		Camera.remove( content.camera );
	}

	public void scrollTo( float x, float y ) {
		content.camera.scroll.set( x, y );
	}

	@Override
	protected void createChildren() {
		controller = new TouchController();
		add( controller );

		thumb = new ColorBlock( 1, 1, THUMB_COLOR );
		thumb.am = THUMB_ALPHA;
		add( thumb );
	}

	public float thumbWidth(){
		return 2;
	}

	@Override
	protected void layout() {

		content.setPos( 0, 0 );
		controller.x = x;
		controller.y = y;
		controller.width = width;
		controller.height = height;

		Point p;
		try {
			if (parent!=null)camera=parent.camera;
			p = camera().cameraToScreen( x, y );
		} catch (Exception e){
		    camera();
			p=new Point(0,0);
		}
		Camera cs = content.camera;
		cs.x = p.x;
		cs.y = p.y;
		cs.resize( (int)width, (int)height );

		thumb.visible = height < content.height();
		if (thumb.visible) {
			thumb.scale.set( thumbWidth(), height * height / content.height() );
			thumb.x = right() - thumb.width();
			thumb.y = y;
		}
	}

	public Component content() {
		return content;
	}

	public void onClick( float x, float y ) {
	}
	public void onTouchDown( float x, float y ) {
	}
	public void onDrag(){
    }

	public class TouchController extends TouchArea {

		private float dragThreshold;

		public TouchController() {
			super( 0, 0, 0, 0 );
			dragThreshold = PixelScene.defaultZoom * 8;
		}


        @Override
        protected void onTouchDown(NoosaInputProcessor.Touch touch) {
            if (dragging) {
            } else {

                PointF p = content.camera.screenToCamera( (int)touch.current.x, (int)touch.current.y );
                ScrollPane.this.onTouchDown( p.x, p.y );
            }
        }

        @Override
		protected void onTouchUp( NoosaInputProcessor.Touch touch ) {
			if (dragging) {

				dragging = false;
				thumb.am = THUMB_ALPHA;

			} else {

				PointF p = content.camera.screenToCamera( (int)touch.current.x, (int)touch.current.y );
				ScrollPane.this.onClick( p.x, p.y );
			}
		}

		private boolean dragging = false;
		private PointF lastPos = new PointF();

		@Override
		protected void onDrag( NoosaInputProcessor.Touch t ) {
			if (dragging) {

				doScroll(t.current);

			} else if (PointF.distance( t.current, t.start ) > dragThreshold) {

			    ScrollPane.this.onDrag();
				dragging = true;
				lastPos.set( t.current );
				thumb.am = 1;

			}
		}

		@Override
		public boolean onMouseScroll(int scroll) {
			PointF newPt = new PointF(lastPos);
			newPt.y -= scroll * content.camera.zoom * 4;
			doScroll(newPt);
			return true;
		}

		private void doScroll(PointF current) {
			final Camera c = content.camera;

			c.scroll.offset( PointF.diff(lastPos, current).invScale( c.zoom ) );
			if (c.scroll.x + width > content.width()) {
				c.scroll.x = content.width() - width;
			}
			if (c.scroll.x < 0) {
				c.scroll.x = 0;
			}
			if (c.scroll.y + height > content.height()) {
				c.scroll.y = content.height() - height;
			}
			if (c.scroll.y < 0) {
				c.scroll.y = 0;
			}

			thumb.y = y + height * c.scroll.y / content.height();

			lastPos.set(current);
		}
	}
}
