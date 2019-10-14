package com.exception;

public class ContentTypeNotFoundException extends Exception {

    private static final long serialVersionUID = Integer.MIN_VALUE;

    public ContentTypeNotFoundException(String message) {
        super(message);
    }
}