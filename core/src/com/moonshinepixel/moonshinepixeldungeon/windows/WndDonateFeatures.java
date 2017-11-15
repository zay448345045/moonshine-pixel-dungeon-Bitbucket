package com.moonshinepixel.moonshinepixeldungeon.windows;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.Unlocks;
import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.moonshinepixel.moonshinepixeldungeon.ui.*;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;

public class WndDonateFeatures extends Window {
    private static final int WIDTH		    = 112;
    private static final int HEIGHT         = 138;
    private static final int SLIDER_HEIGHT	= 24;
    private static final int BTN_HEIGHT	    = 18;
    private static final int GAP_TINY 		= 2;
    private static final int GAP_SML 		= 6;
    private static final int GAP_LRG 		= 18;
    private RedButton rb;
    public WndDonateFeatures(){
        super();

        final Group par = this;
        final WndDonateFeatures wnd = this;

        float top = 0;

        RenderedTextMultiline rtm = new RenderedTextMultiline(12);
        rtm.text(Messages.get(this,"title"));
        rtm.hardlight(Window.SHPX_COLOR);
        rtm.setPos(0,top);
        add(rtm);
        top=rtm.bottom()+GAP_TINY;

        RenderedTextMultiline renaming = new RenderedTextMultiline(9);
        renaming.text(Messages.get(this, "locked"),WIDTH);
        renaming.setPos(0, top);
        add(renaming);
        top = renaming.bottom() + GAP_SML;

        CheckBox goldUiCheckbox = new CheckBox(Unlocks.GOLDHUD.dispName()){
            boolean firstcheck = true;
            @Override
            public void checked(boolean value) {
                super.checked(value);
                MoonshinePixelDungeon.hudType(value?1:0);
                if(!firstcheck) {
                    wnd.parent.add(new WndDonateFeatures());
                    wnd.onBackPressed();
                } else {firstcheck=false;}
            }
        };
        goldUiCheckbox.setRect(0,top,WIDTH,BTN_HEIGHT);
        add(goldUiCheckbox);
        goldUiCheckbox.lock(!Unlocks.isUnlocked(Unlocks.GOLDHUD));
//        goldUiCheckbox.lock(true);
        goldUiCheckbox.checked(MoonshinePixelDungeon.hudType()==1);
        top+=goldUiCheckbox.height()+GAP_TINY;
        goldUiCheckbox.enable(Unlocks.isUnlocked(Unlocks.GOLDHUD));
//        goldUiCheckbox.enable(false);

        final String[] names = new String[]{"Default","Empty","Steel","Dark","Crystal"};

        rb = new RedButton("Menu button style: ", 8){
            @Override
            protected void onClick() {
                super.onClick();
                WndOptions wo = new WndOptions("Choose menu button style","",names){
                    @Override
                    protected void onSelect(int index) {
                        MoonshinePixelDungeon.buttonType(index);
                        rb.text("Menu button style: "+names[index]);
                    }
                }.setEnabled(Unlocks.getButtonTypes(MoonshinePixelDungeon.unlocks())).setLocked(Unlocks.getButtonTypes(MoonshinePixelDungeon.unlocks()));
                wo.onSelect(MoonshinePixelDungeon.buttonType());
                par.add(wo);
            }
        };
        rb.text("Menu button style: "+names[MoonshinePixelDungeon.buttonType()]);
        rb.setRect(0,top,WIDTH,BTN_HEIGHT);
        add(rb);
        top=rb.bottom();

        resize(WIDTH,(int)top);
    }
}
