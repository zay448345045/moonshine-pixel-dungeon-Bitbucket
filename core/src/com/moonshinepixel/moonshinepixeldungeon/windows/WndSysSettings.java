package com.moonshinepixel.moonshinepixeldungeon.windows;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.ui.CheckBox;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;

public class WndSysSettings extends Window {
    private static final int WIDTH		    = 112;
    private static final int HEIGHT         = 138;
    private static final int SLIDER_HEIGHT	= 24;
    private static final int BTN_HEIGHT	    = 18;
    private static final int GAP_TINY 		= 2;
    private static final int GAP_SML 		= 6;
    private static final int GAP_LRG 		= 18;
    public WndSysSettings(){
        super();
        float top = 0;
        CheckBox updateCheckerBox = new CheckBox("Enable auto updater"){
            @Override
            public void checked(boolean value) {
                super.checked(value);
                MoonshinePixelDungeon.updateChecker(value);
            }
        };
        updateCheckerBox.setRect(0,top,WIDTH,BTN_HEIGHT);
        add(updateCheckerBox);
        updateCheckerBox.checked(MoonshinePixelDungeon.updateChecker());
        top += updateCheckerBox.height()+GAP_TINY;
        CheckBox startInGame = new CheckBox("Continue game on launch",7){
            @Override
            public void checked(boolean value) {
                super.checked(value);
                MoonshinePixelDungeon.startInGame(value);
            }
        };
        startInGame.setRect(0,top,WIDTH,BTN_HEIGHT);
        add(startInGame);
        startInGame.checked(MoonshinePixelDungeon.startInGame());
        top += startInGame.height();

        resize(WIDTH,HEIGHT/2);
    }
}
