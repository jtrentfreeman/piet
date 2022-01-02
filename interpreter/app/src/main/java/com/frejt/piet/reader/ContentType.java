package com.frejt.piet.reader;

import com.frejt.piet.exception.ContentTypeNotFoundException;

/**
 * Content types that are supported by the interpreter.
 */
public enum ContentType {

    PPM("image/x-portable-pixmap"), 
    PNG("image/png");

    String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public static ContentType getContentType(String contentType) throws ContentTypeNotFoundException {
        for (ContentType fileType : ContentType.values()) {
            if(contentType.equals(fileType.getContentType())) {
                return fileType;
            }
        }
        throw new ContentTypeNotFoundException("File type " + contentType + " is not currently supported!");
    }

}