package com.relay.client.view;

import com.relay.client.model.ChatSummary;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class ChatCell extends ListCell<ChatSummary> {
    @Override
    protected void updateItem(ChatSummary chatSummary, boolean empty) {
        super.updateItem(chatSummary, empty);

        if (empty || chatSummary == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        Label chatNameLabel = new Label(chatSummary.getDisplayName());
        HBox container = new HBox(chatNameLabel);

        chatNameLabel.getStyleClass().add("chat-entry-name");
        container.getStyleClass().add("chat-entry-container");

        setGraphic(container);
    }
}
