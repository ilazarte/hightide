package com.blm.hightide.model;

import java.util.List;

/**
 * Created by perico on 4/5/2016.
 */
public class FileData {

    private String name;

    private List<FileLine> lines;

    public FileData() {
    }

    public FileData(String name, List<FileLine> lines) {
        this.name = name;
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FileLine> getLines() {
        return lines;
    }

    public void setLines(List<FileLine> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "FileData{" +
                "name='" + name + '\'' +
                '}';
    }
}
