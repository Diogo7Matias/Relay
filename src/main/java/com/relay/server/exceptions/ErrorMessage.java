package com.relay.server.exceptions;

public enum ErrorMessage {
    USER_NAME_INVALID("User name is invalid.")
    ;

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}
