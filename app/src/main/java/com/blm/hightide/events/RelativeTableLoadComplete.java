package com.blm.hightide.events;

import com.blm.hightide.model.StudyGridParams;
import com.blm.hightide.model.Watchlist;

import java.util.List;

public class RelativeTableLoadComplete {

    private Watchlist watchlist;

    private List<Object> gridList;

    private StudyGridParams params;

    public RelativeTableLoadComplete(Watchlist watchlist, List<Object> relativeTicks, StudyGridParams params) {
        this.watchlist = watchlist;
        this.gridList = relativeTicks;
        this.params = params;
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

    public StudyGridParams getParams() {
        return params;
    }

    public void setParams(StudyGridParams params) {
        this.params = params;
    }
}
