package com.relay.protocol;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import com.google.gson.JsonParseException;
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
        out.name("type").value(value.getType().toString());
        out.name("requestID").value(value.getRequestID().toString());
        out.name("body").value(value.getBody());
        out.name("sender").value(value.getSender());
        out.name("timestamp").value(timestampValue);
        out.endObject();
    }

    @Override
    public Message read(JsonReader in) throws IOException {
        MessageType type = null;
        UUID requestID = null;
        String body = null;
        String sender = null;
        Instant timestamp = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "type" -> type = MessageType.valueOf(in.nextString().toUpperCase());
                case "requestID" -> requestID = UUID.fromString(in.nextString());
                case "body" -> body = in.nextString();
                case "sender" -> sender = in.nextString();
                case "timestamp" -> timestamp = Instant.parse(in.nextString());
                default -> in.skipValue(); // ignore unknown fields
            }
        }
        in.endObject();

        if (type == null) {
            throw new JsonParseException("Message JSON missing required \"type\" field");
        }

        return Message.builder(type)
            .requestID(requestID)
            .body(body)
            .sender(sender)
            .timestamp(timestamp)
            .build();
    }
}