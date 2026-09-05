package com.relay.server.textMessage.domain;

import java.time.Instant;
import java.util.UUID;

public class TextMessage {
    private final String body;
    private final UUID senderID;
    private final Instant timestamp;

    public TextMessage(String body, UUID senderID, Instant timestamp) {
        this.body = body;
        this.senderID = senderID;
        this.timestamp = timestamp;
    }

    public String getBody() {
        return this.body;
    }

    public UUID getSenderID() {
        return this.senderID;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
}
