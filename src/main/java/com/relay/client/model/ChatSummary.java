package com.relay.client.model;

public class ChatSummary {
    private final String chatID;
    private String displayName;

    public ChatSummary(String chatID, String displayName) {
        this.chatID = chatID;
        this.displayName = displayName;
    }

    public String getChatID() {
        return this.chatID;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
