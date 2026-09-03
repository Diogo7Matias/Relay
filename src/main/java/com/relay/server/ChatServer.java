package com.relay.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.relay.server.exceptions.ErrorMessage.USERNAME_ALREADY_EXISTS;
import static com.relay.server.exceptions.ErrorMessage.USER_NOT_FOUND;
import com.relay.server.exceptions.RelayException;
import com.relay.server.net.ClientHandler;

/**
 * Accept client connections over blocking TCP sockets.
 */
public class ChatServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 10;

    private final ConcurrentHashMap<ClientHandler, Thread> handlers = new ConcurrentHashMap<>();
    private final Set<ChatRoom> rooms = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();

        // cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (ClientHandler handler : chatServer.handlers.keySet()) {
                handler.close();
            }

            for (Thread thread : chatServer.handlers.values()) {
                try {
                    thread.join(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }));

        // accept connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {            
            int clientCount = 0;

            System.out.println("Server listening on port " + PORT + "...");

            // spawn a thread per client 
            while (clientCount < MAX_CLIENTS) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, chatServer);

                Thread thread = new Thread(handler);
                thread.start();
                chatServer.handlers.put(handler, thread);
                
                System.out.println("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());                
                clientCount++;
            }

            System.out.println("Maximum number of clients reached. No longer accepting connections.");
        }
    }

    public ChatRoom newRoom(ClientHandler requester, String otherUser) {
        ClientHandler otherUserHandler = null;

        for (ClientHandler h : handlers.keySet()) {
            if (h.getUsername() != null && h.getUsername().equals(otherUser)) {
                otherUserHandler = h;
            }
        }

        if (otherUserHandler == null) {
            throw new RelayException(USER_NOT_FOUND);
        }

        if (otherUserHandler.equals(requester)) {
            return null; // ignore
        }

        ChatRoom newRoom = new ChatRoom();
        newRoom.addHandler(otherUserHandler);
        newRoom.addHandler(requester);

        this.rooms.add(newRoom);
        return newRoom;
    }

    public void joinChatRoom(ClientHandler requester, String roomID) {
        for (ChatRoom room : rooms) {
            if (requester.hadRoom() && room.getID().equals(requester.getRoomID())) {
                if (room.getID().equals(roomID)) { 
                    break; // client is trying to join the room they are already in
                }
                room.removeHandler(requester);
            }
            if (room.getID().equals(roomID)) {
                room.addHandler(requester);
            }
        }
    }

    public void isNameUnique(String name) {
        for (ClientHandler h : handlers.keySet()) {
            if (h.getUsername() != null && h.getUsername().equals(name)) {
                throw new RelayException(USERNAME_ALREADY_EXISTS);
            }
        }
    }
}
