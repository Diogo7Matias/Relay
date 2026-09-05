package com.relay.server.textMessage.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.relay.protocol.Message;

public class TextMessageRepository {
    private final Connection connection;

    public TextMessageRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Message message) {
        String sql = "INSERT INTO messages (request_id, body, sender, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, message.getRequestID().toString());
            stmt.setString(2, message.getBody());
            stmt.setString(3, message.getSender());
            stmt.setString(4, message.getTimestamp().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save message.\n" + e.getMessage());
        }
    }
}

