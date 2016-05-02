package com.blm.hightide.events;

import com.blm.hightide.model.StudyParams;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.github.mikephil.charting.data.LineData;

public class LineDataAvailable {

    private Watchlist watchlist;

    private Security security;

    private LineData lineData;

    private StudyParams params;

    public LineDataAvailable(Security security, LineData lineData) {
        this.security = security;
        this.lineData = lineData;
    }

    public LineDataAvailable(Watchlist watchlist, LineData lineData, StudyParams params) {
        this.watchlist = watchlist;
        this.lineData = lineData;
        this.params = params;
    }

    public LineDataAvailable(Security security, LineData data, StudyParams params) {
        this.security = security;
        this.lineData = data;
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

    public StudyParams getParams() {
        return params;
    }

    public void setParams(StudyParams params) {
        this.params = params;
    }
}
