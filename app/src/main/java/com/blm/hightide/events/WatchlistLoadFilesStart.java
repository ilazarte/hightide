package com.blm.hightide.events;

import com.blm.hightide.model.StudyParams;

public class WatchlistLoadFilesStart {

    private int watchlistId;

    private StudyParams params;

    public WatchlistLoadFilesStart(int watchlistId, StudyParams params) {
        this.watchlistId = watchlistId;
        this.params = params;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public StudyParams getParams() {
        return params;
    }

    public void setParams(StudyParams params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "WatchlistLoadFilesStart{" +
                "watchlistId=" + watchlistId +
                ", params=" + params +
                '}';
    }
}
