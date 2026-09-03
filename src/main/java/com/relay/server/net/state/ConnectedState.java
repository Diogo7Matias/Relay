package com.relay.server.net.state;

import com.relay.protocol.Message;
import static com.relay.server.exceptions.ErrorMessage.MESSAGE_BODY_MISSING;
import com.relay.server.exceptions.RelayException;
import com.relay.server.net.ClientHandler;

public class ConnectedState implements ClientState {
    
    @Override
    public void handleMessage(ClientHandler handler, Message message) {
        switch (message.getType()) {
            case NEW_CHAT_REQUEST -> {
                if (message.getBody() == null || message.getBody().isBlank()) {
                    throw new RelayException(MESSAGE_BODY_MISSING);
                }
                handler.processNewChatRequest(message.getRequestID(), message.getBody());
            }
            case JOIN_CHAT_REQUEST -> {
                if (message.getBody() == null || message.getBody().isBlank()) {
                    throw new RelayException(MESSAGE_BODY_MISSING);
                }
                handler.processJoinChatRoom(message.getRequestID(), message.getBody());
                handler.setState(new ChatRoomState());
            }
            default -> System.err.println("Unhandled message type: " + message.getType());
        }
    }
}
