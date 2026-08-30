package com.relay.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import com.relay.client.ServerConnection;

public class LoginController {
    
    /**
     * A connection to the server.
     * Allows sending/retrieving information to/from the server.
     */
    private ServerConnection svConnection;

    @FXML
    TextField usernameField;

    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
    }

    @FXML
    private void handleConfirm() {
        String username = usernameField.getText();
        if (username == null || username.isBlank()) return;
        svConnection.requestUsername(username);
    }
}
