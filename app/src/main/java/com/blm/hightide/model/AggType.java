package com.blm.hightide.model;

import java.util.ArrayList;
import java.util.List;

public enum AggType {

    MONTH("Month", TickType.DAILY, 1000 * 60 * 60 * 8),  // 8 hours
    WEEK("Week", TickType.DAILY, 1000 * 60 * 60 * 4), // 4 hours
    DAY("Day", TickType.DAILY, 1000 * 60 * 60), // 1 hour
    MIN30("30 Min", TickType.INTRADAY, 1000 * 60 * 30), // 30 minutes
    MIN15("15 Min", TickType.INTRADAY, 1000 * 60 * 15), // 15 minutes
    MIN5("5 Min", TickType.INTRADAY, 1000 * 60 * 5); // 5 minutes

    /**
     * Label to use for ui
     */
    private String label;

    /**
     * TickType for caching and retrieval purposes
     */
    private TickType tickType;

    /**
     * TTL suggestion for a cache file based on this file data.
     * May be ignored in the case of intraday
     */
    private long ttl;

    AggType(String label, TickType tickType, long ttl) {
        this.label = label;
        this.tickType = tickType;
        this.ttl = ttl;
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

    public long getTtl() {
        return ttl;
    }

}
