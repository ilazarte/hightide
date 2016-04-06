package com.blm.hightide.events;

import com.blm.hightide.model.FileData;

public class FileDataAvailable {

    private String symbol;

    private FileData fileData;

    public FileDataAvailable() {
    }

    public FileDataAvailable(String symbol, FileData fileData) {
        this.symbol = symbol;
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
}
