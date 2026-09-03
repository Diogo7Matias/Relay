package com.relay.client.model;

public class ChatSummary {
    private final String chatID;
    // ...

    public ChatSummary(String chatID) {
        this.chatID = chatID;
    }

    public String getChatID() {
        return this.chatID;
    }
}
