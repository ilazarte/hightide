package com.blm.hightide.model;

public class StudyParams {

    private int length = 80;

    private int avgLength = 20;

    private AggType aggType = AggType.DAY;

    public StudyParams(int length, int avgLength) {
        this.length = length;
        this.avgLength = avgLength;
    }

    public StudyParams(int length, int avgLength, AggType aggType) {
        this.length = length;
        this.avgLength = avgLength;
        this.aggType = aggType;
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

    public AggType getAggType() {
        return aggType;
    }

    public void setAggType(AggType aggType) {
        this.aggType = aggType;
    }

    @Override
    public String toString() {
        return "StudyParams{" +
                "length=" + length +
                ", avgLength=" + avgLength +
                ", aggType=" + aggType +
                '}';
    }
}
