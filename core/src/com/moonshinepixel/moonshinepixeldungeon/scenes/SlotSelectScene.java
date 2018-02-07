package com.moonshinepixel.moonshinepixeldungeon.scenes;

import com.moonshinepixel.moonshinepixeldungeon.*;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.*;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndChallenges;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndMessage;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndOptions;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndRunSettings;
import com.watabou.noosa.*;
import com.watabou.noosa.ui.Component;

public class SlotSelectScene extends PixelScene {

    private static final float GAP	= 4;
    private static final float BUTTON_HEIGHT	= 30;

    @Override
    public void create() {
        super.create();

        uiCamera.visible = false;

        float top = 0;

        int w = Camera.main.width;
        int h = Camera.main.height;

        Archs archs = new Archs();
        archs.setSize( w, h );
        add( archs );

        RenderedText title = renderText(
                Messages.get(this, "title"),9);
        title.hardlight(Window.SHPX_COLOR);
        title.x = (w - title.width()) / 2;
        title.y = GAP;
        align(title);
        add(title);

        top=title.y+title.height();

        NinePatch panel = Chrome.get(Chrome.Type.TOAST);
        panel.size(Math.min(w,150),h-top);
        panel.x=(w-panel.width())/2;
        panel.y=top;
        add(panel);

        ScrollPane sp = new ScrollPane(new Component());
        Component cp = sp.content();
        cp.clear();
        add(sp);

        ExitButton btnExit = new ExitButton();
        btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
        add( btnExit );

        SimpleButton sb = new SimpleButton(Icons.get(Icons.INFO)){
            @Override
            protected void onClick() {
                super.onClick();
                SlotSelectScene.this.add(new WndMessage(Messages.get(SlotSelectScene.class,"info")));
            }
        };
        add(sb);
        float scale1 = (panel.innerWidth()/6-2)/sb.width();
        sb.scale(scale1);
        sb.setPos(panel.x+panel.width()-panel.marginRight()-sb.width()-2,panel.y+panel.marginTop());

        SimpleButton sb2 = new SimpleButton(Icons.get(MoonshinePixelDungeon.challenges()>0?Icons.SETTING_C:Icons.SETTING_UC)){
            @Override
            protected void onClick() {
                super.onClick();
                SlotSelectScene.this.add(new WndRunSettings());
            }
        };
        add(sb2);
        float scale2 = (panel.innerWidth()/6-2)/sb2.width();
        sb2.scale(scale2);
        sb2.setPos(panel.x+panel.width()-panel.marginRight()-sb2.width()-2,sb.bottom());

        float bottom = 0;
        for (int i = 0; i<10; i++){
            final GamesInProgress.Info info = GamesInProgress.check(i);
            final int curSlot = i;
            GameButton gb = new GameButton(Messages.get(this,"empty")){
                @Override
                protected void onClick() {
                    super.onClick();
                    StartScene.curSlot=curSlot;
                    if (info!=null){
                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                        Game.switchScene( InterlevelScene.class );
                    } else {
                        Game.switchScene( StartScene.class );
                    }
                }

                @Override
                protected boolean onLongClick() {
                    StartScene.curSlot=curSlot;
                    if (info!=null){
                        SlotSelectScene.this.add(new WndOptions(Messages.get(SlotSelectScene.class,"options"),"",Messages.get(SlotSelectScene.class,"erase"),Messages.get(SlotSelectScene.class,"new_game"),Messages.get(SlotSelectScene.class,"challenges"),Messages.get(SlotSelectScene.class,"load")){
                            @Override
                            protected void onSelect(int index) {
                                super.onSelect(index);
                                switch (index){
                                    case 0:
                                        SlotSelectScene.this.add( new WndOptions(
                                                Messages.get(SlotSelectScene.class, "really"),
                                                Messages.get(StartScene.class, "warning"),
                                                Messages.get(SlotSelectScene.class, "yes"),
                                                Messages.get(StartScene.class, "no") ) {
                                            @Override
                                            protected void onSelect( int index ) {
                                                if (index == 0) {
                                                    Dungeon.deleteGame(curSlot,true);
                                                    MoonshinePixelDungeon.resetScene();
                                                }
                                            }
                                        } );
                                        break;
                                    case 1:
                                        SlotSelectScene.this.add( new WndOptions(
                                                Messages.get(StartScene.class, "really"),
                                                Messages.get(StartScene.class, "warning"),
                                                Messages.get(StartScene.class, "yes"),
                                                Messages.get(StartScene.class, "no") ) {
                                            @Override
                                            protected void onSelect( int index ) {
                                                if (index == 0) {
                                                    Dungeon.deleteGame(curSlot,true);
                                                    MoonshinePixelDungeon.switchNoFade(StartScene.class);
                                                }
                                            }
                                        } );
                                        break;
                                    case 2:
                                        SlotSelectScene.this.add(new WndChallenges(Challenges.challengesFromSlot(curSlot),false));
                                        break;
                                    case 3:
                                        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
                                        Game.switchScene( InterlevelScene.class );
                                        break;
                                }
                            }
                        });
                        return true;
                    }
                    Game.switchScene( StartScene.class );
                    return true;
                }
            };
            if (info!=null){
                gb.text(Messages.titleCase(info.heroClass.title()));
                gb.secondary(Messages.get(StartScene.class, "depth_level", Dungeon.isChallenged(Challenges.AMNESIA,Challenges.challengesFromSlot(curSlot))?"??":Dungeon.showDepth[info.depth], Dungeon.isChallenged(Challenges.AMNESIA,Challenges.challengesFromSlot(curSlot))||Dungeon.isChallenged(Challenges.ANALGESIA,Challenges.challengesFromSlot(curSlot))?"??":info.level ), true);
                gb.icon(Icons.get(info.heroClass));

            }
            gb.setRect(0,bottom,panel.innerWidth()*5/6,BUTTON_HEIGHT);
            cp.add(gb);
            bottom=gb.bottom()+GAP/2;
        }
        cp.setSize(panel.innerWidth(),bottom);
        sp.setRect(panel.x+panel.marginLeft(),panel.y+panel.marginTop(),panel.innerWidth(),panel.innerHeight());

    }

    @Override
    protected void onBackPressed() {
        MoonshinePixelDungeon.switchNoFade(TitleScene.class);
    }

    private static class GameButton extends RedButton {

        private static final int SECONDARY_COLOR_N    = 0xCACFC2;
        private static final int SECONDARY_COLOR_H    = 0xFFFF88;

        private RenderedText secondary;

        public GameButton( String primary ) {
            super( primary );

            this.secondary.text( null );
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            remove(bg);
            bg=Chrome.get(Chrome.Type.GEM);
            add(bg);

            secondary = renderText( 6 );
            add( secondary );
        }

        @Override
        protected void layout() {
            super.layout();

            if (secondary.text().length() > 0) {
                text.y = y + (height - text.height() - secondary.baseLine()) / 2;

                secondary.x = x + (width - secondary.width()) / 2;
                secondary.y = text.y + text.height();
            } else {
                text.y = y + (height - text.baseLine()) / 2;
            }
            align(text);
            align(secondary);
            if (icon!=null)
                icon.x=x+bg.marginLeft();
        }

        public void secondary( String text, boolean highlighted ) {
            secondary.text( text );

            secondary.hardlight( highlighted ? SECONDARY_COLOR_H : SECONDARY_COLOR_N );
        }
    }
}
