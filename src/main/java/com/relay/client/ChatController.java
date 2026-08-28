package com.relay.client;

import com.relay.protocol.Message;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {

    /**
     * A connection to the server.
     * Allows sending/retrieving information to/from the server.
     */
    private ServerConnection svConnection;

    /**
     * The UI element representing the chat history.
     * Should sit on the center of the screen and
     * display the messages sent by all users.
    */
    @FXML
    private TextArea historyArea;

    /**
     * The UI element representing the user's chat input field.
     * This is where input is fed to the application that then
     * converts it into a message.
     */
    @FXML
    private TextField inputField;

    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
    }

    @FXML
    private void handleSend() {
        String text = inputField.getText();
        historyArea.appendText("YOU > " + text + "\n");
        inputField.clear();

        // NOTE: the javaFX thread is the one executing this operation.
        // Consider handing this task over to another thread if this one
        // somehow gets blocked for too long.
        svConnection.send(text);
    }

    @FXML
    public void updateChatHistory(Message message) {
        historyArea.appendText(message.getSender() + " > "
                             + message.getBody() + "\n");
    }
}