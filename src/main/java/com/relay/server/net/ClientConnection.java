package com.relay.server.net;

import com.relay.protocol.Message;

/**
 * Represents a connection to a client.
 * The connection allows sending messages to the client.
 */
public interface ClientConnection {
    void sendMessage(Message message);
}
