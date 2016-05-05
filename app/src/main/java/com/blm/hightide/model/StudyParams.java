package com.blm.hightide.model;

public class StudyParams {

    private int length = 60;

    private int avgLength = 20;

    private TickType tickType = TickType.DAILY;

    public StudyParams(int length, int avgLength) {
        this.length = length;
        this.avgLength = avgLength;
    }

    public StudyParams(int length, int avgLength, TickType tickType) {
        this.length = length;
        this.avgLength = avgLength;
        this.tickType = tickType;
    }

    public StudyParams() {
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

    public TickType getTickType() {
        return tickType;
    }

    public void setTickType(TickType tickType) {
        this.tickType = tickType;
    }

    @Override
    public String toString() {
        return "StudyParams{" +
                "length=" + length +
                ", avgLength=" + avgLength +
                ", tickType=" + tickType +
                '}';
    }
}
