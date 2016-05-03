package com.blm.hightide.events;

import com.blm.hightide.model.StudyParams;
import com.blm.hightide.model.Watchlist;
import com.github.mikephil.charting.data.LineData;

/**
 * TODO https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/CombinedChartActivity.java
 * TODO split into relativechartdataavailable and securitychartdataavailable and create corresponding classes.
 */
public class RelativeChartDataAvailable {

    private Watchlist watchlist;

    private LineData lineData;

    private StudyParams params;

    public RelativeChartDataAvailable(Watchlist watchlist, LineData LineData, StudyParams params) {
        this.watchlist = watchlist;
        this.lineData = LineData;
        this.params = params;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public LineData getLineData() {
        return lineData;
    }

    public void setLineData(LineData LineData) {
        this.lineData = LineData;
    }

    public StudyParams getParams() {
        return params;
    }

    public void setParams(StudyParams params) {
        this.params = params;
    }
}
