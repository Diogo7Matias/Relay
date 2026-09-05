package com.relay.server.chatroom;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.relay.server.chatroom.domain.ChatRoom;
import com.relay.server.chatroom.repository.RoomRepository;
import static com.relay.server.exceptions.ErrorMessage.CHAT_ROOM_NOT_FOUND;
import com.relay.server.exceptions.RelayException;
import com.relay.server.user.domain.User;

/**
 * Responsible for executing chat room related operations.
 */
public class ChatRoomService {
    private final RoomRepository repository;
    private final Map<UUID, ChatRoom> activeRooms = new ConcurrentHashMap<>();

    public ChatRoomService(RoomRepository repository) {
        this.repository = repository;
    }

    public ChatRoom createRoom(User user, User otherUser) {
        if (user.equals(otherUser)) {
            return null; // ignore
        }

        ChatRoom newRoom = new ChatRoom();
        newRoom.addParticipant(user);
        newRoom.addParticipant(otherUser);

        repository.save(newRoom);
        activeRooms.put(newRoom.getID(), newRoom);
        return newRoom;
    }

    public List<ChatRoom> getAllChatRooms() {
        return repository.findAll();
    }

    public ChatRoom getRoom(UUID roomID) {
        return activeRooms.computeIfAbsent(roomID, id ->
            repository.findByID(id).orElseThrow(() -> new RelayException(CHAT_ROOM_NOT_FOUND))
        );
    }
}
