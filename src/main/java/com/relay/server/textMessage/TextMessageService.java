package com.relay.server.textMessage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.relay.server.textMessage.domain.TextMessage;
import com.relay.server.textMessage.repository.TextMessageRepository;

/**
 * Responsible for executing text message related operations.
 */
public class TextMessageService {
    private final TextMessageRepository repository;

    public TextMessageService(TextMessageRepository repository) {
        this.repository = repository;
    }

    public TextMessage createMessage(UUID roomID, String body, UUID senderID, Instant timestamp) {
        TextMessage msg = new TextMessage(body, senderID, timestamp);
        repository.save(msg, roomID);
        return msg;
    }

    public List<TextMessage> findAllChatRoomMessages(UUID roomID) {
        return repository.findAllByRoomID(roomID);
    }
}

