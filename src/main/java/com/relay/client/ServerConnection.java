package com.relay.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relay.protocol.Message;
import com.relay.protocol.MessageType;
import com.relay.protocol.MessageAdapter;

public class ServerConnection implements Runnable {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    private Socket socket;
    private BufferedReader serverIn;
    private PrintWriter serverOut;

    private Consumer<Message> onMessageReceived;
    private Consumer<Message> onUsernameChosen;

    public void setOnMessageReceived(Consumer<Message> handler) {
        this.onMessageReceived = handler;
    }

    public void setOnUsernameChosen(Consumer<Message> handler) {
        this.onUsernameChosen = handler;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(HOST, PORT);
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOut = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Could not connect to server.\n" + e.getMessage());
            closeConnection();
            return;
        }

        // await messages from the server
        String jsonLine;
        try {
            while ((jsonLine = serverIn.readLine()) != null) {
                Message incomingMessage = gson.fromJson(jsonLine, Message.class);
                handleMessage(incomingMessage);
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch message from server.\n" + e.getMessage());
        }
        closeConnection();
    }

    /**
     * Sends a message containing messageBody to the server.
     * 
     * @param messageBody the message content
     */
    public void send(String messageBody) {
        if (socket != null && !socket.isClosed()) {
            Message message = new Message(messageBody, MessageType.TEXT);
            String jsonline = gson.toJson(message);
            serverOut.println(jsonline);
        } else {
            System.err.println("Cannot contact server.");
        }
    }

    /**
     * Requests a username from the server to identify this client.
     * 
     * @param username the username being requested
     */
    public void requestUsername(String username) {
        if (socket != null && !socket.isClosed()) {
            Message request = new Message(username, MessageType.NAME_REQUEST);
            String jsonline = gson.toJson(request);
            serverOut.println(jsonline);
        } else {
            System.err.println("Cannot contact server.");
        }
    }

    private void handleMessage(Message message) {
        MessageType type = message.getType();
        if (type == null) {
            // TODO
            return;
        }

        switch (type) {
            case MessageType.TEXT:
                if (onMessageReceived != null) {
                    onMessageReceived.accept(message);
                } else {
                    System.err.println("onMessageReceived not specified.");
                }
                break;
            case MessageType.ACK:
                // if (state == ...) // implement state pattern ?
                if (onUsernameChosen != null) {
                    onUsernameChosen.accept(message);
                } else {
                    System.err.println("onUsernameChosen not specified.");
                }
                break;
        
            default: // ignore
                break;
        }
    }

    /**
     * Closes the socket used to contact the server if it is open.
     */
    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while closing connection.\n" + e.getMessage());
        }
    }
}
