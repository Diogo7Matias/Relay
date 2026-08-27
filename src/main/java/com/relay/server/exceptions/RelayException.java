package com.relay.server.exceptions;

public class RelayException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public RelayException(ErrorMessage errorMessage) {
        super(errorMessage.label);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}