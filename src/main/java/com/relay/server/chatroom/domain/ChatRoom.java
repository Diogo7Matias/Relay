package com.relay.server.chatroom.domain;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.relay.server.user.domain.User;

public class ChatRoom {
    private final UUID id;
    private Set<User> participants = ConcurrentHashMap.newKeySet();

    public ChatRoom() {
        this.id = UUID.randomUUID();
    }

    public ChatRoom(UUID id) {
        this.id = id;
    }

    public UUID getID() {
        return this.id;
    }

    public void addParticipant(User user) {
        participants.add(user);
    }

    public void removeParticipant(User user) {
        participants.remove(user);
    }
}

