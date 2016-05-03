package com.blm.hightide.model;

import java.util.List;

public class RelativeGridRow {

    private String timestamp;

    private List<RelativeTick> ticks;

    public RelativeGridRow(String timestamp, List<RelativeTick> ticks) {
        this.timestamp = timestamp;
        this.ticks = ticks;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<RelativeTick> getTicks() {
        return ticks;
    }

    public void setTicks(List<RelativeTick> ticks) {
        this.ticks = ticks;
    }
}
