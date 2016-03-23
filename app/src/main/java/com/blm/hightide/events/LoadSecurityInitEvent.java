package com.blm.hightide.events;

public class LoadSecurityInitEvent {

    private String symbol;

    public LoadSecurityInitEvent(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
