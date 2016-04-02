package com.blm.hightide.events;

public class SecurityLoadStart {

    private String symbol;

    public SecurityLoadStart(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
