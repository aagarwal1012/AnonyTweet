package com.ayush.anonytweet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class getImageFromUrl{
    Bitmap bitmap;
    String url;

    public getImageFromUrl(Bitmap bitmap) {
    }

    public getImageFromUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap(){
        try{
            InputStream srt = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(srt);
        }
        catch (IOException e){
        }

        return bitmap;
    }
}
