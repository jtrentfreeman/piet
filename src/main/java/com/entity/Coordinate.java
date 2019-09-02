package com.entity;

public class Coordinate {
    private Integer x;
    private Integer y;

    public Coordinate() {
        this.x = -1;
        this.y = -1;
    }

    public Coordinate(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getX() {
        return this.x;
    }

    public Integer getY() {
        return this.y;
    }

}