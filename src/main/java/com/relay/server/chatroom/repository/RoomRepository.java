package com.relay.server.chatroom.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.relay.server.chatroom.domain.ChatRoom;

public class RoomRepository {
    private final Connection connection;

    public RoomRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(ChatRoom room) {
        String sql = "INSERT INTO rooms (id) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, room.getID().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save room.\n" + e.getMessage());
        }
    }

    public List<ChatRoom> findAll() {
        List<ChatRoom> rooms = new ArrayList<>();
        
        String sql = "SELECT * FROM rooms";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(new ChatRoom(UUID.fromString(rs.getString("id"))));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch rooms.\n" + e.getMessage());
        }
        return rooms;
    }

    public Optional<ChatRoom> findByID(UUID id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ChatRoom room = new ChatRoom(UUID.fromString(rs.getString("id")));
                return Optional.of(room);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Failed to query chat room.\n" + e.getMessage());
            return Optional.empty();
        }
    }
}
