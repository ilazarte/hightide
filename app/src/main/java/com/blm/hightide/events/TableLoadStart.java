package com.blm.hightide.events;

import com.blm.corals.Tick;
import com.blm.hightide.model.StudyParams;
import com.blm.hightide.model.TickType;

public class TableLoadStart {

    private String symbol;

    private TickType tickType;

    public TableLoadStart(String symbol, TickType tickType) {
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
