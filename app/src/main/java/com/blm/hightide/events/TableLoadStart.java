package com.blm.hightide.events;

import com.blm.hightide.model.AggType;

public class TableLoadStart {

    private String symbol;

    private AggType aggType;

    public TableLoadStart(String symbol, AggType aggType) {
        this.symbol = symbol;
        this.aggType = aggType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public AggType getAggType() {
        return aggType;
    }

    public void setAggType(AggType aggType) {
        this.aggType = aggType;
    }
}
