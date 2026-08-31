package com.relay.client.net.state;

import com.relay.client.net.ServerConnection;
import com.relay.protocol.Message;

public class ConnectedState implements ConnectionState {
    @Override
    public void handleMessage(ServerConnection connection, Message message) {
        switch (message.getType()) {
            case TEXT -> connection.notifyMessageReceived(message);
            case ERROR -> connection.notifyErrorReceived(message);
            default -> System.err.println("Unhandled message type: " + message.getType());
        }
    }
}
