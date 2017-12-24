package com.moonshinepixel.moonshinepixeldungeon.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.moonshinepixel.moonshinepixeldungeon.Assets;
import com.moonshinepixel.moonshinepixeldungeon.levels.Terrain;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.utils.GameArrays;

import java.util.HashMap;

public class ImageToMap {
    public static Integer[] createArray(Pixmap image, HashMap<Integer,Integer> RGB888_colors){
        Integer[] array = new Integer[image.getHeight()*image.getWidth()];
        int pos = 0;
        for (int y=0; y<image.getHeight();y++){
            for (int x=0;x<image.getWidth();x++){
                Color color = new Color(image.getPixel(x,y));
                Integer clr = RGB888_colors.get(Color.rgb888(color));
                if (clr!=null) {
                    array[pos] = clr;
                    pos++;
                } else {
                    throw new IllegalArgumentException(String.format("No tile found for color %1$s at x:%2$d|y:%3$d",color.toString(),x,y), new NullPointerException());
                }
            }
        }
        return array;
    }
    public static int[] mapFromImage(Image image){
        return (int[])GameArrays.simplify(createArray(image.texture.bitmap,colors));
    }
    public static int[] mapFromImage(String image){
        return (int[])GameArrays.simplify(createArray(TextureCache.getBitmap(image),colors));
    }
    public static final HashMap<Integer,Integer> colors = new HashMap<>();
    static {
        colors.put(0x000000, Terrain.WALL);
        colors.put(0xffffff, Terrain.EMPTY);
        colors.put(0x501e00, Terrain.DOOR);
        colors.put(0x646464, Terrain.WALL_DECO);
        colors.put(0xff0000, Terrain.INACTIVE_TRAP);
        colors.put(0xffff00, Terrain.ENTRANCE);
        colors.put(0x808000, Terrain.SIGN);
        colors.put(0x1e0a00, Terrain.LOCKED_DOOR);
        colors.put(0xff00ff, Terrain.PEDESTAL);
        colors.put(0x00ffff, Terrain.EXIT);
    }
}
