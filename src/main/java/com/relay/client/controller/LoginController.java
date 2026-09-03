package com.relay.client.controller;

import com.relay.client.net.ServerConnection;
import com.relay.client.util.Callbacks;
import com.relay.protocol.Message;
import com.relay.protocol.MessageType;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController implements ViewController {
    
    /**
     * A connection to the server.
     * Allows sending/retrieving information to/from the server.
     */
    private ServerConnection svConnection;

    private Node currentErrorNode;

    private Runnable onLoginSuccess;

    @FXML
    private VBox page;

    @FXML
    private TextField usernameField;

    @Override
    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
    }

    public void setOnLoginSuccess(Runnable handler) {
        this.onLoginSuccess = handler;
    }

    @FXML
    private void handleConfirm() {
        String username = usernameField.getText();
        if (username == null || username.isBlank()) return;
        
        Message request = Message.builder(MessageType.NAME_REQUEST).body(username).build();
        svConnection.sendRequest(request, response -> Platform.runLater(() -> {
            switch (response.getType()) {
                case ACK -> {
                    if (Callbacks.notify(onLoginSuccess, "onLoginSuccess")) {
                        svConnection.setUsername(request.getBody());
                    }
                }
                case ERROR -> displayErrorMessage(response);
                default -> System.err.println("Ignoring message, still in handshake: " + response.getType());
            }
        }));
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
