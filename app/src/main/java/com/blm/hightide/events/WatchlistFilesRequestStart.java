package com.blm.hightide.events;

import com.blm.hightide.model.StudyParams;

public class WatchlistFilesRequestStart {

    private int watchlistId;

    private StudyParams params;

    private boolean readRequest;

    public WatchlistFilesRequestStart(int watchlistId, StudyParams params, boolean readRequest) {
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

    public StudyParams getParams() {
        return params;
    }

    public void setParams(StudyParams params) {
        this.params = params;
    }

    public boolean isReadRequest() {
        return readRequest;
    }

    public void setReadRequest(boolean readRequest) {
        this.readRequest = readRequest;
    }
}
