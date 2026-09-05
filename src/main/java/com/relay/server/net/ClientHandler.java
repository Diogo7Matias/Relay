package com.relay.server.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relay.protocol.Message;
import com.relay.protocol.MessageAdapter;
import com.relay.protocol.MessageType;
import com.relay.server.chatroom.ChatRoomService;
import com.relay.server.chatroom.ChatRoomSessionManager;
import com.relay.server.chatroom.domain.ChatRoom;
import static com.relay.server.exceptions.ErrorMessage.MESSAGE_FORMAT_INVALID;
import static com.relay.server.exceptions.ErrorMessage.USER_NOT_FOUND;
import com.relay.server.exceptions.RelayException;
import com.relay.server.net.state.ClientState;
import com.relay.server.net.state.HandshakeState;
import com.relay.server.user.UserService;
import com.relay.server.user.domain.User;

/**
 * Handles communication with a single client.
 * 
 * Receives messages from the client and processes them.
 * Can also be used as a ClientConnection to send messages to the client.
 */
public class ClientHandler implements Runnable, ClientConnection {
    private final Socket socket;
    private final ChatRoomSessionManager sessionManager;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    
    private ClientState state;
    private User user;
    
    private PrintWriter out;
    
    // An instance of Gson, used to serialize/deserialize messages.
    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();

    public ClientHandler(Socket socket, ChatRoomService roomService,
            ChatRoomSessionManager sessionManager, UserService userService) {
        this.socket = socket;
        this.sessionManager = sessionManager;
        this.chatRoomService = roomService;
        this.userService = userService;
        this.state = new HandshakeState();
    }
    
    private void setUser(String username) {
        Optional<User> found = userService.findUser(username);
        this.user = found.isPresent()
            ? new User(found.get().getID(), username)
            : new User(username);
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    /**
     * Sends a message to the client associated with this handler.
     * 
     * The message is converted to a string formatted as a JSON object
     * and sent over to the client.
     * 
     * @param message the message to be sent
     */
    @Override
    public void sendMessage(Message message) {
        String json = gson.toJson(message);
        this.out.println(json);
    }

    /**
     * Awaits client requests.
     */
    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        ) {
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            String jsonLine;

            // readLine returns null when the client closes the connection
            while ((jsonLine = in.readLine()) != null) {
                System.out.println("Received: " + jsonLine);
                Message message = gson.fromJson(jsonLine, Message.class);
                
                try {
                    handleMessage(message);
                } catch (RelayException e) {
                    sendMessage(Message.builder(MessageType.ERROR)
                        .requestID(message.getRequestID())
                        .body(e.getErrorMessage().label)
                        .build());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("Client disconnected.");
            sessionManager.endConnection(this, this.user);
            close();
        }
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while closing server: " + e.getMessage());
        }
    }

    private void handleMessage(Message message) {
        if (message.getType() == null) {
            throw new RelayException(MESSAGE_FORMAT_INVALID);
        }
        state.handleMessage(this, message);
    }

    /* -vvvvvv- Methods called by the state machine -vvvvvv- */

    public void processTextMessage(String messageBody) {
        Message message = Message.builder(MessageType.TEXT)
            .body(messageBody)
            .sender(this.user.getUsername())
            .timestamp(Instant.now())
            .build();
        sessionManager.broadcast(this, message);
    }

    public void processUsernameRequest(UUID requestID, String username) {
        userService.createUser(username);
        setUser(username);
        sessionManager.beginConnection(this, this.user);

        Message ack = Message.builder(MessageType.ACK)
            .requestID(requestID)
            .build();
        sendMessage(ack);
    }

    public void processNewChatRequest(UUID requestID, String otherUserName) {
        User otherUser = userService.findUser(otherUserName).orElseThrow(() -> new RelayException(USER_NOT_FOUND));
        ChatRoom newRoom = chatRoomService.createRoom(this.user, otherUser);
        
        if (newRoom != null) {
            Message ack = Message.builder(MessageType.ACK)
                .requestID(requestID)
                .body(newRoom.getID().toString())
                .build();
            Message chatCreated = Message.builder(MessageType.CHAT_CREATED)
                .body(newRoom.getID().toString())
                .sender(this.user.getUsername())
                .build();
            sendMessage(ack);
            sessionManager.sendToUser(otherUser, chatCreated);
        }
    }

    public void processJoinChatRoom(UUID requestID, UUID roomID) {
        sessionManager.join(this, chatRoomService.getRoom(roomID));
        Message ack = Message.builder(MessageType.ACK)
                .requestID(requestID)
                .build();
        sendMessage(ack);
    }
}
