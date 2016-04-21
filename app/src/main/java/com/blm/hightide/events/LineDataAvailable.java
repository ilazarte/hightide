package com.blm.hightide.events;

import com.blm.hightide.model.MovingAvgParams;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.github.mikephil.charting.data.LineData;

public class LineDataAvailable {

    private Watchlist watchlist;

    private Security security;

    private LineData lineData;

    private MovingAvgParams params;

    public LineDataAvailable(Security security, LineData lineData) {
        this.security = security;
        this.lineData = lineData;
    }

    public LineDataAvailable(Watchlist watchlist, LineData lineData, MovingAvgParams params) {
        this.watchlist = watchlist;
        this.lineData = lineData;
        this.params = params;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData lineData) {
        this.lineData = lineData;
    }

    public MovingAvgParams getParams() {
        return params;
    }

    public void setParams(MovingAvgParams params) {
        this.params = params;
    }
}
