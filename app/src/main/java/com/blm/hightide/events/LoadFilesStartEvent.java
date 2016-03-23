package com.blm.hightide.events;

import com.blm.hightide.model.Watchlist;

public class LoadFilesStartEvent {
    private Watchlist watchlist;

    public LoadFilesStartEvent(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }
}
