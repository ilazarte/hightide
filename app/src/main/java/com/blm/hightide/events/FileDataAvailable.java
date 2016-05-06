package com.blm.hightide.events;

import com.blm.hightide.model.FileData;
import com.blm.hightide.model.StudyParams;
import com.blm.hightide.model.TickType;

public class FileDataAvailable {

    private String symbol;

    private FileData fileData;

    private TickType tickType;

    public FileDataAvailable(String symbol, FileData fileData, TickType tickType) {
        this.symbol = symbol;
        this.tickType = tickType;
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

    public TickType getTickType() {
        return tickType;
    }

    public void setTickType(TickType tickType) {
        this.tickType = tickType;
    }
}
