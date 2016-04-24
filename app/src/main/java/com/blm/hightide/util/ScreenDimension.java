package com.blm.hightide.util;

public class ScreenDimension {

    private float width;

    private float height;

    public ScreenDimension(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "ScreenDimension{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
