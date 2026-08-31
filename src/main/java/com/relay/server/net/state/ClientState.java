package com.relay.server.net.state;

import com.relay.protocol.Message;
import com.relay.server.net.ClientHandler;

public interface ClientState {
    public abstract void handleMessage(ClientHandler handler, Message message);
}
