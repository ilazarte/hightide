package com.blm.hightide.events;

import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.github.mikephil.charting.data.LineData;

public class LineDataAvailable {

    private Watchlist watchlist;

    private Security security;

    private LineData lineData;

    public LineDataAvailable(Security security, LineData lineData) {
        this.security = security;
        this.lineData = lineData;
    }

    public LineDataAvailable(Watchlist watchlist, LineData lineData) {
        this.watchlist = watchlist;
        this.lineData = lineData;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public Security getSecurity() {
        return security;
    }

    public LineData getLineData() {
        return lineData;
    }
}
