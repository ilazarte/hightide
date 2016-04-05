package com.blm.hightide.events;

public class WatchlistFilesRequestStart {

    private int watchlistId;

    private boolean readRequest;

    public WatchlistFilesRequestStart(int watchlistId, boolean readRequest) {
        this.watchlistId = watchlistId;
        this.readRequest = readRequest;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public boolean isReadRequest() {
        return readRequest;
    }

    public void setReadRequest(boolean readRequest) {
        this.readRequest = readRequest;
    }
}
