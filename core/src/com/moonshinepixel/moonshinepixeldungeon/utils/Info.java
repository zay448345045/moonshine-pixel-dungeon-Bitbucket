package com.moonshinepixel.moonshinepixeldungeon.utils;

import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.ui.Icons;
import com.moonshinepixel.moonshinepixeldungeon.windows.WndHardNotification;

import java.util.LinkedHashSet;

public class Info {

    //WARNING: Only VERY important info going here

    public static LinkedHashSet<WndHardNotification> getInfo(){
        int lastInfo = MoonshinePixelDungeon.infoLevel();
        LinkedHashSet<WndHardNotification> windows = new LinkedHashSet();

        if (lastInfo<57){
            windows.add(new WndHardNotification(Icons.get(Icons.INFO),"Update checker","Auto update checker is now aviliable on android!\nDisabled by default, you can enable it in settings.\nEven with update checker disabled you can manually check for updates","Ok",3));
        }


        MoonshinePixelDungeon.infoLevel(57);
        return windows;
    }
}
