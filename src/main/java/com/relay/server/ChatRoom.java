package com.relay.server;

import java.util.concurrent.CopyOnWriteArrayList;

import com.relay.protocol.Message;
import static com.relay.server.exceptions.ErrorMessage.USER_NAME_INVALID;
import com.relay.server.exceptions.RelayException;
import com.relay.server.net.ClientHandler;

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
    private final CopyOnWriteArrayList<Message> chatLog = new CopyOnWriteArrayList<>();

    public void addHandler(ClientHandler handler) {
        this.handlers.add(handler);
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

    public void isNameUnique(String name) {
        for (ClientHandler h : handlers) {
            if (h.getUsername() != null && h.getUsername().equals(name)) {
                throw new RelayException(USER_NAME_INVALID);
            }
        }
    }
}
