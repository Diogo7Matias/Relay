package com.relay.protocol;

/**
 * Represents a type of message.
 * 
 * @see NAME_REQUEST 
 * @see TEXT
 * @see ACK
 * @see ERROR
 * @see NEW_CHAT_REQUEST
 * @see CONNECT_TO_CHAT_REQUEST#JOIN_CHAT_REQUEST
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
    ERROR,

    /**
     * A type of message sent by a client requesting the creation 
     * of a new chat.
     */
    NEW_CHAT_REQUEST,

    /**
     * The response to a NEW_CHAT_REQUEST.
     * Should contain the ID of the new chat.
     */
    NEW_CHAT_RESPONSE,

    /**
     * A type of message representing a client request to join a
     * chat room.
     */
    JOIN_CHAT_REQUEST
}
