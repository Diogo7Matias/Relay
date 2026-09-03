package com.relay.protocol;

/**
 * Represents a type of message.
 * 
 * @see TEXT
 * @see ACK
 * @see ERROR
 * @see NAME_REQUEST 
 * @see NEW_CHAT_REQUEST
 * @see JOIN_CHAT_REQUEST
 * @see CHAT_CREATED
 */
public enum MessageType {

    /**
     * Represents a text message.
     * Can be sent by a client to the server or by the server to 
     * any client (when a broadcast happens).
     */
    TEXT,

    /**
     * A type of control message representing an acknowledgement
     * of something related to a previous message.
     * ACK messages may carry useful information through the body field.
     */
    ACK,

    /**
     * Represents an error message.
     * This type of message shall use the errorMessage field.
     */
    ERROR,

    /**
     * A type of message sent by a client requesting a username.
     */
    NAME_REQUEST,

    /**
     * A type of message sent by a client requesting the creation 
     * of a new chat room.
     */
    NEW_CHAT_REQUEST,

    /**
     * A type of message representing a client request to join a
     * chat room.
     */
    JOIN_CHAT_REQUEST,

    /**
     * A type of message sent by the server to inform a client that
     * a chat room involving them was created.
     */
    CHAT_CREATED
}
