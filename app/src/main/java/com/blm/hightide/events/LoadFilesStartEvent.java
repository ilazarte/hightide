package com.blm.hightide.events;

public class LoadFilesStartEvent {
    private int watchlistId;

    public LoadFilesStartEvent(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }
}
