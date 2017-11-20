package com.watabou.noosa;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.SmartTexture;
import com.watabou.glwrap.Texture;

public class RepeatingImage extends Image {
    private final Image repeatingImage;
    public RepeatingImage(Image img,int w, int h){
        super();
        repeatingImage=img;
        size(w,h);
    }
    public RepeatingImage(Image img){
        super();
        repeatingImage=img;
        size(Math.round(img.width()),Math.round(img.height()));
    }
    public void size(int w, int h){
        Pixmap map = new Pixmap(w,h,Pixmap.Format.RGBA8888);
        Pixmap ico = new Pixmap(Math.round(repeatingImage.width),Math.round(repeatingImage.height), Pixmap.Format.RGBA8888);
        ico.drawPixmap(repeatingImage.texture.bitmap,0,0,Math.round((repeatingImage.frame().left*repeatingImage.texture.width)),Math.round((repeatingImage.frame().top*repeatingImage.texture.height)),ico.getWidth(),ico.getHeight());
        int top = 0;
        int left = 0;
        //System.out.println(ico.getWidth()+"|"+ico.getHeight());
        do {
            do{
                map.drawPixmap(ico,left,top);
                left+=ico.getWidth();
            } while (left<w);
            top+=ico.getHeight();
        } while (top<h);
        texture(map);
    }
//    public void size(int w, int h){
//        //System.out.println(w+"|"+h);
//        Pixmap pm = new Pixmap(w,h,repeatingImage.texture.bitmap.getFormat());
//        //System.out.println(pm.getFormat());
//        pm.drawPixmap(repeatingImage.texture.bitmap,0,0,Math.round(repeatingImage.frame.left*repeatingImage.texture.width),Math.round(repeatingImage.frame.top*repeatingImage.texture.height),w,h);
//        //System.out.println(Math.round(repeatingImage.frame.left*repeatingImage.texture.width)+"|"+Math.round(repeatingImage.frame.top*repeatingImage.texture.height));
//        SmartTexture st = new SmartTexture(pm, Texture.NEAREST, Texture.REPEAT);
//        texture(st);
//        frame(0,0,w/pm.getWidth(),h/pm.getHeight());
//    }
}
