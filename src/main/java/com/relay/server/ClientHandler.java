package com.relay.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final ChatRoom room;
    private final Socket socket;
    private final String name;
    private PrintWriter out;

    public ClientHandler(Socket socket, ChatRoom room, String name) {
        this.room = room;
        this.socket = socket;
        this.name = name;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        ) {
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            String line;

            // readLine returns null when the client closes the connection
            while ((line = in.readLine()) != null) {
                System.out.println("Received: " + line);
                room.updateChatLog(line);
                room.broadcast(line, this);
            }
            out.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            if (out != null) out.close();
        }
        System.out.println("Client disconnected.");
    }

    /**
     * Sends a message to the client associated with this handler.
     * 
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message, String sender) throws IOException {
        this.out.print("[" + sender + "]" + " > ");
        this.out.println(message);
    }

    public String getName() {
        return this.name;
    }
}
