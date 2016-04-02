package com.blm.hightide.events;

import com.github.mikephil.charting.data.LineData;

public class LineDataAvailable {

    private LineData lineData;

    public LineDataAvailable(LineData watchlist) {
        this.lineData = watchlist;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
    }
}
