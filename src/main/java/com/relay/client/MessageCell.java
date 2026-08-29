package com.relay.client;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.relay.protocol.Message;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class MessageCell extends ListCell<Message> {
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = formatter.format(message.getTimestamp().atZone(ZoneId.systemDefault()));
        Label usernameLabel = new Label(message.getSender());
        Label timestampLabel = new Label(formattedTime);

        HBox header = new HBox(usernameLabel, timestampLabel);
        Label body = new Label(message.getBody());
        VBox container = new VBox(header, body);
        
        body.setWrapText(true);
        container.prefWidthProperty().bind(this.widthProperty().subtract(20));
        
        header.getStyleClass().add("message-header");
        usernameLabel.getStyleClass().add("message-header-username");
        timestampLabel.getStyleClass().add("message-header-timestamp");
        body.getStyleClass().add("message-body");
        container.getStyleClass().add("message-container");

        setGraphic(container);
    }
}