package com.blm.hightide.model;

import com.blm.corals.ReadError;

import java.util.ArrayList;
import java.util.List;

public class FileLine {

    private int num;

    private String line;

    private List<ReadError> errors;

    public FileLine() {
    }

    public FileLine(int num, String line, List<ReadError> errors) {
        this.num = num;
        this.line = line;
        this.errors = errors;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public List<ReadError> getErrors() {
        return errors;
    }

    public void setErrors(List<ReadError> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "FileLine{" +
                "line='" + line + '\'' +
                ", num=" + num +
                '}';
    }

    public void add(ReadError re) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        errors.add(re);
    }
}
