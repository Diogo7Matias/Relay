package com.relay.client.controller;

import java.io.IOException;
import java.util.Optional;

import com.relay.client.model.ChatSummary;
import com.relay.client.net.ServerConnection;
import com.relay.client.view.ChatCell;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;

public class HomepageController implements ViewController {

    private ServerConnection svConnection;

    @FXML
    private BorderPane borderPane;

    @FXML
    private ListView<ChatSummary> chatsList;

    @Override
    public void setServerConnection(ServerConnection connection) {
        this.svConnection = connection;
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
        result.ifPresent(chatName -> {
            newChat(chatName);
        });
    }

    private void newChat(String chatName) {
        svConnection.createNewChat(chatName);
    }

    private void joinChat(ChatSummary chatSummary) {
        svConnection.joinChat(chatSummary.getDisplayName());
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
            svConnection.setOnMessageReceived(
                msg -> Platform.runLater(
                    () -> controller.updateChatHistory(msg)
                )
            );
            svConnection.loadChatHistory(chatID);

            borderPane.setCenter(chatView);
        } catch (IOException e) {
            System.err.println("Failed to load chat view.\n" + e.getMessage());
        }
    }
}
