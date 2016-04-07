package com.blm.hightide.events;

public class FileLoadStart {

    private String symbol;

    public FileLoadStart(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
