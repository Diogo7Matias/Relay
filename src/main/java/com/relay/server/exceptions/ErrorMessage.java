package com.relay.server.exceptions;

public enum ErrorMessage {
    USER_NAME_INVALID("User name is invalid."),
    MESSAGE_FORMAT_INVALID("Invalid message format."),
    MESSAGE_BODY_MISSING("Message body field is empty or missing.")
    ;

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}
