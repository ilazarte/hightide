package com.blm.hightide.events;

public class WatchlistLoadFilesStart {

    private int watchlistId;

    private int last;

    private int avgLen;

    public WatchlistLoadFilesStart(int watchlistId, int last, int avgLen) {
        this.watchlistId = watchlistId;
        this.last = last;
        this.avgLen = avgLen;
    }

    public int getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(int watchlistId) {
        this.watchlistId = watchlistId;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }

    public int getAvgLen() {
        return avgLen;
    }

    public void setAvgLen(int avgLen) {
        this.avgLen = avgLen;
    }
}
