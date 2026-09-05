package com.relay.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Database {
    private final Connection connection;

    public Database(String dbFilePath) throws SQLException, IOException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        initSchema();
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() throws SQLException, IOException {
        String schema = readResource("/com/relay/server/schema.sql");
        try (Statement stmt = connection.createStatement()) {
            for (String statement : schema.split(";")) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    private String readResource(String path) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Resource not found: " + path);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }

    public void clearAll() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM messages;");
            stmt.execute("DELETE FROM rooms;");
            stmt.execute("DELETE FROM users;");
        }
    }
}