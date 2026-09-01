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

    public void broadcast(Message message) {
        for (ClientHandler h : handlers) {
            h.sendMessage(message);
        }
    }
}
