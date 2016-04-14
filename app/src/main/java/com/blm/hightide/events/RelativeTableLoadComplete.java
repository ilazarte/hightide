package com.blm.hightide.events;

import com.blm.hightide.model.Watchlist;

import java.util.List;

public class RelativeTableLoadComplete {

    private Watchlist watchlist;

    private List<Object> gridList;

    private int topN;

    public RelativeTableLoadComplete(Watchlist watchlist, List<Object> relativeTicks, int topN) {
        this.watchlist = watchlist;
        this.gridList = relativeTicks;
        this.topN = topN;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public List<Object> getGridList() {
        return gridList;
    }

    public void setGridList(List<Object> gridList) {
        this.gridList = gridList;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }
}
