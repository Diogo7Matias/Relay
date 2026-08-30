package com.relay.protocol;

/**
 * Represents a type of message.
 * 
 * @see NAME_REQUEST 
 * @see TEXT
 * @see ACK
 * @see ERROR
 */
public enum MessageType {

    /**
     * A type of message sent by a client requesting a username.
     */
    NAME_REQUEST,

    /**
     * Represents a text message.
     * Can be sent by a client to the server or by the server to 
     * any client (when a broadcast happens).
     */
    TEXT,

    /**
     * A type of control message representing an acknowledgement
     * of something related to a previous message.
     */
    ACK,

    /**
     * Represents an error message.
     */
    ERROR
}
