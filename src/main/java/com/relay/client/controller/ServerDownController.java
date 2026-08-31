package com.relay.client.controller;

import com.relay.client.net.ServerConnection;

import javafx.fxml.FXML;

public class ServerDownController {
    
    /**
     * A connection to the server.
     * Allows sending/retrieving information to/from the server.
     */
    private ServerConnection svConnection;

    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
    }

    @FXML
    private void handleConnect() {
        if (svConnection.connect()) {
            Thread netThread = new Thread(svConnection);
            netThread.start();
        }
    }
}
