package com.dylan.circleimageview;

import android.graphics.Bitmap;

/**
 * Description
 * author   Dylan.zhuang
 * Date:    16/7/7-下午5:03
 */
public class CircleImageViewBean {
    /**
     * 位图
     */
    private Bitmap bitmap;
    /**
     * 随机背景
     */
    private int randomBg;
    /**
     * 文本信息
     */
    private String text;

    public CircleImageViewBean(Bitmap bitmap, int randomBg, String text) {
        this.bitmap = bitmap;
        this.randomBg = randomBg;
        this.text = text;
    }

    public int getRandomBg() {
        return randomBg;
    }

    public void setRandomBg(int randomBg) {
        this.randomBg = randomBg;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
