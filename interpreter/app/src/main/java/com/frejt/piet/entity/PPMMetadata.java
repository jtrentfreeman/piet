package com.frejt.piet.entity;

/**
 * The PPM format is a lowest common denominator color image file format.
 * 
 */
public class PPMMetadata {

    /**
     * A "magic number" for identifying the file type. A ppm image's magic number is
     * the two characters "P6".
     */
    private String magicNumber;

    /**
     * A width, formatted as ASCII characters in decimal.
     */
    private Integer column;

    /**
     * A height, again in ASCII decimal.
     */
    private Integer row;

    /**
     * The maximum color value (Maxval), again in ASCII decimal. 
     * Must be less than 65536 and more than zero.
     */
    private Integer maxVal;

    public PPMMetadata(String magicNumber, Integer column, Integer row, Integer maxVal) {
        this.magicNumber = magicNumber;
        this.column = column;
        this.row = row;
        this.maxVal = maxVal;
    }

    public PPMMetadata() {

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