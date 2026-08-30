package com.relay.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relay.protocol.Message;
import com.relay.protocol.MessageType;
import com.relay.protocol.MessageAdapter;

public class ClientHandler implements Runnable {
    private final ChatRoom room;
    private final Socket socket;
    private final String username;

    private PrintWriter out;

    /**
     * An instance of Gson, used to serialize/deserialize messages.
     */
    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    public ClientHandler(Socket socket, ChatRoom room, String username) {
        this.room = room;
        this.socket = socket;
        this.username = username;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        ) {
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            String jsonLine;

            // readLine returns null when the client closes the connection
            while ((jsonLine = in.readLine()) != null) {
                System.out.println("Received: " + jsonLine);
                Message message = gson.fromJson(jsonLine, Message.class);
                handleMessage(message);
            }
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            if (out != null) out.close();
        }
        System.out.println("Client disconnected.");
    }

    /**
     * Sends a message to the client associated with this handler.
     * 
     * The message is converted to a string formatted as a JSON object
     * and sent over to the client.
     * 
     * @param message the message to be sent
     */
    public void sendMessage(Message message) {
        String json = gson.toJson(message);
        this.out.println(json);
    }

    public String getUsername() {
        return this.username;
    }

    private void handleMessage(Message message) {
        MessageType type = message.getType();
        if (type == null) {
            // TODO
        }

        switch (type) {
            case MessageType.TEXT:
                String body = message.getBody();
                if (body == null || body.isBlank()) {
                    // TODO
                    break;
                }

                Message textMessage = new Message(this.username, message.getBody(), Instant.now());
                room.updateChatLog(textMessage);
                room.broadcast(textMessage);
                break;
            case MessageType.NAME_REQUEST:
                String username = message.getBody();
                if (username == null || username.isBlank()) {
                    // TODO
                    break;
                }
                // TODO check if name is available
                Message ack = new Message(MessageType.ACK);
                sendMessage(ack);
            default: // ignore
                break;
        }
    }
}
