package com.relay.server;

import java.io.IOException;
import java.util.ArrayList;

public class ChatRoom {
    private final ArrayList<String> chatLog = new ArrayList<>();
    private final ArrayList<ClientHandler> handlers = new ArrayList<>();
    
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
