package com.blm.hightide.events;

import com.blm.hightide.model.Watchlist;

public class RequestFilesStartEvent {

    private Watchlist watchlist;

    public RequestFilesStartEvent(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }
}
