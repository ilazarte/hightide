package com.blm.hightide.events;

import com.blm.hightide.model.Watchlist;

public class LoadFilesCompleteEvent {

    private LineDataAvailableEvent lineDataAvailableEvent;

    public LoadFilesCompleteEvent(LineDataAvailableEvent lineDataAvailableEvent) {
        this.lineDataAvailableEvent = lineDataAvailableEvent;
    }

    public LineDataAvailableEvent getLineDataAvailableEvent() {
        return lineDataAvailableEvent;
    }

    public void setLineDataAvailableEvent(LineDataAvailableEvent lineDataAvailableEvent) {
        this.lineDataAvailableEvent = lineDataAvailableEvent;
    }
}