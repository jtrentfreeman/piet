package com.entity;

public class Metadata {
    private String magicNumber;
    private Integer column;
    private Integer row;
    private Integer maxVal;

    public Metadata(String magicNumber, Integer column, Integer row, Integer maxVal) {
        this.magicNumber = magicNumber;
        this.column = column;
        this.row = row;
        this.maxVal = maxVal;
    }

    public Metadata() {

    }

    public String getMagicNumber() {
        return this.magicNumber;
    }

    public void setMagicNumber(String magicNumber) {
        this.magicNumber = magicNumber;
    }

    public Integer getColumn() {
        return this.column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getRow() {
        return this.row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getMaxVal() {
        return this.maxVal;
    }

    public void setMaxVaL(Integer maxVal) {
        this.maxVal = maxVal;
    }


}