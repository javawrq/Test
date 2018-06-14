package com.xhwl.xhwlownerapp.Entity.UserEntity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/17.
 */

public class LoadImage implements Serializable{
    private String fileName;
    private Bitmap bitmap;

    public LoadImage() {
        super();
        // TODO Auto-generated constructor stub
    }
    public LoadImage(String fileName, Bitmap bitmap) {
        super();
        this.fileName = fileName;
        this.bitmap = bitmap;
    }
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * @return the bitmap
     */
    public Bitmap getBitmap() {
        return bitmap;
    }
    /**
     * @param bitmap the bitmap to set
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    @Override
    public int hashCode() {
        return this.getFileName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        LoadImage loadImg = (LoadImage)o;
        return this.getFileName().equals(loadImg.getFileName());
    }
}
