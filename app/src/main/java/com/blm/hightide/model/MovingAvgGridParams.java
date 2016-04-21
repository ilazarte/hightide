package com.blm.hightide.model;

public class MovingAvgGridParams extends MovingAvgParams {

    private int topLength = 6;

    public MovingAvgGridParams(int length, int avgLength, int topLength) {
        super(length, avgLength);
        this.topLength = topLength;
    }

    public MovingAvgGridParams() {
    }

    public MovingAvgGridParams(int topLength) {
        this.topLength = topLength;
    }

    public int getTopLength() {
        return topLength;
    }

    public void setTopLength(int topLength) {
        this.topLength = topLength;
    }

    @Override
    public String toString() {
        return "MovingAvgGridParams{" +
                "topLength=" + topLength +
                "} " + super.toString();
    }
}
