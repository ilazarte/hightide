package com.blm.hightide.events;

import com.blm.hightide.model.StudyParams;

public class FileLoadStart {

    private String symbol;

    private StudyParams params;

    public FileLoadStart(String symbol, StudyParams params) {
        this.symbol = symbol;
        this.params = params;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public StudyParams getParams() {
        return params;
    }

    public void setParams(StudyParams params) {
        this.params = params;
    }
}
