package com.blm.hightide.events;

import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.github.mikephil.charting.data.LineData;

public class LineDataAvailable {

    private Watchlist watchlist;

    private Security security;

    private LineData lineData;

    private int last;

    private int avgLen;

    public LineDataAvailable(Security security, LineData lineData) {
        this.security = security;
        this.lineData = lineData;
    }

    public LineDataAvailable(Watchlist watchlist, LineData lineData) {
        this.watchlist = watchlist;
        this.lineData = lineData;
    }

    public LineDataAvailable(Watchlist wl, LineData data, int last, int avgLen) {
        this(wl, data);
        this.last = last;
        this.avgLen = avgLen;
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

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getAvgLen() {
        return avgLen;
    }

    public void setAvgLen(int avgLen) {
        this.avgLen = avgLen;
    }
}
