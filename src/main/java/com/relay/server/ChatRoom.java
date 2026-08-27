package com.relay.server;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoom {

    /**
     * A list of handlers for the clients participating in the room.
     * It's implemented as a CopyOnWriteArrayList because it needs to be thread-safe
     * and most accesses are iterations rather than list updates/modification.
     * Worth keeping in mind for future optimization.
     */
    private final CopyOnWriteArrayList<ClientHandler> handlers = new CopyOnWriteArrayList<>();
    
    /**
     * Contains all the messages sent by clients of the room.
     * Currently it serves no real purpose and will probably be removed or changed
     * when I introduce a database.
     * This too needs to be thread-safe.
     */
    private final CopyOnWriteArrayList<String> chatLog = new CopyOnWriteArrayList<>();
    
    public void addHandler(ClientHandler handler) {
        this.handlers.add(handler);
    }

    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler h : handlers) {
            if (h == sender) continue;
            try {
                h.sendMessage(message, sender.getName());
            } catch (IOException e) {
                System.err.println("Failed to send message.\n" + e.getMessage());
            }
        }
    }

    public void updateChatLog(String message) {
        this.chatLog.add(message);
    }

    public void printLog() {
        System.out.println(chatLog.toString());
    }
}
