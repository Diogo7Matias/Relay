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

    public void setOnMessageReceived(Consumer<Message> handler) {
        this.onMessageReceived = handler;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(HOST, PORT);
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOut = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server. Type a message and press Enter.");
        } catch (IOException e) {
            System.err.println("Could not connect to server.\n" + e.getMessage());
            closeConnection();
            return;
        }

        // await messages from the server
        String jsonLine;
        try {
            while ((jsonLine = serverIn.readLine()) != null) {
                Message newMessage = gson.fromJson(jsonLine, Message.class);
                if (onMessageReceived != null) {
                    onMessageReceived.accept(newMessage);
                } else {
                    System.err.println("onMessageReceived not specified.");
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch message from server.\n" + e.getMessage());
        }
        closeConnection();
    }

    /**
     * Sends a message body (its content) to the server.
     * 
     * @param messageBody the message content
     */
    public void send(String messageBody) {
        if (socket != null && !socket.isClosed()) {
            serverOut.println(messageBody);
        } else {
            System.err.println("Cannot contact server.");
        }
    }

    /**
     * Closes the socket used to contact the server if it is open.
     */
    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while closing connection.\n" + e.getMessage());
        }
    }
}
