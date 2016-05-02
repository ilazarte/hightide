package com.blm.hightide.events;

import com.blm.hightide.model.StudyGridParams;

public class RelativeTableLoadStart {

    private int watchlistId;

    private StudyGridParams params;

    private boolean readRequest;

    public RelativeTableLoadStart(int watchlistId, StudyGridParams params, boolean readRequest) {
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

    public StudyGridParams getParams() {
        return params;
    }

    public void setParams(StudyGridParams params) {
        this.params = params;
    }

    public boolean isReadRequest() {
        return readRequest;
    }

    public void setReadRequest(boolean readRequest) {
        this.readRequest = readRequest;
    }
}
