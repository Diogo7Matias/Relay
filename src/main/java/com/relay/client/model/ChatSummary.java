package com.relay.client.model;

public class ChatSummary {
    private final String displayName;
    // ...

    public ChatSummary(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
