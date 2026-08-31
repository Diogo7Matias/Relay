package com.relay.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import com.relay.server.net.ClientHandler;

/**
 * Accept client connections over blocking TCP sockets.
 */
public class ChatServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 10;

    public static void main(String[] args) throws IOException {
        ChatRoom room = new ChatRoom();
        ConcurrentHashMap<ClientHandler, Thread> handlers = new ConcurrentHashMap<>();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down server...");

            for (ClientHandler handler : handlers.keySet()) {
                handler.close();
            }

            for (Thread thread : handlers.values()) {
                try {
                    thread.join(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            System.err.println("Server shutdown complete.");
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {            
            int clientCount = 0;

            System.out.println("Server listening on port " + PORT + "...");

            // spawn a thread per client 
            while (clientCount < MAX_CLIENTS) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, room);
                room.addHandler(handler);

                Thread thread = new Thread(handler);
                thread.start();
                handlers.put(handler, thread);
                
                System.out.println("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());                
                clientCount++;
            }

            System.out.println("Maximum number of clients reached. No longer accepting connections.");
        }
    }
}
