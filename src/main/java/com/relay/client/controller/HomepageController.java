package com.relay.client.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import com.relay.client.model.ChatSummary;
import com.relay.client.net.ServerConnection;
import com.relay.client.util.Callbacks;
import com.relay.client.view.ChatCell;
import com.relay.protocol.Message;
import com.relay.protocol.MessageType;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;

public class HomepageController implements ViewController {

    private ServerConnection svConnection;

    private Consumer<String> onNewChat;
    private Consumer<String> onJoinChat;

    @FXML
    private BorderPane borderPane;

    @FXML
    private Label username;

    @FXML
    private ListView<ChatSummary> chatsList;

    @Override
    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
    }

    public void setOnNewChat(Consumer<String> handler) {
        this.onNewChat = handler;
    }

    public void setOnJoinChat(Consumer<String> handler) {
        this.onJoinChat = handler;
    }

    public void setUsernameLabel() {
        this.username.setText(svConnection.getUsername());
    }

    @FXML
    private void initialize() {
        chatsList.setCellFactory(listView -> new ChatCell());
        chatsList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newSelection) -> {
            if (newSelection != null) {
                joinChat(newSelection);
            }
        });
    }

    @FXML
    private void showPrompt() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Chat");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Chat Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(chatName -> newChat(chatName));
    }

    private void newChat(String otherUser) {
        Message request = Message.builder(MessageType.NEW_CHAT_REQUEST)
            .body(otherUser)
            .build();

        svConnection.sendRequest(request, response -> Platform.runLater(() -> {
            switch (response.getType()) {
                case ACK -> Callbacks.notify(onNewChat, response.getBody(), "onNewChat");
                case ERROR -> System.err.println(response.getBody());
                default -> System.err.println("Unhandled message type: " + response.getType());
            }
        }));
    }

    private void joinChat(ChatSummary chatSummary) {
        String chatID = chatSummary.getChatID();
        Message request = Message.builder(MessageType.JOIN_CHAT_REQUEST)
            .body(chatID)
            .build();

        svConnection.sendRequest(request, response -> Platform.runLater(() -> {
            switch (response.getType()) {
                case ACK -> Callbacks.notify(onJoinChat, response.getBody(), "onJoinChat");
                case ERROR -> System.err.println(response.getBody());
                default -> System.err.println("Unhandled message type: " + response.getType());
            }
        }));
    }

    public void updateChatsList(ChatSummary chatSummary) {
        chatsList.getItems().addFirst(chatSummary);
    }

    public void openChatSection(String chatID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/relay/client/chat.fxml"));
            Node chatView = loader.load();
            ChatController controller = loader.getController();

            controller.setServerConnection(svConnection);
            svConnection.setOnMessageReceived(msg -> Platform.runLater(() -> controller.updateChatHistory(msg)));
            svConnection.loadChatHistory(chatID);

            borderPane.setCenter(chatView);
        } catch (IOException e) {
            System.err.println("Failed to load chat view.\n" + e.getMessage());
        }
    }
}
