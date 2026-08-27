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

        out.beginObject();
        out.name("sender").value(value.getSender());
        out.name("body").value(value.getBody());
        out.name("timestamp").value(value.getTimestamp().toString());
        out.endObject();
    }

    @Override
    public Message read(JsonReader in) throws IOException {
        String sender = null;
        String body = null;
        Instant timestamp = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "sender" -> sender = in.nextString();
                case "body" -> body = in.nextString();
                case "timestamp" -> timestamp = Instant.parse(in.nextString());
                default -> in.skipValue(); // ignore unknown fields
            }
        }
        in.endObject();

        return new Message(sender, body, timestamp);
    }
}