package com.blm.hightide.model;

public class MovingAvgParams {

    private int length = 60;

    private int avgLength = 20;

    public MovingAvgParams(int length, int avgLength) {
        this.length = length;
        this.avgLength = avgLength;
    }

    public MovingAvgParams() {
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getAvgLength() {
        return avgLength;
    }

    public void setAvgLength(int avgLength) {
        this.avgLength = avgLength;
    }

    @Override
    public String toString() {
        return "MovingAvgParams{" +
                "length=" + length +
                ", avgLength=" + avgLength +
                '}';
    }
}
