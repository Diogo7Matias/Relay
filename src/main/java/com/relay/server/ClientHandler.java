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
            String line;

            // readLine returns null when the client closes the connection
            while ((line = in.readLine()) != null) {
                System.out.println("Received: " + line);

                Message message = new Message(this.username, line, Instant.now());
                room.updateChatLog(message);
                room.broadcast(message);
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
}
