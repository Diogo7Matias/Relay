package com.relay.protocol;

import java.time.Instant;

public class Message {
    private final String sender;
    private final String body;
    private final Instant timestamp;

    public Message(String sender, String body, Instant timestamp) {
        this.sender = sender;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return this.sender;
    }

    public String getBody() {
        return this.body;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return this.timestamp + " | " 
            + "[" + this.sender + "]" + " > "
            + this.body;
    }
}
