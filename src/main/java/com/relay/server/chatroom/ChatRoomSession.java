package com.relay.server.chatroom;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.relay.protocol.Message;
import com.relay.server.chatroom.domain.ChatRoom;
import com.relay.server.net.ClientConnection;

/**
 * Represents an active chat room session.
 * 
 * Holds a connection to each participant of the chat room and
 * provides methods to broadcast messages to the participants.
 */
public class ChatRoomSession {
    private final ChatRoom room;
    private final Set<ClientConnection> connections = ConcurrentHashMap.newKeySet();

    public ChatRoomSession(ChatRoom room) {
        this.room = room;
    }

    public UUID getRoomID() {
        return room.getID();
    }

    public void add(ClientConnection connection) {
        connections.add(connection);
    }

    public void remove(ClientConnection connection) {
        connections.remove(connection);
    }

    public void broadcast(Message message) {
        connections.forEach(connection -> connection.sendMessage(message));
    }

    public void broadcastExcept(Message message, ClientConnection excludedConnection) {
        connections.stream()
            .filter(connection -> !connection.equals(excludedConnection))
            .forEach(connection -> connection.sendMessage(message));
    }
}
