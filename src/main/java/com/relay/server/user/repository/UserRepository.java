package com.relay.server.user.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.relay.server.user.domain.User;

public class UserRepository {
    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(User user) {
        String sql = "INSERT INTO users (id, username) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getID().toString());
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to save user.\n" + e.getMessage());
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        
        String sql = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(UUID.fromString(rs.getString("id")), rs.getString("username")));
            }
        } catch (SQLException e) {
            System.err.println("Failed to fetch users.\n" + e.getMessage());
        }
        return users;
    }

    public Optional<User> findByName(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(UUID.fromString(rs.getString("id")), rs.getString("username"));
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.err.println("Failed to query user.\n" + e.getMessage());
            return Optional.empty();
        }
    }
}
