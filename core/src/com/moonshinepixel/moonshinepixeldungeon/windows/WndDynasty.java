package com.moonshinepixel.moonshinepixeldungeon.windows;

import com.moonshinepixel.moonshinepixeldungeon.Dungeon;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.Rankings;
import com.moonshinepixel.moonshinepixeldungeon.Unlocks;
import com.moonshinepixel.moonshinepixeldungeon.actors.hero.HeroClass;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.RedButton;
import com.moonshinepixel.moonshinepixeldungeon.ui.TextField;
import com.moonshinepixel.moonshinepixeldungeon.ui.Window;

public class WndDynasty extends Window {

    private String name = "";
    private HeroClass hc = Dungeon.hero.heroClass;
    private RedButton ok;

    private static final int WIDTH	= 120;

    public WndDynasty(boolean first){
        Rankings.INSTANCE.load();
        float top = 0;
        if (first){
            TextField tf = new TextField(Messages.get(this,"name")){
                @Override
                public void onTextChange() {
                    if (!name.equals(""))
                        name=text();
                    validate();
                }

                @Override
                public void onTextCancel() {
                }
            };
            tf.setRect(0,top,WIDTH,BTN_HEIGHT);
            add(tf);
            top=tf.bottom()+GAP_SML;
        }
        ok = new RedButton(Messages.get(this,"create")){
            @Override
            protected void onClick() {
                super.onClick();
                Rankings.INSTANCE.beginDynasty(name);
                hide();
            }
        };
        ok.setRect(0,top,WIDTH,HEIGHT);
        add(ok);
        top=ok.bottom();
        resize(WIDTH,(int)top);
        validate();
    }

    private void validate(){
        ok.enable(!name.equals(""));
    }

    @Override
    public void onBackPressed() {
    }
}
