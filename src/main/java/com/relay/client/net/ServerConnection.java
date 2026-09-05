package com.relay.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relay.client.model.ChatSummary;
import com.relay.client.util.Callbacks;
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
    private Consumer<ChatSummary> onChatCreated;

    private String username;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private final Map<UUID, Consumer<Message>> pendingRequests = new ConcurrentHashMap<>();

    public void setOnServerStatusChange(Consumer<Boolean> handler) {
        this.onServerStatusChange = handler;
    }

    public void setOnMessageReceived(Consumer<Message> handler) {
        this.onMessageReceived = handler;
    }
    
    public void setOnChatCreated(Consumer<ChatSummary> handler) {
        this.onChatCreated = handler;
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
     * This message is not a request so it will not wait for a response.
     * 
     * @param payload the message payload
     */
    public void sendTextMessage(String payload) {
        send(Message.builder(MessageType.TEXT).body(payload).build());
    }

    /**
     * Sends a request to the server.
     * 
     * @param request the request message
     * @param onResponse a callback function to execute when the response arrives
     */
    public void sendRequest(Message request, Consumer<Message> onResponse) {
        if (send(request)) {
            pendingRequests.put(request.getRequestID(), onResponse);
        }
    }

    /**
     * Sends the specified message to the server over the established socket.
     */
    private boolean send(Message message) {
        if (socket != null && !socket.isClosed()) {
            String jsonline = gson.toJson(message);
            serverOut.println(jsonline);
            return true;
        } else {
            System.err.println("Cannot contact server.");
            notifyServerStatusChange(false);
            return false;
        }
    }

    private void handleMessage(Message message) {
        UUID requestID = message.getRequestID();

        if (isPending(requestID)) {
            Consumer<Message> callback = pendingRequests.remove(requestID);
            callback.accept(message);
        } else {
            // messages that are not replies to client requests
            switch (message.getType()) {
                case TEXT -> notifyMessageReceived(message);
                case CHAT_CREATED -> notifyChatCreated(new ChatSummary(message.getBody(), message.getSender()));
                default -> System.err.println("Ignoring unknown message.");
            }
        }
    }

    private boolean isPending(UUID requestID) {
        return requestID != null && pendingRequests.containsKey(requestID);
    }

    /* -------------- Notify Methods -------------- */

    public void notifyServerStatusChange(Boolean isUp) {
        Callbacks.notify(onServerStatusChange, isUp, "onServerDown");
    }

    public void notifyMessageReceived(Message message) {
        Callbacks.notify(onMessageReceived, message, "onMessageReceived");
    }

    public void notifyChatCreated(ChatSummary chatSummary) {
        Callbacks.notify(onChatCreated, chatSummary, "onChatCreated");
    }
}
