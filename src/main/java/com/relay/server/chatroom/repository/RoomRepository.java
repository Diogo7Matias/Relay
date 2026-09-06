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
import com.relay.server.user.domain.User;

public class RoomRepository {
    private final Connection connection;

    public RoomRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(ChatRoom room, User user, User otherUser) {
        String sql1 = "INSERT INTO rooms (id) VALUES (?)";
        String sql2 = "INSERT INTO room_users (room_id, user_id) VALUES (?, ?)";
        try (
            PreparedStatement stmt1 = connection.prepareStatement(sql1);
            PreparedStatement stmt2 = connection.prepareStatement(sql2)
        ) {
            stmt1.setString(1, room.getID().toString());
            stmt1.executeUpdate();

            stmt2.setString(1, room.getID().toString());
            stmt2.setString(2, user.getID().toString());
            stmt2.executeUpdate();
            stmt2.setString(1, room.getID().toString());
            stmt2.setString(2, otherUser.getID().toString());
            stmt2.executeUpdate();
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
    
    public List<ChatRoom> findAllByUserID(UUID userID) {
        List<ChatRoom> rooms = new ArrayList<>();
        
        String sql = """
                SELECT * FROM rooms r
                JOIN room_users ru
                ON r.id = ru.room_id
                WHERE ru.user_id = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userID.toString());

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
