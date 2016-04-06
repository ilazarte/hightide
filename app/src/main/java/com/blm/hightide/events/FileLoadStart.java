package com.blm.hightide.events;

/**
 * Created by perico on 4/5/2016.
 */
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
