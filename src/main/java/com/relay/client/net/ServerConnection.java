package com.relay.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relay.client.net.state.ConnectionState;
import com.relay.client.net.state.HandshakeState;
import com.relay.protocol.Message;
import com.relay.protocol.MessageAdapter;
import com.relay.protocol.MessageType;

public class ServerConnection implements Runnable {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    private Socket socket;
    private BufferedReader serverIn;
    private PrintWriter serverOut;

    private Consumer<Boolean> onServerStatusChange;
    private Consumer<Message> onMessageReceived;
    private Consumer<Message> onErrorReceived;
    private Consumer<Message> onNewChat;
    private Consumer<String> onJoinChat;
    private Runnable onUsernameChosen;

    private ConnectionState state = new HandshakeState();

    public void setState(ConnectionState state) {
        this.state = state;
    }

    public void setOnServerStatusChange(Consumer<Boolean> handler) {
        this.onServerStatusChange = handler;
    }

    public void setOnMessageReceived(Consumer<Message> handler) {
        this.onMessageReceived = handler;
    }

    public void setOnErrorReceived(Consumer<Message> handler) {
        this.onErrorReceived = handler;
    }

    public void setOnNewChat(Consumer<Message> handler) {
        this.onNewChat = handler;
    }

    public void setOnJoinChaat(Consumer<String> handler) {
        this.onJoinChat = handler;
    }

    public void setOnUsernameChosen(Runnable handler) {
        this.onUsernameChosen = handler;
    }

    /**
     * Awaits new messages from the server.
     * If there is no connection established, no action is performed.
     */
    @Override
    public void run() {
        if (socket == null || socket.isClosed()) {
            return;
        }

        String jsonLine;
        try {
            // await messages from the server
            while ((jsonLine = serverIn.readLine()) != null) {
                System.out.println("Received: " + jsonLine);
                Message incomingMessage = gson.fromJson(jsonLine, Message.class);
                handleMessage(incomingMessage);
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch message from server: " + e.getMessage());
        } finally {
            notifyServerStatusChange(false);
            closeConnection();
        }
    }

    /**
     * Attempts to create a socket and connect to the server.
     * 
     * @return whether the connection was successful or not
     */
    public boolean connect() {
        try {
            socket = new Socket(HOST, PORT);
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOut = new PrintWriter(socket.getOutputStream(), true);
            notifyServerStatusChange(true);
            return true;
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            notifyServerStatusChange(false);
            closeConnection();
            return false;
        }
    }
    
    /**
     * Closes the socket used to contact the server if it is open.
     */
    public void closeConnection() {
        setState(new HandshakeState());
        try {
            System.out.println("Closing connection...");
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while closing connection: " + e.getMessage());
        }
    }

    public void loadChatHistory(String chatID) {
        // TODO: fetch from server
    }

    /**
     * Sends a text message to the server.
     * 
     * @param payload the message payload
     */
    public void sendTextMessage(String payload) {
        send(new Message(payload, MessageType.TEXT));
    }

    /**
     * Requests a username from the server to identify this client.
     * 
     * @param username the username being requested
     */
    public void requestUsername(String username) {
        send(new Message(username, MessageType.NAME_REQUEST));
    }

    /**
     * Asks the server to create a new chat with the user specified.
     * 
     * @param otherUser the other user's name
     */
    public void createNewChat(String otherUser) {
        send(new Message(otherUser, MessageType.NEW_CHAT_REQUEST));
    }

    /**
     * Sends a request to the server asking to join a chat room.
     * 
     * @param chatID the ID of the chat room
     */
    public void joinChat(String chatID) {
        send(new Message(chatID, MessageType.JOIN_CHAT_REQUEST));
    }

    /**
     * Sends the specified message to the server over the established socket.
     */
    private void send(Message message) {
        if (socket != null && !socket.isClosed()) {
            String jsonline = gson.toJson(message);
            serverOut.println(jsonline);
        } else {
            System.err.println("Cannot contact server.");
            notifyServerStatusChange(false);
        }
    }

    private void handleMessage(Message message) {
        if (message.getType() == null) { // should not happen
            System.err.println("Invalid message format from server.");
            return;
        }
        state.handleMessage(this, message);
    }

    /* -------------- Notify Methods -------------- */

    public void notifyServerStatusChange(Boolean isUp) {
        notify(onServerStatusChange, isUp, "onServerDown");
    }

    public void notifyMessageReceived(Message message) {
        notify(onMessageReceived, message, "onMessageReceived");
    }

    public void notifyErrorReceived(Message message) {
        notify(onErrorReceived, message, "onErrorReceived");
    }

    public void notifyNewChat(Message message) {
        notify(onNewChat, message, "onNewChat");
    }

    public void notifyJoinChat(String chatID) {
        notify(onJoinChat, chatID, "onJoinChat");
    }

    public void notifyUsernameChosen() {
        notify(onUsernameChosen, "onUsernameChosen");
    }

    /* helper method to notify a consumer */
    private <T> void notify(Consumer<T> handler, T arg, String handlerName) {
        if (handler != null) {
            handler.accept(arg);
        } else {
            System.err.println(handlerName + " not specified.");
        }
    }

    /* helper method to notify a runnable */
    private void notify(Runnable handler, String handlerName) {
        if (handler != null) {
            handler.run();
        } else {
            System.err.println(handlerName + " not specified.");
        }
    }
}
