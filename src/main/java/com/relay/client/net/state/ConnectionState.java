package com.relay.client.net.state;

import com.relay.client.net.ServerConnection;
import com.relay.protocol.Message;

public interface ConnectionState {
    public abstract void handleMessage(ServerConnection connection, Message message);
}
