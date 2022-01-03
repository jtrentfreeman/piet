package com.frejt.piet.exception;

public class ColorNotFoundException extends Exception {

    private static final long serialVersionUID = Integer.MIN_VALUE;

    public ColorNotFoundException(String message) {
        super(message);
    }
}