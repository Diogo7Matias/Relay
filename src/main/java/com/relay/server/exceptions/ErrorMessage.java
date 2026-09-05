package com.relay.server.exceptions;

public enum ErrorMessage {
    USERNAME_INVALID("Username is invalid."),
    USERNAME_ALREADY_EXISTS("Username already exists."),
    USER_NOT_FOUND("User not found."),
    MESSAGE_FORMAT_INVALID("Invalid message format."),
    MESSAGE_BODY_MISSING("Message body field is empty or missing."),
    CHAT_ROOM_NOT_FOUND("Chat room not found.")
    ;

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}
