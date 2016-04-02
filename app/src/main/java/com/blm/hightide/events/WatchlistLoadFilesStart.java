package com.blm.hightide.events;

public class WatchlistLoadFilesStart {

    private int watchlistId;

    public WatchlistLoadFilesStart(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }
}
