package com.relay.server.chatroom;

import java.util.List;
import java.util.UUID;

import com.relay.server.chatroom.domain.ChatRoom;
import com.relay.server.chatroom.repository.RoomRepository;
import static com.relay.server.exceptions.ErrorMessage.CHAT_ROOM_NOT_FOUND;
import com.relay.server.exceptions.RelayException;
import com.relay.server.user.domain.User;

/**
 * Responsible for managing chat rooms.
 */
public class ChatRoomService {
    private final RoomRepository repository;

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
        return newRoom;
    }

    public List<ChatRoom> getAllChatRooms() {
        return repository.findAll();
    }

    public ChatRoom getRoom(UUID roomID) {
        return repository.findByID(roomID).orElseThrow(() -> new RelayException(CHAT_ROOM_NOT_FOUND));
    }
}
