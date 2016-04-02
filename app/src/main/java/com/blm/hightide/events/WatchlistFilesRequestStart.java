package com.blm.hightide.events;

public class WatchlistFilesRequestStart {

    private int watchlistId;

    public WatchlistFilesRequestStart(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }
}
