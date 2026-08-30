package com.relay.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Accept client connections over blocking TCP sockets.
 */
public class ChatServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 2;

    public static void main(String[] args) throws IOException {
        ChatRoom room = new ChatRoom();
        Integer clientCount = 0;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT + "...");

            // spawn a thread per client 
            while (clientCount < MAX_CLIENTS) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, room);
                room.addHandler(handler);

                Thread thread = new Thread(handler);
                thread.start();
                
                System.out.println("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());                
                clientCount++;
            }

            System.out.println("Maximum number of clients reached. Server is no longer accepting connections.");
        }
    }
}
