package com.relay.protocol;

import java.io.IOException;
import java.time.Instant;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class MessageAdapter extends TypeAdapter<Message> {

    @Override
    public void write(JsonWriter out, Message value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        
        Instant timestamp = value.getTimestamp();
        String timestampValue = timestamp != null ? timestamp.toString() : null;

        out.beginObject();
        out.name("sender").value(value.getSender());
        out.name("body").value(value.getBody());
        out.name("timestamp").value(timestampValue);
        out.name("type").value(value.getType().toString());
        out.name("errorMessage").value(value.getErrorMessage());
        out.endObject();
    }

    @Override
    public Message read(JsonReader in) throws IOException {
        String sender = null;
        String body = null;
        Instant timestamp = null;
        MessageType type = null;
        String errorMessage = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "sender" -> sender = in.nextString();
                case "body" -> body = in.nextString();
                case "timestamp" -> timestamp = Instant.parse(in.nextString());
                case "type" -> type = MessageType.valueOf(in.nextString().toUpperCase());
                case "errorMessage" -> errorMessage = in.nextString();
                default -> in.skipValue(); // ignore unknown fields
            }
        }
        in.endObject();

        switch (type) {
            case MessageType.TEXT:
                return new Message(sender, body, timestamp);
            case MessageType.NAME_REQUEST:
                return new Message(body, type);
            case MessageType.ACK:
                return new Message(type);   
            case MessageType.ERROR:
                return new Message(errorMessage);             
            default:
                System.err.println("MessageAdapter failed to parse message.");
                return new Message(type);
        }
    }
}