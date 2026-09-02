package com.relay.client.controller;

import com.relay.client.net.ServerConnection;

public interface ViewController {
    public abstract void setServerConnection(ServerConnection connection);
}
