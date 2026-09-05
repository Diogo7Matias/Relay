package com.relay.server.chatroom;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.relay.protocol.Message;
import com.relay.server.chatroom.domain.ChatRoom;
import com.relay.server.net.ClientConnection;
import com.relay.server.user.domain.User;

/**
 * Manages active client connections and their chat room sessions.
 */
public class ChatRoomSessionManager {
    private final Map<ClientConnection, ChatRoomSession> connectionSessions = new ConcurrentHashMap<>();
    private final Map<UUID, ClientConnection> userConnections = new ConcurrentHashMap<>();
    private final Map<UUID, ChatRoomSession> roomSessions = new ConcurrentHashMap<>();

    public void beginConnection(ClientConnection connection, User user) {
        userConnections.put(user.getID(), connection);
    }

    public void endConnection(ClientConnection connection, User user) {
        ChatRoomSession session = connectionSessions.remove(connection);
        if (session != null) {
            session.remove(connection);
        }
        userConnections.remove(user.getID());
    }

    public void join(ClientConnection connection, ChatRoom room) {
        ChatRoomSession previousSession = connectionSessions.get(connection);
        ChatRoomSession newSession = roomSessions.computeIfAbsent(room.getID(), id -> new ChatRoomSession(room));

        if (previousSession != null && previousSession != newSession) {
            previousSession.remove(connection);
        }

        newSession.add(connection);
        connectionSessions.put(connection, newSession);
    }

    public void leave(ClientConnection connection) {
        ChatRoomSession session = connectionSessions.remove(connection);
        if (session != null) {
            session.remove(connection);
        }
    }

    public UUID getUserSessionRoom(User user) {
        ClientConnection connection = userConnections.get(user.getID());
        return connection != null && connectionSessions.get(connection) != null 
            ? connectionSessions.get(connection).getRoomID()
            : null;
    }

    public void sendToUser(User user, Message message) {
        ClientConnection connection = userConnections.get(user.getID());
        if (connection != null) {
            connection.sendMessage(message);
        }
    }

    public void broadcast(ClientConnection sender, Message message) {
        ChatRoomSession session = connectionSessions.get(sender);
        if (session != null) {
            session.broadcast(message);
        }
    }

    public void broadcastExcept(Message message, ClientConnection excludedConnection) {
        ChatRoomSession session = connectionSessions.get(excludedConnection);
        if (session != null) {
            session.broadcastExcept(message, excludedConnection);
        }
    }
}
