package com.watabou.noosa;

import com.badlogic.gdx.graphics.Pixmap;
import com.watabou.gltextures.TextureCache;

public class LayeredImage {
    Pixmap[] pixlayers;
    Pixmap image;
    LayeredImage(){

    }

    public LayeredImage(String ...layers){
        pixlayers = new Pixmap[layers.length];
        for (int i = 0; i<layers.length;i++){
            pixlayers[i]= TextureCache.getBitmap(layers[i]);
        }
    }
    public LayeredImage(Pixmap ...layers){
        pixlayers = new Pixmap[layers.length];
        for (int i = 0; i<layers.length;i++){
            pixlayers[i]=layers[i];
        }
    }

    public Pixmap getImage() {
        if (image!=null){
            return image;
        } else {
            try {
                return drawImage();
            } catch (LayersNotFoundException e){
                //System.out.println(e);
                return null;
            }
        }
    }
    public Pixmap drawImage() throws LayersNotFoundException{
        if (pixlayers!=null) {
            if (pixlayers.length>0) {
                image = pixlayers[0];
                for (int i = 1; i<pixlayers.length;i++){
                    image.drawPixmap(pixlayers[i],0,0);
                }
                return image;
            }
        }
        throw new LayersNotFoundException();
    }

    private class LayersNotFoundException extends Exception{

        public LayersNotFoundException() {
            super();
        }
        public LayersNotFoundException(String message) {
            super(message);
        }
    }
}
