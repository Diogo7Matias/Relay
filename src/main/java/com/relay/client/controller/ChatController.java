package com.relay.client.controller;

import com.relay.client.net.ServerConnection;
import com.relay.client.view.MessageCell;
import com.relay.protocol.Message;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
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
    private ListView<Message> historyList;

    /**
     * The UI element representing the user's chat input field.
     * This is where input is fed to the application that then
     * converts it into a message.
     */
    @FXML
    private TextField inputField;

    @FXML
    private void initialize() {
        historyList.setCellFactory(listView -> new MessageCell());
        historyList.getItems().addListener((ListChangeListener<Message>) change -> {
            Platform.runLater(() -> {
                historyList.scrollTo(historyList.getItems().size() - 1);
            });
        });
    }

    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
    }

    @FXML
    private void handleSend() {
        String text = inputField.getText();
        inputField.clear();

        if (text == null || text.isBlank()) return;

        // NOTE: the javaFX thread is the one executing this operation.
        // Consider handing this task over to another thread if this one
        // somehow gets blocked for too long.
        svConnection.send(text);
    }

    @FXML
    public void updateChatHistory(Message message) {
        historyList.getItems().add(message);
    }
}