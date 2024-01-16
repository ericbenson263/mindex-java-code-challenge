package com.mindex.challenge.data;

public enum ErrorMessages {
    NOT_NULL("This function is not expecting a null value."),
    METHOD_NOT_ALLOWED("405 METHOD_NOT_ALLOWED");
    //TODO add more error types

    private final String value;

    ErrorMessages(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

