package com.blm.hightide.events;

import com.blm.hightide.model.MovingAvgGridParams;

public class RelativeTableLoadStart {

    private int watchlistId;

    private MovingAvgGridParams params;

    private boolean readRequest;

    public RelativeTableLoadStart(int watchlistId, MovingAvgGridParams params, boolean readRequest) {
        this.watchlistId = watchlistId;
        this.params = params;
        this.readRequest = readRequest;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public MovingAvgGridParams getParams() {
        return params;
    }

    public void setParams(MovingAvgGridParams params) {
        this.params = params;
    }

    public boolean isReadRequest() {
        return readRequest;
    }

    public void setReadRequest(boolean readRequest) {
        this.readRequest = readRequest;
    }
}
