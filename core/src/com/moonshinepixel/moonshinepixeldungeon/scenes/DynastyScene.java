package com.moonshinepixel.moonshinepixeldungeon.scenes;

import com.moonshinepixel.moonshinepixeldungeon.*;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.Archs;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.TextField;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndError;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndOptions;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Music;

public class DynastyScene extends PixelScene {

    private static final int WIDTH			= 120;
    private static final int BTN_HEIGHT		= 18;
    private static final float GAP	        = 6;
    private static final float LARGE_GAP	= 8;

    private String name = "";
    private RedButton ok;
    public static boolean surface = false;

    public boolean enabled = false;

    {
        Rankings.INSTANCE.load();
    }

    @Override
    public void create() {

        super.create();

        if (!Dungeon.dynastyID.equals("")||!enabled) {
            hide();
            return;
        }

        Music.INSTANCE.play( Assets.THEME, true );
        Music.INSTANCE.volume( MoonshinePixelDungeon.musicVol() / 10f );

        uiCamera.visible = false;

        int w = Camera.main.width;
        int h = Camera.main.height;
        float top = 0;

        Archs archs = new Archs();
        archs.setSize( w, h );
        add( archs );

        final boolean cancreate = Rankings.activeDynastiesIDS().size()< Unlocks.getDynSize();

        NinePatch panel = Chrome.get(Chrome.Type.TOAST);
        panel.size(WIDTH,100);
        panel.x=(w-WIDTH)/2;
        add(panel);


        RenderedText title = renderText(Messages.get(this,"title"),9);
        title.hardlight(Window.SHPX_COLOR);
        title.x=(panel.innerWidth()-title.width())/2+panel.marginLeft()+panel.x;
        add(title);
        align(title);
        top+=title.height()+GAP;
        TextField tf = new TextField(Messages.get(this,"name")){
            @Override
            public void onTextChange() {
                name=text();
                validate();
            }

            @Override
            public void onTextCancel() {
            }
        };
        tf.setSize(panel.innerWidth(),BTN_HEIGHT);
        tf.setPos((panel.innerWidth()-tf.width())/2+panel.marginLeft()+panel.x,0);
        add(tf);
        top+=tf.height()+GAP;
        ok = new RedButton(Messages.get(this,"create")){
            @Override
            protected void onClick() {
                super.onClick();
                if (cancreate) {
                    Rankings.INSTANCE.beginDynasty(name);
                    DynastyScene.this.hide();
                } else {
                    DynastyScene.this.add(new WndError(Messages.get(DynastyScene.class,"noplace")));
                }
            }
        };
        ok.setSize(panel.innerWidth()/2,BTN_HEIGHT);
        ok.setPos(panel.innerWidth()/2-ok.width()+panel.marginLeft()+panel.x,0);
        add(ok);
        top+=ok.height();

        RedButton cancel = new RedButton(Messages.get(this,"cancel")){
            @Override
            protected void onClick() {
                super.onClick();
                if (cancreate)
                    DynastyScene.this.add(new WndOptions(
                            Messages.get(DynastyScene.class, "really"),
                            Messages.get(DynastyScene.class, "warning"),
                            Messages.get(DynastyScene.class, "yes"),
                            Messages.get(DynastyScene.class, "no") ){
                        @Override
                        protected void onSelect(int index) {
                            super.onSelect(index);
                            if (index==0)DynastyScene.this.hide();
                        }
                    });
                else hide();
            }
        };
        cancel.setRect(ok.right(),0,panel.innerWidth()/2,ok.height());
        add(cancel);

        panel.y=(h-panel.height())/2;
        title.y=panel.y+GAP;
        tf.setPos(tf.left(),title.y+title.height+GAP);
        ok.setPos(ok.left(),tf.bottom()+GAP);
        cancel.setPos(ok.right(),ok.top());

        panel.size(WIDTH,(cancel.bottom()+GAP-panel.y));


        validate();
        fadeIn();
    }

    private void validate(){
        ok.enable(!name.equals(""));
    }

    private void hide(){
        if (surface){
            Game.switchScene( SurfaceScene.class );
        } else {
            Game.switchScene( RankingsScene.class );
        }
    }

    @Override
    protected void onBackPressed() {
    }
}
