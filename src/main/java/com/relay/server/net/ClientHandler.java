package com.relay.server.net;

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
import com.relay.protocol.MessageType;
import com.relay.server.ChatRoom;
import static com.relay.server.exceptions.ErrorMessage.MESSAGE_FORMAT_INVALID;
import com.relay.server.exceptions.RelayException;
import com.relay.server.net.state.ClientState;
import com.relay.server.net.state.HandshakeState;

public class ClientHandler implements Runnable {
    private final ChatRoom room;
    private final Socket socket;
    private String username;

    private PrintWriter out;
    private ClientState state;

    /**
     * An instance of Gson, used to serialize/deserialize messages.
     */
    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    public ClientHandler(Socket socket, ChatRoom room) {
        this.state = new HandshakeState();
        this.room = room;
        this.socket = socket;
    }

    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        room.isNameUnique(username);
        this.username = username;
    }

    public void setState(ClientState state) {
        this.state = state;
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
                
                try {
                    handleMessage(message);
                } catch (RelayException e) {
                    Message errorMsg = new Message(e.getErrorMessage().label);
                    sendMessage(errorMsg);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Client disconnected.");
        if (this.out != null) this.out.close();
    }

    private void handleMessage(Message message) {
        if (message.getType() == null) {
            throw new RelayException(MESSAGE_FORMAT_INVALID);
        }
        state.handleMessage(this, message);
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

    public void procesTextMessage(String messageBody) {
        Message textMessage = new Message(this.username, messageBody, Instant.now());
        room.updateChatLog(textMessage);
        room.broadcast(textMessage);
    }

    public void processUsernameRequest(String username) {
        setUsername(username);
        Message ack = new Message(MessageType.ACK);
        sendMessage(ack);
    }
}
