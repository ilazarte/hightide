package com.blm.hightide.model;

import java.util.Comparator;

public class RelativeTick {

    private static class RelativeTickComparator implements Comparator<RelativeTick> {
        @Override
        public int compare(RelativeTick lhs, RelativeTick rhs) {
            return lhs.getValue().compareTo(rhs.getValue());
        }
    }

    public static final RelativeTickComparator COMPARATOR = new RelativeTickComparator();

    private String symbol;

    private Double value;

    private int color;

    public RelativeTick(String symbol, Double value, int color) {
        this.value = value;
        this.symbol = symbol;
        this.color = color;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
