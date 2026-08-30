package com.relay.client.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import com.relay.client.ServerConnection;
import com.relay.protocol.Message;

public class LoginController {
    
    /**
     * A connection to the server.
     * Allows sending/retrieving information to/from the server.
     */
    private ServerConnection svConnection;

    private Node currentErrorNode;

    @FXML
    private VBox page;

    @FXML
    private TextField usernameField;

    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
    }

    @FXML
    private void handleConfirm() {
        String username = usernameField.getText();
        if (username == null || username.isBlank()) return;
        svConnection.requestUsername(username);
    }

    public void displayErrorMessage(Message message) {
        String errorStr = message.getErrorMessage();
        if (currentErrorNode != null) {
            this.page.getChildren().remove(currentErrorNode);
        }
        currentErrorNode = buildErrorMessage(errorStr);
        this.page.getChildren().addFirst(currentErrorNode);
    }

    private Label buildErrorMessage(String errorMessage) {
        Label errorLabel = new Label("ERROR: " + errorMessage);
        errorLabel.getStyleClass().add("error-message");
        return errorLabel;
    }
}
