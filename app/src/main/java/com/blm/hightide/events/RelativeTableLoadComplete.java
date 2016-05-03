package com.blm.hightide.events;

import com.blm.hightide.model.RelativeGridRow;
import com.blm.hightide.model.StudyGridParams;
import com.blm.hightide.model.Watchlist;

import java.util.List;

public class RelativeTableLoadComplete {

    private Watchlist watchlist;

    private List<RelativeGridRow> gridList;

    private StudyGridParams params;

    public RelativeTableLoadComplete(Watchlist watchlist, List<RelativeGridRow> gridList, StudyGridParams params) {
        this.watchlist = watchlist;
        this.gridList = gridList;
        this.params = params;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public List<RelativeGridRow> getGridList() {
        return gridList;
    }

    public void setGridList(List<RelativeGridRow> gridList) {
        this.gridList = gridList;
    }

    public StudyGridParams getParams() {
        return params;
    }

    public void setParams(StudyGridParams params) {
        this.params = params;
    }
}
