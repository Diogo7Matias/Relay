package com.relay.protocol;

import java.time.Instant;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {
    private final MessageType type;
    private final UUID requestID;
    private final String body;
    private final String sender;
    private final Instant timestamp;
    private final String errorMessage;

    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    private Message(Builder builder) {
        this.type = builder.type;
        this.requestID = builder.requestID != null ? builder.requestID : UUID.randomUUID();
        this.body = builder.body;
        this.sender = builder.sender;
        this.timestamp = builder.timestamp;
        this.errorMessage = builder.errorMessage;
    }

    public static Builder builder(MessageType type) {
        return new Builder(type);
    }

    public static class Builder {
        private final MessageType type;
        private UUID requestID;
        private String body;
        private String sender;
        private Instant timestamp;
        private String errorMessage;

        private Builder(MessageType type) {
            this.type = type;
        }

        public Builder requestID(UUID requestID) {
            this.requestID = requestID;
            return this;
        }
        
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }

    public MessageType getType() {
        return this.type;
    }

    public UUID getRequestID() {
        return this.requestID;
    }
    
    public String getBody() {
        return this.body;
    }

    public String getSender() {
        return this.sender;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
