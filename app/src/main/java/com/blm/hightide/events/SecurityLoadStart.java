package com.blm.hightide.events;

import com.blm.hightide.model.MovingAvgParams;

public class SecurityLoadStart {

    private String symbol;

    private MovingAvgParams params;

    public SecurityLoadStart(String symbol) {
        this.symbol = symbol;
    }

    public SecurityLoadStart(String symbol, MovingAvgParams params) {
        this.symbol = symbol;
        this.params = params;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public MovingAvgParams getParams() {
        return params;
    }

    public void setParams(MovingAvgParams params) {
        this.params = params;
    }
}
