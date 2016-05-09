package com.blm.hightide.events;

import com.blm.hightide.model.AggType;
import com.blm.hightide.model.FileData;

public class FileDataAvailable {

    private String symbol;

    private FileData fileData;

    private AggType aggType;

    public FileDataAvailable(String symbol, FileData fileData, AggType tickType) {
        this.symbol = symbol;
        this.aggType = tickType;
        this.fileData = fileData;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public FileData getFileData() {
        return fileData;
    }

    public void setFileData(FileData fileData) {
        this.fileData = fileData;
    }

    public AggType getAggType() {
        return aggType;
    }

    public void setAggType(AggType aggType) {
        this.aggType = aggType;
    }
}
