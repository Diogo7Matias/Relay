package com.relay.server;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.relay.protocol.Message;
import com.relay.server.net.ClientHandler;

public class ChatRoom {

    private static long idCount = 0;
    private final String id;

    /**
     * A list of handlers for the clients participating in the room.
     */
    private final Set<ClientHandler> handlers = ConcurrentHashMap.newKeySet();
    
    public ChatRoom() {
        this.id = "ROOM#" + idCount++;
    }

    public String getID() {
        return this.id;
    }

    public void addHandler(ClientHandler handler) {
        this.handlers.add(handler);
        handler.setChatRoom(this);
    }

    public void removeHandler(ClientHandler handler) {
        this.handlers.remove(handler);
        handler.setChatRoom(null);
    }

    /**
     * Broadcast a message to all clients participating in this chat room.
     * 
     * @param message the message to be sent
     */
    public void broadcast(Message message) {
        for (ClientHandler h : handlers) {
            h.sendMessage(message);
        }
    }

    /**
     * Broadcast a message to all clients participating in this chat room
     * except the client specified.
     * 
     * @param message the message to be sent
     * @param handler the client that will not receive the message
     */
    public void broadcastExcept(Message message, ClientHandler handler) {
        for (ClientHandler h : handlers) {
            if (!h.equals(handler)) {
                h.sendMessage(message);
            }
        }
    }
}
