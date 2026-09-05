package com.relay.server.textMessage.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.relay.server.textMessage.domain.TextMessage;

public class TextMessageRepository {
    private final Connection connection;

    public TextMessageRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(TextMessage message, UUID roomID) {
        String sql = "INSERT INTO messages (room_id, body, sender, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, roomID.toString());
            stmt.setString(2, message.getBody());
            stmt.setString(3, message.getSenderID().toString());
            stmt.setString(4, message.getTimestamp().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save message.\n" + e.getMessage());
        }
    }

    public List<TextMessage> findAllByRoomID(UUID roomID) {
        List<TextMessage> messages = new ArrayList<>();
        String id = roomID.toString();

        String sql = "SELECT * FROM messages WHERE room_id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new TextMessage(
                    rs.getString("body"),
                    UUID.fromString(rs.getString("sender")),
                    Instant.parse(rs.getString("timestamp"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch messages.\n" + e.getMessage());
        }
        return messages;
    }
}

