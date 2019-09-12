package com.entity;

import com.google.gson.GsonBuilder;

/**
 * Piet code takes the form of graphics made up of the recognised colours. 
 * Individual pixels of colour are significant in the language, so it is common for programs to be enlarged for viewing so that details are 
 *   easily visible.
 * In such enlarged programs, the term "codel" is used to mean a block of colour equivalent to a single pixel of code. to avoid confusion 
 *   with the actual pixels of the enlarged graphic, of which many may make up one codel.
 */
public class Codel {
    private Integer x;
    private Integer y;

    public Codel() {
        this.x = -1;
        this.y = -1;
    }

    public Codel(Integer x, Integer y) {
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

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

}