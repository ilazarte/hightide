package com.blm.hightide.model;

import java.util.ArrayList;
import java.util.List;

public enum AggType {

    MONTH("Month", TickType.DAILY),
    WEEK("Week", TickType.DAILY),
    DAY("Day", TickType.DAILY),
    MIN30("30 Min", TickType.INTRADAY),
    MIN15("15 Min", TickType.INTRADAY),
    MIN5("5 Min", TickType.INTRADAY);

    /**
     * Label to use for ui
     */
    private String label;

    /**
     * TickType for caching and retrieval purposes
     */
    private TickType tickType;


    AggType(String label, TickType tickType) {
        this.label = label;
        this.tickType = tickType;
    }

    /**
     * Get all the labels from the aggtypes in order of .values()
     * @return the labels
     */
    public static List<String> labels() {
        List<String> labels = new ArrayList<>();
        AggType[] values = AggType.values();
        for (AggType aggType : values) {
            labels.add(aggType.getLabel());
        }
        return labels;
    }

    /**
     * Index of the agg type in the values.
     */
    public static int indexOf(AggType aggType) {
        AggType[] values = AggType.values();
        for (int i = 0; i < values.length; i++) {
            AggType value = values[i];
            if (aggType.equals(value)) {
                return i;
            }
        }
        return -1;
    }

    public String getLabel() {
        return label;
    }

    public TickType getTickType() {
        return tickType;
    }
}
