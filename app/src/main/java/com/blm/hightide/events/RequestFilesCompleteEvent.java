package com.blm.hightide.events;

import com.blm.hightide.model.Watchlist;

public class RequestFilesCompleteEvent {

    private Watchlist watchlist;

    public RequestFilesCompleteEvent(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }
}
