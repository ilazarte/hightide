package com.blm.hightide.events;

import com.blm.hightide.model.MovingAvgParams;

public class WatchlistLoadFilesStart {

    private int watchlistId;

    private MovingAvgParams params;

    public WatchlistLoadFilesStart(int watchlistId, MovingAvgParams params) {
        this.watchlistId = watchlistId;
        this.params = params;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public MovingAvgParams getParams() {
        return params;
    }

    public void setParams(MovingAvgParams params) {
        this.params = params;
    }
}
