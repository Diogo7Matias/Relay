package com.relay.server.user.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.relay.server.chatroom.domain.ChatRoom;

public class User {
    private final UUID id;
    private String username;
    private Set<ChatRoom> chatRooms;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
    }

    public User(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getID() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public Set<ChatRoom> getChatRooms() {
        return new HashSet<>(this.chatRooms);
    }

    public void setChatRooms(Set<ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }
}
