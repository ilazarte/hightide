package com.blm.hightide.events;

import com.blm.hightide.model.Watchlist;

import java.util.List;

public class WatchlistFilesRequestComplete {

    private List<Watchlist> watchlists;

    private Watchlist watchlist;

    public WatchlistFilesRequestComplete(List<Watchlist> watchlists, Watchlist watchlist) {
        this.watchlists = watchlists;
        this.watchlist = watchlist;
    }

    public List<Watchlist> getWatchlists() {
        return watchlists;
    }

    public void setWatchlists(List<Watchlist> watchlists) {
        this.watchlists = watchlists;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }
}
