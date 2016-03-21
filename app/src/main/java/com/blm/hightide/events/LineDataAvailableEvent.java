package com.blm.hightide.events;

import com.github.mikephil.charting.data.LineData;

public class LineDataAvailableEvent {

    private LineData lineData;

    public LineDataAvailableEvent(LineData watchlist) {
        this.lineData = watchlist;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
    }
}
