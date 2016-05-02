package com.blm.hightide.model;

public class StudyGridParams extends StudyParams {

    private int topLength = 6;

    public StudyGridParams(int length, int avgLength, int topLength) {
        super(length, avgLength);
        this.topLength = topLength;
    }

    public StudyGridParams() {
    }

    public StudyGridParams(int topLength) {
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
        return "StudyGridParams{" +
                "topLength=" + topLength +
                "} " + super.toString();
    }
}
