package com.relay.server;

import java.util.concurrent.CopyOnWriteArrayList;

import com.relay.protocol.Message;
import com.relay.server.net.ClientHandler;

public class ChatRoom {

    private static long idCount = 0;
    private final String id;

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
    private final CopyOnWriteArrayList<Message> chatLog = new CopyOnWriteArrayList<>();

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

    public void updateChatLog(Message message) {
        this.chatLog.add(message);
    }

    public void printLog() {
        System.out.println(chatLog.toString());
    }
}
