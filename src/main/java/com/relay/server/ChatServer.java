package com.relay.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import com.relay.server.chatroom.ChatRoomService;
import com.relay.server.chatroom.ChatRoomSessionManager;
import com.relay.server.chatroom.repository.RoomRepository;
import com.relay.server.net.ClientHandler;
import com.relay.server.textMessage.repository.TextMessageRepository;
import com.relay.server.user.UserService;
import com.relay.server.user.repository.UserRepository;

/**
 * The entry point for the server.
 * Accept client connections over blocking TCP sockets.
 */
public class ChatServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 100;

    public static void main(String[] args) throws IOException, SQLException {
        ConcurrentHashMap<ClientHandler, Thread> handlers = new ConcurrentHashMap<>();
        
        Database database = new Database("relay.db");
        RoomRepository roomRepository = new RoomRepository(database.getConnection());
        UserRepository userRepository = new UserRepository(database.getConnection());
        TextMessageRepository messageRepository = new TextMessageRepository(database.getConnection());
        
        ChatRoomService roomService = new ChatRoomService(roomRepository);
        UserService userService = new UserService(userRepository);
        ChatRoomSessionManager sessionManager = new ChatRoomSessionManager();
        
        setCleanUpRoutine(handlers);

        // accept connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {            
            int clientCount = 0;

            System.out.println("Server listening on port " + PORT + "...");

            // spawn a thread per client 
            while (clientCount < MAX_CLIENTS) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, roomService, sessionManager, userService);

                Thread thread = new Thread(handler);
                thread.start();
                handlers.put(handler, thread);
                
                System.out.println("Client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());                
                clientCount++;
            }

            System.out.println("Maximum number of clients reached. No longer accepting connections.");
        }
    }

    private static void setCleanUpRoutine(ConcurrentHashMap<ClientHandler, Thread> handlers) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
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
        }));
    }
}
