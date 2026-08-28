package com.relay.server;

import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relay.protocol.Message;
import com.relay.protocol.MessageAdapter;
import static com.relay.server.exceptions.ErrorMessage.USER_NAME_INVALID;
import com.relay.server.exceptions.RelayException;

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
    
    /**
     * An instance of Gson, used to serialize/deserialize messages.
     */
    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    public void addHandler(ClientHandler handler) {
        isNameUnique(handler.getUsername());
        this.handlers.add(handler);
    }

    public void broadcast(Message message) {
        for (ClientHandler h : handlers) {
            if (!h.getUsername().equals(message.getSender())) {
                String json = gson.toJson(message);
                h.sendMessage(json);
            }
        }
    }

    public void updateChatLog(Message message) {
        this.chatLog.add(message);
    }

    public void printLog() {
        System.out.println(chatLog.toString());
    }

    private void isNameUnique(String name) {
        for (ClientHandler h : handlers) {
            if (h.getUsername().equals(name)) {
                throw new RelayException(USER_NAME_INVALID);
            }
        }
    }
}
