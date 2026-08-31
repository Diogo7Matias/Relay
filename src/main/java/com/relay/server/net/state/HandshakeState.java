package com.relay.server.net.state;

import com.relay.protocol.Message;
import static com.relay.server.exceptions.ErrorMessage.MESSAGE_BODY_MISSING;
import com.relay.server.exceptions.RelayException;
import com.relay.server.net.ClientHandler;

public class HandshakeState implements ClientState {

    @Override
    public void handleMessage(ClientHandler handler, Message message) {
        switch (message.getType()) {
            case NAME_REQUEST -> {
                if (message.getBody() == null || message.getBody().isBlank()) {
                    throw new RelayException(MESSAGE_BODY_MISSING);
                }
                handler.processUsernameRequest(message.getBody());
                handler.setState(new ConnectedState());
            }
            default -> System.err.println("Unhandled message type: " + message.getType());
        }
    }
}
