package com.relay.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;

public class Message {
    private final String sender;
    private final String body;
    private final Instant timestamp;
    private final MessageType type;
    private final String errorMessage;

    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    /**
     * This constructor is meant to be used by the server when
     * broadcasting a message to its clients.
     */
    public Message(String sender, String body, Instant timestamp) {
        this.sender = sender;
        this.body = body;
        this.timestamp = timestamp;
        this.type = MessageType.TEXT;
        this.errorMessage = null;
    }

    /**
     * This constructor's main purpose is letting a client create
     * a message of the type NAME_REQUEST since they need only
     * specify the body and type.
     */
    public Message(String body, MessageType type) {
        this.sender = null;
        this.body = body;
        this.timestamp = null;
        this.type = type;
        this.errorMessage = null;
    }

    /**
     * This constructor can be used by both a client and a server.
     * It is mostly used to construct messages of the type ACK
     * since that message type requires no other fields.
     */
    public Message(MessageType type) {
        this.sender = null;
        this.body = null;
        this.timestamp = null;
        this.type = type;
        this.errorMessage = null;
    }

    /**
     * This constructor's purpose is to create messages of the type ERROR
     * by specifying the errorMessage.
     */
    public Message(String errorMessage) {
        this.sender = null;
        this.body = null;
        this.timestamp = null;
        this.type = MessageType.ERROR;
        this.errorMessage = errorMessage;
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

    public MessageType getType() {
        return this.type;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
