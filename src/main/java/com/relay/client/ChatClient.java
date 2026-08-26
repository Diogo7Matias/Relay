package com.relay.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Connect to the server, send messages on stdin,
 * print whatever comes back.
 */
public class ChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Connected to server. Type a message and press Enter.");
            
            // handle the reception of messages from the server
            Thread receiverThread = new Thread(() -> {
                String response;
                try {
                    while ((response = serverIn.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to fetch message from server.\n" + e.getMessage());
                }
            });
            receiverThread.start();
            
            String line;
            do { 
                System.out.print("[YOU] > ");
                if ((line = userIn.readLine()) == null) break;
                serverOut.println(line);
            } while (true);
        } catch (IOException e) {
            System.err.println("Could not connect to server.\n" + e.getMessage());
        }
    }
}
