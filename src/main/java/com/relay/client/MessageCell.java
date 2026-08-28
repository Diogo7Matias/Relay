package com.relay.client;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.relay.protocol.Message;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

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
        Label header = new Label(message.getSender() + " | " + formattedTime);
        Label body = new Label(message.getBody());

        VBox container = new VBox(header, body);
        setGraphic(container);
    }
}