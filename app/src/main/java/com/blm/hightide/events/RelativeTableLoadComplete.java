package com.blm.hightide.events;

import com.blm.hightide.model.RelativeTick;
import com.blm.hightide.model.Watchlist;

import java.util.List;

public class RelativeTableLoadComplete {

    private Watchlist watchlist;

    private List<RelativeTick> relativeTicks;

    public RelativeTableLoadComplete(Watchlist watchlist, List<RelativeTick> relativeTicks) {
        this.watchlist = watchlist;
        this.relativeTicks = relativeTicks;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public List<RelativeTick> getRelativeTicks() {
        return relativeTicks;
    }

    public void setRelativeTicks(List<RelativeTick> relativeTicks) {
        this.relativeTicks = relativeTicks;
    }
}
