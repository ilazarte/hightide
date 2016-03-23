package com.blm.hightide.events;

public class LoadFilesInitEvent {

    private int watchlistId;

    public LoadFilesInitEvent(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }
}
