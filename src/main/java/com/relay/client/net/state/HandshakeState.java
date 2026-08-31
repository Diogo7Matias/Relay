package com.relay.client.net.state;

import com.relay.client.net.ServerConnection;
import com.relay.protocol.Message;

public class HandshakeState implements ConnectionState {
    @Override
    public void handleMessage(ServerConnection connection, Message message) {
        switch (message.getType()) {
            case ERROR -> connection.notifyErrorReceived(message);
            case ACK -> {
                connection.notifyUsernameChosen(message);
                connection.setState(new ConnectedState());
            }
            default -> System.err.println("Ignoring message, still in handshake: " + message.getType());
        }
    }
}
