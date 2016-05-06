package com.blm.hightide.events;

import com.blm.hightide.model.TickType;

public class FileLoadStart {

    private String symbol;

    private TickType tickType;

    public FileLoadStart(String symbol, TickType tickType) {
        this.symbol = symbol;
        this.tickType = tickType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TickType getTickType() {
        return tickType;
    }

    public void setTickType(TickType tickType) {
        this.tickType = tickType;
    }
}
